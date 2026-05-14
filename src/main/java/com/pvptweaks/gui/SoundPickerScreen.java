package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import com.pvptweaks.sound.CustomSoundManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
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

public class SoundPickerScreen extends Screen {
    private final Screen parent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;
    
    private SoundListWidget list;
    private TextFieldWidget searchField;
    private String statusMsg = "";
    private long statusTime = 0;
    private int tab = 0; // 0=Presets, 1=Registry, 2=Custom

    public SoundPickerScreen(Screen parent, SoundProfile profile, String label, Runnable onSave) {
        super(Text.literal("Sound Picker"));
        this.parent = parent;
        this.profile = profile;
        this.label = label;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int listT = 60;
        int listB = 45;
        list = new SoundListWidget(client, width, height - listT - listB, listT, 26);
        addSelectableChild(list);
        list.refresh(tab);

        searchField = new TextFieldWidget(textRenderer, width / 2 - 100, 36, 200, 16, Text.literal("Search..."));
        searchField.setChangedListener(list::filter);
        addSelectableChild(searchField);

        int tabW = 80;
        int tabSpacing = 10;
        int totalTabsW = (tabW * 3) + (tabSpacing * 2);
        int tabStartX = (width - totalTabsW) / 2;

        addDrawableChild(ButtonWidget.builder(Text.literal("Presets"), b -> switchTab(0))
            .dimensions(tabStartX, 10, tabW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Registry"), b -> switchTab(1))
            .dimensions(tabStartX + tabW + tabSpacing, 10, tabW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Custom"), b -> switchTab(2))
            .dimensions(tabStartX + (tabW + tabSpacing) * 2, 10, tabW, 20).build());

        int btnW = 60;
        int btnSpacing = 4;
        int bStartX = (width - (btnW * 6 + btnSpacing * 5)) / 2;
        int bY = height - 35;

        addDrawableChild(ButtonWidget.builder(Text.literal("Default"), b -> {
            profile.mode = "default";
            onSave.run();
            close();
        }).dimensions(bStartX, bY, btnW, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Folder"), b -> openSoundsFolder())
            .dimensions(bStartX + btnW + btnSpacing, bY, btnW, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Play"), b -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null) {
                previewSound(selected.id);
            } else {
                showStatus("§cSelect a sound!", true);
            }
        }).dimensions(bStartX + (btnW + btnSpacing) * 2, bY, btnW, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§aSave"), b -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null) {
                saveEntry(selected);
            } else {
                showStatus("§cSelect a sound!", true);
            }
        }).dimensions(bStartX + (btnW + btnSpacing) * 3, bY, btnW, 20).build());

        ButtonWidget removeBtn = ButtonWidget.builder(Text.literal("§cRemove"), b -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null && tab == 2) {
                try {
                    Files.deleteIfExists(java.nio.file.Path.of(selected.id));
                    list.refresh(2);
                    showStatus("§aRemoved sound", false);
                } catch (Exception e) {
                    showStatus("§cFailed to remove", true);
                }
            }
        }).dimensions(bStartX + (btnW + btnSpacing) * 4, bY, btnW, 20).build();
        
        // Hide remove button if not in custom tab
        addDrawableChild(removeBtn);

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close())
            .dimensions(bStartX + (btnW + btnSpacing) * 5, bY, btnW, 20).build());
    }

    private void openSoundsFolder() {
        try {
            net.minecraft.util.Util.getOperatingSystem().open(com.pvptweaks.config.PvpTweaksConfig.SOUNDS_DIR.toFile());
        } catch (Exception e) {
            try {
                if (System.getProperty("os.name").toLowerCase().contains("nix") || System.getProperty("os.name").toLowerCase().contains("nux")) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", com.pvptweaks.config.PvpTweaksConfig.SOUNDS_DIR.toAbsolutePath().toString()});
                } else {
                    showStatus("§cFailed to open folder", true);
                }
            } catch (Exception ex) {
                showStatus("§cFailed to open folder", true);
            }
        }
    }

    private void switchTab(int t) {
        this.tab = t;
        list.refresh(t);
        searchField.setText("");
    }

    private void showStatus(String msg, boolean error) {
        this.statusMsg = msg;
        this.statusTime = System.currentTimeMillis();
    }

    private void saveEntry(SoundEntry entry) {
        if (tab == 2) {
            Identifier customId = CustomSoundManager.registerCustomSound(entry.id);
            if (customId != null) {
                profile.mode = "preset";
                profile.presetId = customId.toString();
                onSave.run();
                close();
            } else {
                showStatus("§cRegistration failed", true);
            }
        } else {
            profile.mode = "preset";
            profile.presetId = entry.id;
            onSave.run();
            close();
        }
    }

    private void previewSound(String idStr) {
        try {
            Identifier id;
            if (tab == 2) {
                id = CustomSoundManager.registerCustomSound(idStr);
                if (id == null) {
                    showStatus("§cConversion failed", true);
                    return;
                }
            } else if (idStr.contains(":")) {
                id = Identifier.of(idStr);
            } else {
                id = Identifier.ofVanilla(idStr);
            }

            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Previewing sound: {}", id);
            SoundEvent ev = SoundEvent.of(id);
            client.getSoundManager().play(PositionedSoundInstance.master(ev, 1.0f));
            showStatus("§aPlaying: " + id.getPath(), false);
        } catch (Exception e) {
            showStatus("§cPreview failed", true);
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Preview failed for: " + idStr, e);
        }
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        
        list.render(ctx, mx, my, delta);
        searchField.render(ctx, mx, my, delta);
        
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§l" + label), width / 2, 8, 0xFFFFFF);

        if (tab == 2) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("§7Tip: Place .ogg files in the sounds folder. Native OGGs are recommended for performance."), width / 2, 26, 0xFFFFFFFF);
        }
        
        // Render buttons and other widgets
        super.render(ctx, mx, my, delta);

        // Status Toast
        if (!statusMsg.isEmpty() && System.currentTimeMillis() - statusTime < 3000) {
            int msgW = textRenderer.getWidth(statusMsg);
            int tx = width / 2;
            int ty = height - 55;
            ctx.fill(tx - msgW/2 - 4, ty - 4, tx + msgW/2 + 4, ty + 12, 0xBB000000);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(statusMsg), tx, ty, 0xFFFFFFFF);
        }
    }

    @Override public void close() { client.setScreen(parent); }

    class SoundListWidget extends AlwaysSelectedEntryListWidget<SoundEntry> {
        private final List<SoundEntry> all = new ArrayList<>();

        public SoundListWidget(MinecraftClient mc, int width, int height, int top, int entryH) {
            super(mc, width, height, top, entryH);
        }

        public void refresh(int tab) {
            clearEntries();
            all.clear();
            if (tab == 0) {
                String[][] presets = {
                    {"Explosion (Default)", "minecraft:entity.generic.explode"},
                    {"Ender Dragon Death", "minecraft:entity.ender_dragon.death"},
                    {"Anvil Land", "minecraft:block.anvil.land"},
                    {"Orb Pickup", "minecraft:entity.experience_orb.pickup"},
                    {"Totem Use", "minecraft:item.totem.use"},
                    {"Shield Break", "minecraft:item.shield.break"}
                };
                for (String[] p : presets) all.add(new SoundEntry(p[0], p[1]));
            } else if (tab == 1) {
                Registries.SOUND_EVENT.getIds().stream().map(Identifier::toString).sorted()
                    .forEach(id -> all.add(new SoundEntry(id, id)));
            } else {
                try {
                    Path dir = PvpTweaksConfig.SOUNDS_DIR;
                    if (Files.exists(dir)) {
                        Files.list(dir).sorted().forEach(p -> {
                            String fn = p.getFileName().toString();
                            String lowFn = fn.toLowerCase();
                            if (lowFn.endsWith(".ogg") || lowFn.endsWith(".mp3") || lowFn.endsWith(".wav")) {
                                all.add(new SoundEntry(fn, p.toAbsolutePath().toString()));
                            } else {
                                all.add(new SoundEntry("§7" + fn + " (unsupported)", "invalid"));
                            }
                        });
                    }
                } catch (IOException ignored) {}
            }
            all.forEach(this::addEntry);
        }

        public void filter(String q) {
            String low = q.toLowerCase();
            clearEntries();
            all.stream().filter(e -> e.name.toLowerCase().contains(low) || e.id.toLowerCase().contains(low))
                .forEach(this::addEntry);
        }
    }

    class SoundEntry extends AlwaysSelectedEntryListWidget.Entry<SoundEntry> {
        final String name;
        final String id;
        private long lastClick = 0;

        SoundEntry(String name, String id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, boolean hovered, float delta) {
            int x = this.getX();
            int y = this.getY();
            
            // Clean vanilla-like item rendering
            int color = hovered ? 0xFFFFFFFF : 0xFFAAAAAA;
            if (this == list.getSelectedOrNull()) color = 0xFF55FF55; // Green for selected
            
            ctx.drawTextWithShadow(textRenderer, Text.literal(name), x + 2, y + 2, color);
            ctx.drawTextWithShadow(textRenderer, Text.literal(id.length() > 50 ? "..." + id.substring(id.length()-47) : id), x + 2, y + 14, 0xFF666666);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            if (doubled) {
                if (!id.equals("invalid")) previewSound(id);
            }
            list.setSelected(this);
            return true;
        }

        @Override
        public Text getNarration() { return Text.literal(name); }
    }
}
