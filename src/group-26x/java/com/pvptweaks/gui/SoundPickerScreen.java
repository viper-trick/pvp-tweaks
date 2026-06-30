package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import com.pvptweaks.sound.CustomSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SoundPickerScreen extends Screen {
    private final Screen currentConfigScreen;
    private final Screen originalParent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;
    
    private SoundListWidget list;
    private EditBox searchField;
    private String statusMsg = "";
    private long statusTime = 0;
    private int tab = 0; // 0=Presets, 1=Registry, 2=Custom

    public SoundPickerScreen(Screen currentConfigScreen, Screen originalParent, SoundProfile profile, String label, Runnable onSave) {
        super(Component.literal("Sound Picker"));
        this.currentConfigScreen = currentConfigScreen;
        this.originalParent = originalParent;
        this.profile = profile;
        this.label = label;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int listT = 100;
        int listB = 52;
        list = new SoundListWidget(minecraft, width, height - listT - listB, listT, 26);
        addWidget(list);
        list.refresh(tab);

        searchField = new EditBox(font, width / 2 - 100, 62, 200, 16, Component.literal("Search..."));
        searchField.setResponder(list::filter);
        addWidget(searchField);

        int tabW = 80;
        int tabSpacing = 10;
        int totalTabsW = (tabW * 3) + (tabSpacing * 2);
        int tabStartX = (width - totalTabsW) / 2;

        addRenderableWidget(Button.builder(Component.literal("Presets"), b -> switchTab(0))
            .bounds(tabStartX, 36, tabW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Registry"), b -> switchTab(1))
            .bounds(tabStartX + tabW + tabSpacing, 36, tabW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Custom"), b -> switchTab(2))
            .bounds(tabStartX + (tabW + tabSpacing) * 2, 36, tabW, 20).build());

        int btnW = 55;
        int btnSpacing = 4;
        int bStartX = (width - (btnW * 7 + btnSpacing * 6)) / 2;
        int bY = height - 36;

        addRenderableWidget(Button.builder(Component.literal("Default"), b -> {
            profile.mode = "default";
            onSave.run();
            onClose();
        }).bounds(bStartX, bY, btnW, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Add"), b -> {
            minecraft.setScreen(new AddSoundScreen(this));
        }).bounds(bStartX + btnW + btnSpacing, bY, btnW, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Play"), b -> {
            SoundListWidget.SoundEntry selected = (SoundListWidget.SoundEntry)list.getSelected();
            if (selected != null) {
                previewSound(selected.id);
            } else {
                showStatus("§cSelect a sound!", true);
            }
        }).bounds(bStartX + (btnW + btnSpacing) * 2, bY, btnW, 20).build());

        Button editBtn = Button.builder(Component.literal("§6Edit"), b -> {
            SoundListWidget.SoundEntry selected = (SoundListWidget.SoundEntry)list.getSelected();
            if (selected != null && tab == 2) {
                minecraft.setScreen(new SoundEditorScreen(this, selected.name, selected.id));
            }
        }).bounds(bStartX + (btnW + btnSpacing) * 3, bY, btnW, 20).build();

        addRenderableWidget(Button.builder(Component.literal("§aSave"), b -> {
            SoundListWidget.SoundEntry selected = (SoundListWidget.SoundEntry)list.getSelected();
            if (selected != null) {
                saveEntry(selected);
            } else {
                showStatus("§cSelect a sound!", true);
            }
        }).bounds(bStartX + (btnW + btnSpacing) * 4, bY, btnW, 20).build());

        Button removeBtn = Button.builder(Component.literal("§cRemove"), b -> {
            SoundListWidget.SoundEntry selected = (SoundListWidget.SoundEntry)list.getSelected();
            if (selected != null && tab == 2) {
                try {
                    Files.deleteIfExists(java.nio.file.Path.of(selected.id));
                    list.refresh(2);
                    showStatus("§aRemoved sound", false);
                } catch (Exception e) {
                    showStatus("§cFailed to remove", true);
                }
            }
        }).bounds(bStartX + (btnW + btnSpacing) * 5, bY, btnW, 20).build();
        
        // Hide remove/edit buttons if not in custom tab
        addRenderableWidget(editBtn);
        addRenderableWidget(removeBtn);

        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
            .bounds(bStartX + (btnW + btnSpacing) * 6, bY, btnW, 20).build());
    }

    // TODO 26.x: File drop handling API changed

    public void refreshCustomTab() { if (tab == 2) list.refresh(2); }

    public void selectAndSaveSound(String path) { importAndApply(path); }

    /** Called by AddSoundScreen after file is copied to SOUNDS_DIR. */
    public void importAndApply(String absPath) {
        switchTab(2);
        list.refresh(2);
        for (SoundListWidget.SoundEntry entry : (java.util.List<SoundListWidget.SoundEntry>)(List)list.children()) {
            if (entry.id.equals(absPath)) {
                list.setSelected(entry);
                saveEntry(entry);
                return;
            }
        }
        showStatus("\u00a7aImported! Select the file and click Save.", false);
    }

    private void switchTab(int t) {
        this.tab = t;
        list.refresh(t);
        searchField.setValue("");
    }

    private void showStatus(String msg, boolean error) {
        this.statusMsg = msg;
        this.statusTime = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    private void saveEntry(SoundListWidget.SoundEntry entry) {
        if (tab == 2) {
            Identifier customId = CustomSoundManager.registerCustomSound(entry.id);
            if (customId != null) {
                profile.mode = "preset";
                profile.presetId = customId.toString();
                onSave.run();
                onClose();
            } else {
                showStatus("§cRegistration failed", true);
            }
        } else {
            profile.mode = "preset";
            profile.presetId = entry.id;
            onSave.run();
            onClose();
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
                id = Identifier.parse(idStr);
            } else {
                id = Identifier.withDefaultNamespace(idStr);
            }

            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Previewing sound: {}", id);
            SoundEvent ev = SoundEvent.createVariableRangeEvent(id);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ev, 1.0f));
            showStatus("§aPlaying: " + id.getPath(), false);
        } catch (Exception e) {
            showStatus("§cPreview failed", true);
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Preview failed for: " + idStr, e);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        
        super.extractRenderState(ctx, mx, my, delta);
        
        list.extractRenderState(ctx, mx, my, delta);
        searchField.extractRenderState(ctx, mx, my, delta);
        
        ctx.centeredText(font, Component.literal("§l" + label), width / 2, 8, 0xFFFFFF);

        if (tab == 2) {
            ctx.centeredText(font, Component.literal("§7Tip: Place .ogg files in the sounds folder. Native OGGs are recommended for performance."), width / 2, 82, 0xFFFFFFFF);
        }

        if (!statusMsg.isEmpty() && System.currentTimeMillis() - statusTime < 3000) {
            int msgW = font.width(statusMsg);
            int tx = width / 2;
            int ty = height - 55;
            ctx.fill(tx - msgW/2 - 4, ty - 4, tx + msgW/2 + 4, ty + 12, 0xBB000000);
            ctx.centeredText(font, Component.literal(statusMsg), tx, ty, 0xFFFFFFFF);
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new com.pvptweaks.gui.PvpTweaksHubScreen(originalParent));
    }

        @SuppressWarnings("rawtypes")
    class SoundListWidget extends AbstractSelectionList {
        private final List<SoundEntry> all = new ArrayList<>();

        public SoundListWidget(Minecraft mc, int width, int height, int top, int entryH) {
            super(mc, width, height, top, entryH);
        }

        @Override
        public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput out) {}

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
                BuiltInRegistries.SOUND_EVENT.keySet().stream().map(Identifier::toString).sorted()
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

        class SoundEntry extends AbstractSelectionList.Entry<SoundEntry> {
            final String name;
            final String id;
            private long lastClick = 0;

            SoundEntry(String name, String id) {
                this.name = name;
                this.id = id;
            }

            @Override
            public void extractContent(GuiGraphicsExtractor ctx, int mx, int my, boolean hovered, float delta) {
                int x = this.getX();
                int y = this.getY();
                
                SoundListWidget list = SoundListWidget.this;
                int color = hovered ? 0xFFFFFFFF : 0xFFAAAAAA;
                if (this == list.getSelected()) color = 0xFF55FF55;
                
                ctx.text(font, Component.literal(name), x + 2, y + 2, color);
                ctx.text(font, Component.literal(id.length() > 50 ? "..." + id.substring(id.length()-47) : id), x + 2, y + 14, 0xFF666666);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
                SoundListWidget list = SoundListWidget.this;
                if (doubled) {
                    if (!id.equals("invalid")) previewSound(id);
                }
                list.setSelected(this);
                return true;
            }

            public Component getNarration() { return Component.literal(name); }
        }
    }
}
