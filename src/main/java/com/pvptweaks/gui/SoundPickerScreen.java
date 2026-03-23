package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
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

    private static final String[][] PRESETS = {
        {"Firework Blast",        "entity.firework_rocket.blast"      },
        {"Firework Large Blast",  "entity.firework_rocket.large_blast"},
        {"Firework Twinkle",      "entity.firework_rocket.twinkle"    },
        {"Lightning Thunder",     "entity.lightning_bolt.thunder"     },
        {"Ender Dragon Growl",    "entity.ender_dragon.growl"         },
        {"Ender Dragon Death",    "entity.ender_dragon.death"         },
        {"Wither Spawn",          "entity.wither.spawn"               },
        {"Warden Sonic Boom",     "entity.warden.sonic_boom"          },
        {"Elder Guardian Curse",  "entity.elder_guardian.curse"       },
        {"Toast Challenge",       "ui.toast.challenge_complete"       },
        {"Bell Ring",             "block.bell.use"                    },
        {"Anvil Land",            "block.anvil.land"                  },
        {"Explosion",             "entity.generic.explode"            },
    };

    private final Screen parent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;

    private TextFieldWidget searchField;
    private List<String> allSounds;
    private List<String> filtered;
    private List<String> customFiles = new ArrayList<>();
    private int scrollOffset = 0;
    private String selected  = "";
    private String statusMsg = "";
    // tabs: 0=recommended, 1=all MC, 2=custom folder
    private int tab = 0;

    private static final int ROW_H  = 14;
    private static final int LIST_Y = 70;

    public SoundPickerScreen(Screen parent, SoundProfile profile, String label, Runnable onSave) {
        super(Text.literal("Sound Picker — " + label));
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

        // Search bar
        searchField = new TextFieldWidget(textRenderer, width/2 - 150, 48, 300, 16, Text.empty());
        searchField.setPlaceholder(Text.literal("Search sounds..."));
        searchField.setChangedListener(q -> {
            filtered = allSounds.stream().filter(s -> s.contains(q.toLowerCase())).collect(Collectors.toList());
            scrollOffset = 0;
        });
        addDrawableChild(searchField);

        // Tabs
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2605 Recommended"),
            btn -> { tab = 0; scrollOffset = 0; }).dimensions(4, 26, 140, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("All MC Sounds"),
            btn -> { tab = 1; scrollOffset = 0; }).dimensions(148, 26, 120, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc2 My Sounds"),
            btn -> { tab = 2; scrollOffset = 0; refreshCustomFiles(); }).dimensions(272, 26, 110, 18).build());

        // Open folder button
        addDrawableChild(ButtonWidget.builder(Text.literal("\ud83d\udcc1 Open Folder"),
            btn -> openSoundsFolder()).dimensions(width - 130, 26, 126, 18).build());

        // Bottom
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2714 Use Vanilla"),
            btn -> { profile.mode="default"; profile.presetId=""; onSave.run(); client.setScreen(parent); })
            .dimensions(4, height - 24, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2714 Save Selected"),
            btn -> { if (!selected.isBlank()) { profile.mode="preset"; profile.presetId=selected; } onSave.run(); client.setScreen(parent); })
            .dimensions(width/2 - 55, height - 24, 110, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2716 Cancel"),
            btn -> client.setScreen(parent)).dimensions(width - 84, height - 24, 80, 20).build());
    }

    private void refreshCustomFiles() {
        customFiles.clear();
        try {
            Path dir = PvpTweaksConfig.SOUNDS_DIR;
            if (Files.exists(dir)) {
                customFiles = Files.list(dir)
                    .filter(p -> p.toString().endsWith(".ogg"))
                    .map(p -> "pvptweaks:custom/" + p.getFileName().toString().replace(".ogg",""))
                    .sorted().collect(Collectors.toList());
            }
        } catch (IOException e) { /* ignore */ }
        if (customFiles.isEmpty()) {
            statusMsg = "\u00a77No .ogg files in sounds folder yet. Add files and click My Sounds.";
        } else {
            statusMsg = "";
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

    private void playSound(String soundId) {
        try {
            Identifier id = Identifier.tryParse(soundId);
            if (id == null) return;
            SoundEvent ev = Registries.SOUND_EVENT.get(id);
            if (ev == null) return;
            client.getSoundManager().play(PositionedSoundInstance.master(ev, 1.0f));
        } catch (Exception ignored) {}
    }

    private List<String> currentList() {
        if (tab == 2) return customFiles;
        if (tab == 1) return filtered;
        return null; // presets
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
                    if (playZone) playSound(PRESETS[i][1]);
                    else selected = PRESETS[i][1];
                }
            } else {
                List<String> list = currentList();
                if (list != null && i >= 0 && i < list.size()) {
                    if (playZone) playSound(list.get(i));
                    else selected = list.get(i);
                }
            }
            return true;
        }
        return super.mouseClicked(click, b);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);
        super.render(ctx, mx, my, delta);

        int midX = width / 2;
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a76\u266a Sound Picker — \u00a7f" + label), midX, 8, 0xFFFFFF);

        // Active
        String cur = profile.isDefault() ? "\u00a77Vanilla" : "\u00a7b" + profile.presetId;
        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a77Active: " + cur), 4, LIST_Y - 18, 0xFFFFFF);
        if (!statusMsg.isEmpty())
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(statusMsg), midX, LIST_Y - 6, 0xFFFFFF);

        // List
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
                    Text.literal((sel?"\u00a7b\u25b6 ":"\u00a77  ") + name + " \u00a78(" + id + ")"),
                    8, y + 2, 0xFFFFFF);
                ctx.fill(width-54, y+1, width-4, y+ROW_H-1, 0x88004400);
                ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7a\u25b6"), width-29, y+2, 0xFFFFFF);
            }
        } else {
            List<String> list = currentList();
            if (list != null) {
                for (int i = 0; i < maxRows; i++) {
                    int idx = i + scrollOffset;
                    if (idx >= list.size()) break;
                    String s = list.get(idx);
                    boolean sel = s.equals(selected);
                    int y = LIST_Y + i * ROW_H;
                    if (sel) ctx.fill(4, y, width-58, y+ROW_H, 0x88224488);
                    ctx.drawTextWithShadow(textRenderer,
                        Text.literal((sel?"\u00a7b\u25b6 ":"\u00a77  ") + s), 8, y+2, 0xFFFFFF);
                    ctx.fill(width-54, y+1, width-4, y+ROW_H-1, 0x88004400);
                    ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7a\u25b6"), width-29, y+2, 0xFFFFFF);
                }
                ctx.drawTextWithShadow(textRenderer,
                    Text.literal("\u00a78" + list.size() + " sounds"), width-80, LIST_Y-18, 0xAAAAAA);
            }
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a78Put .ogg files in the sounds folder, click My Sounds to refresh"),
            midX, height - 38, 0xAAAAAA);
    }

    @Override public void close() { client.setScreen(parent); }
}
