package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import com.pvptweaks.sound.Mp3Converter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundPickerScreen extends Screen {

    // All IDs stored with FULL namespace so Identifier.tryParse() works.
    private static final String[][] PRESETS = {
        {"Firework Blast",       "minecraft:entity.firework_rocket.blast"      },
        {"Firework Large Blast", "minecraft:entity.firework_rocket.large_blast" },
        {"Firework Twinkle",     "minecraft:entity.firework_rocket.twinkle"     },
        {"Lightning Thunder",    "minecraft:entity.lightning_bolt.thunder"      },
        {"Ender Dragon Growl",   "minecraft:entity.ender_dragon.growl"          },
        {"Ender Dragon Death",   "minecraft:entity.ender_dragon.death"          },
        {"Wither Spawn",         "minecraft:entity.wither.spawn"                },
        {"Warden Sonic Boom",    "minecraft:entity.warden.sonic_boom"           },
        {"Elder Guardian Curse", "minecraft:entity.elder_guardian.curse"        },
        {"Toast Challenge",      "minecraft:ui.toast.challenge_complete"        },
        {"Bell Ring",            "minecraft:block.bell.use"                     },
        {"Anvil Land",           "minecraft:block.anvil.land"                   },
        {"Explosion",            "minecraft:entity.generic.explode"             },
    };

    private final Screen parent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;

    private TextFieldWidget searchField;
    private List<String> allSounds;
    private List<String> filtered;
    // Entries: "pvptweaks:custom/safeId" for .ogg, "convert:safeId|absPath" for MP3/WAV etc.
    private List<String> customFiles = new ArrayList<>();
    private int scrollOffset = 0;
    private String selected  = "";
    private String statusMsg = "";
    private int tab = 0; // 0=recommended, 1=all MC, 2=my sounds

    private static final int ROW_H  = 14;
    private static final int LIST_Y = 70;

    // ARGB colors — 0xFF prefix required in MC 1.21.6+
    private static final int COL_WHITE  = 0xFFFFFFFF;
    private static final int COL_GREY   = 0xFFAAAAAA;
    private static final int COL_YELLOW = 0xFFFFFF55;
    private static final int COL_GREEN  = 0xFF55FF55;

    public SoundPickerScreen(Screen parent, SoundProfile profile, String label, Runnable onSave) {
        super(Text.literal("Sound Picker \u2014 " + label));
        this.parent   = parent;
        this.profile  = profile;
        this.label    = label;
        this.onSave   = onSave;
        this.selected = profile.isPreset() ? profile.presetId : "";
    }

    @Override
    protected void init() {
        allSounds = Registries.SOUND_EVENT.getIds().stream()
            .map(id -> id.getNamespace() + ":" + id.getPath())
            .sorted().collect(Collectors.toList());
        filtered = new ArrayList<>(allSounds);
        refreshCustomFiles();

        searchField = new TextFieldWidget(textRenderer, width / 2 - 150, 48, 300, 16, Text.empty());
        searchField.setPlaceholder(Text.literal("Search sounds..."));
        searchField.setChangedListener(q -> {
            filtered = allSounds.stream()
                .filter(s -> s.contains(q.toLowerCase()))
                .collect(Collectors.toList());
            scrollOffset = 0;
        });
        addDrawableChild(searchField);

        addDrawableChild(ButtonWidget.builder(Text.literal("\u2605 Recommended"),
            b -> { tab = 0; scrollOffset = 0; }).dimensions(4, 26, 140, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("All MC Sounds"),
            b -> { tab = 1; scrollOffset = 0; }).dimensions(148, 26, 120, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc2 My Sounds"),
            b -> { tab = 2; scrollOffset = 0; refreshCustomFiles(); }).dimensions(272, 26, 110, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc1 Open Folder"),
            b -> openSoundsFolder()).dimensions(width - 130, 26, 126, 18).build());

        // Bottom buttons
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2714 Use Vanilla"),
            b -> {
                profile.mode = "default";
                profile.presetId = "";
                profile.customPath = "";
                onSave.run();
                client.setScreen(parent);
            }).dimensions(4, height - 24, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("\u2714 Save Selected"),
            b -> saveSelected()).dimensions(width / 2 - 55, height - 24, 110, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("\u2716 Cancel"),
            b -> client.setScreen(parent)).dimensions(width - 84, height - 24, 80, 20).build());
    }

    private void saveSelected() {
        if (selected.isBlank()) { onSave.run(); client.setScreen(parent); return; }

        if (selected.startsWith("convert:")) {
            // MP3/WAV/etc — convert now, then register as preset
            String rest    = selected.substring("convert:".length());
            int    sep     = rest.indexOf('|');
            String absPath = rest.substring(sep + 1);

            statusMsg = "\u00a7eConverting... please wait";
            // Run conversion on a background thread to avoid freezing the UI
            Thread t = new Thread(() -> {
                com.pvptweaks.sound.CustomSoundManager.registerCustomSound(absPath);
                // Build the pvptweaks id from the safe name
                java.nio.file.Path src = java.nio.file.Paths.get(absPath);
                String fn   = src.getFileName().toString();
                String base = fn.contains(".") ? fn.substring(0, fn.lastIndexOf('.')) : fn;
                String sid  = base.toLowerCase().replaceAll("[^a-z0-9_]", "_");
                String pvpId = "pvptweaks:custom/" + sid;
                profile.mode = "preset";
                profile.presetId = pvpId;
                profile.customPath = "";
                onSave.run();
            }, "pvptweaks-convert");
            t.setDaemon(true);
            t.start();
            client.setScreen(parent);
            return;
        }

        // Preset (vanilla or pvptweaks:custom/xxx already converted)
        profile.mode = "preset";
        profile.presetId = selected;
        profile.customPath = "";
        onSave.run();
        client.setScreen(parent);
    }

    private void refreshCustomFiles() {
        customFiles.clear();
        try {
            Path dir = PvpTweaksConfig.SOUNDS_DIR;
            if (Files.exists(dir)) {
                Files.list(dir)
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase();
                        return n.endsWith(".ogg") || Mp3Converter.needsConversion(n);
                    })
                    .sorted()
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        if (name.toLowerCase().endsWith(".ogg")) {
                            String key = name.substring(0, name.length() - 4);
                            customFiles.add("pvptweaks:custom/" + key);
                        } else {
                            // MP3/WAV/etc — mark for conversion
                            String base = name.contains(".")
                                ? name.substring(0, name.lastIndexOf('.')) : name;
                            String safeId = base.toLowerCase().replaceAll("[^a-z0-9_]", "_");
                            customFiles.add("convert:" + safeId + "|" + p.toAbsolutePath());
                        }
                    });
            }
        } catch (IOException ignored) {}

        if (customFiles.isEmpty()) {
            statusMsg = "\u00a77No audio files found. Click Open Folder and add .ogg or .mp3 files.";
        } else {
            boolean hasMp3 = customFiles.stream().anyMatch(s -> s.startsWith("convert:"));
            statusMsg = hasMp3
                ? "\u00a7e\u26a0 .mp3/.wav files will be converted when you Save"
                : "";
        }
    }

    private void openSoundsFolder() {
        try {
            Path dir = PvpTweaksConfig.SOUNDS_DIR;
            Files.createDirectories(dir);
            Runtime.getRuntime().exec(new String[]{"xdg-open", dir.toString()});
            statusMsg = "\u00a7aOpened: " + dir;
        } catch (IOException e) {
            statusMsg = "\u00a7cFailed to open folder";
        }
    }

    /**
     * Preview a sound.
     *
     * BUG FIX: Registries.SOUND_EVENT.get() only returns vanilla MC sounds.
     * For pvptweaks:custom/* sounds, it returns null → NPE → silent failure.
     * Fix: fall back to SoundEvent.createVariableRangeEvent() for non-vanilla IDs.
     * SoundManager.play() then looks up the WeightedSoundSet by ID from sounds.json,
     * which our PvpTweaksDynamicPack provides via findResources().
     */
    private void previewSound(String soundId) {
        if (soundId.startsWith("convert:")) {
            statusMsg = "\u00a7eSave first to convert, then preview";
            return;
        }
        try {
            Identifier id = Identifier.tryParse(soundId);
            if (id == null) { statusMsg = "\u00a7cInvalid ID: " + soundId; return; }

            // Try vanilla registry first
            SoundEvent ev = Registries.SOUND_EVENT.get(id);
            if (ev == null) {
                // For pvptweaks:custom/* or other non-vanilla sounds,
                // create a SoundEvent on the fly. SoundManager will look up
                // the WeightedSoundSet by Identifier from sounds.json.
                ev = SoundEvent.of(id);
            }
            client.getSoundManager().play(PositionedSoundInstance.master(ev, 1.0f));
            statusMsg = "\u00a7a\u25b6 " + soundId;
        } catch (Exception e) {
            statusMsg = "\u00a7cPreview failed: " + e.getMessage();
            com.pvptweaks.PvpTweaksMod.LOGGER.warn("[PVP Tweaks] preview error: {}", e.getMessage());
        }
    }

    private List<String> currentList() {
        if (tab == 2) return customFiles;
        if (tab == 1) return filtered;
        return null;
    }

    /** Display label for a list entry. */
    private String entryLabel(String entry) {
        if (entry.startsWith("convert:")) {
            String rest = entry.substring("convert:".length());
            int sep = rest.indexOf('|');
            String absPath = rest.substring(sep + 1);
            String fname = java.nio.file.Paths.get(absPath).getFileName().toString();
            return "\u00a7e\u21bb " + fname + " \u00a78(convert on save)";
        }
        return entry;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int maxRows = (height - LIST_Y - 30) / ROW_H;
        int total   = tab == 0 ? PRESETS.length : (tab == 2 ? customFiles.size() : filtered.size());
        scrollOffset = (int) Math.max(0, Math.min(Math.max(0, total - maxRows), scrollOffset - vy * 3));
        return true;
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean b) {
        double mx = click.x(), my = click.y();
        int maxRows = (height - LIST_Y - 30) / ROW_H;
        if (my >= LIST_Y && my < LIST_Y + maxRows * ROW_H) {
            int i = (int)((my - LIST_Y) / ROW_H) + scrollOffset;
            boolean playZone = mx >= width - 54;
            if (tab == 0) {
                if (i >= 0 && i < PRESETS.length) {
                    if (playZone) previewSound(PRESETS[i][1]);
                    else selected = PRESETS[i][1];
                }
            } else {
                List<String> list = currentList();
                if (list != null && i >= 0 && i < list.size()) {
                    if (playZone) previewSound(list.get(i));
                    else selected = list.get(i);
                }
            }
            return true;
        }
        return super.mouseClicked(click, b);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // DO NOT call renderBackground() manually — super.render() does it.
        // Calling it twice causes "Can only blur once per frame" crash in MC 1.21.11.
        super.render(ctx, mx, my, delta);

        int midX = width / 2;
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a76\u266a Sound Picker \u2014 \u00a7f" + label), midX, 8, COL_WHITE);

        String cur = profile.isDefault() ? "\u00a77Vanilla" : "\u00a7b" + profile.presetId;
        ctx.drawTextWithShadow(textRenderer,
            Text.literal("\u00a77Active: " + cur), 4, LIST_Y - 18, COL_WHITE);

        if (!statusMsg.isEmpty())
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(statusMsg), midX, LIST_Y - 6, COL_WHITE);

        int maxRows = (height - LIST_Y - 30) / ROW_H;
        ctx.fill(4, LIST_Y, width - 4, LIST_Y + maxRows * ROW_H, 0x88000000);

        if (tab == 0) {
            for (int i = 0; i < maxRows; i++) {
                int idx = i + scrollOffset;
                if (idx >= PRESETS.length) break;
                String name = PRESETS[idx][0], id = PRESETS[idx][1];
                boolean sel = id.equals(selected);
                int y = LIST_Y + i * ROW_H;
                if (sel) ctx.fill(4, y, width - 58, y + ROW_H, 0x88224488);
                ctx.drawTextWithShadow(textRenderer,
                    Text.literal((sel ? "\u00a7b\u25b6 " : "\u00a77  ")
                        + name + " \u00a78(" + id + ")"), 8, y + 2, COL_WHITE);
                ctx.fill(width - 54, y + 1, width - 4, y + ROW_H - 1, 0x88004400);
                ctx.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("\u00a7a\u25b6"), width - 29, y + 2, COL_WHITE);
            }
        } else {
            List<String> list = currentList();
            if (list != null) {
                for (int i = 0; i < maxRows; i++) {
                    int idx = i + scrollOffset;
                    if (idx >= list.size()) break;
                    String entry = list.get(idx);
                    boolean sel  = entry.equals(selected);
                    int y = LIST_Y + i * ROW_H;
                    if (sel) ctx.fill(4, y, width - 58, y + ROW_H, 0x88224488);
                    ctx.drawTextWithShadow(textRenderer,
                        Text.literal((sel ? "\u00a7b\u25b6 " : "\u00a77  ") + entryLabel(entry)),
                        8, y + 2, COL_WHITE);
                    // Only show ▶ for playable sounds (not pending conversion)
                    if (!entry.startsWith("convert:")) {
                        ctx.fill(width - 54, y + 1, width - 4, y + ROW_H - 1, 0x88004400);
                        ctx.drawCenteredTextWithShadow(textRenderer,
                            Text.literal("\u00a7a\u25b6"), width - 29, y + 2, COL_WHITE);
                    }
                }
                ctx.drawTextWithShadow(textRenderer,
                    Text.literal("\u00a78" + list.size() + " files"),
                    width - 60, LIST_Y - 18, COL_GREY);
            }
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a78Click to select   \u25b6 to preview   Supports: .ogg .mp3 .wav .flac"),
            midX, height - 38, COL_GREY);
    }

    @Override public void close() { client.setScreen(parent); }
}
