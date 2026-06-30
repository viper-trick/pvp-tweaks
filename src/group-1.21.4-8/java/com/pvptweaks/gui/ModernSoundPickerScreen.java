package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import com.pvptweaks.sound.CustomSoundManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModernSoundPickerScreen extends Screen {
    private final Screen parent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;
    
    private SoundListWidget list;
    private EditBox searchField;
    private int tab = 0; 
    private float waveAnim = 0;
    private boolean isPlaying = false;

    public ModernSoundPickerScreen(Screen parent, SoundProfile profile, String label, Runnable onSave) {
        super(Component.literal("Sound Picker"));
        this.parent = parent;
        this.profile = profile;
        this.label = label;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int listT = 104;
        int listB = 52;
        list = new SoundListWidget(minecraft, width - 40, height - listT - listB, listT, 36);
        list.setX(20);
        addWidget(list);
        list.refresh(tab);

        searchField = new EditBox(font, width / 2 - 100, 84, 200, 16, Component.literal("Search..."));
        searchField.setResponder(list::filter);
        addWidget(searchField);

        selectActiveSound();

        int tabW = 70;
        int tabX = width / 2 - 115;
        addRenderableWidget(new ModernButtonWidget(tabX, 34, tabW, 20, Component.literal("Presets"), () -> switchTab(0)));
        addRenderableWidget(new ModernButtonWidget(tabX + 80, 34, tabW, 20, Component.literal("Registry"), () -> switchTab(1)));
        addRenderableWidget(new ModernButtonWidget(tabX + 160, 34, tabW, 20, Component.literal("Custom"), () -> switchTab(2)));

        int pitchY = 60;
        addRenderableWidget(new CustomSliderWidget(width / 2 - 100, pitchY, 160, 20, "Pitch", profile.pitchPct, 0, 200, true, v -> profile.pitchPct = v.intValue()));
        addRenderableWidget(new ModernButtonWidget(width / 2 + 65, pitchY, 20, 20, Component.literal("\u21ba"), () -> {
            profile.pitchPct = 100;
            init();
        }));

        int btnY = height - 36;
        addRenderableWidget(new ModernButtonWidget(20, btnY, 70, 20, Component.literal("Default"), () -> {
            profile.mode = "default";
            onSave.run();
            onClose();
        }));

        addRenderableWidget(new ModernButtonWidget(width / 2 - 120, btnY, 75, 20, Component.literal("\u25b6 Preview"), () -> {
            SoundEntry selected = list.getSelected();
            if (selected != null) previewSound(selected.id);
        }));

        addRenderableWidget(new ModernButtonWidget(width / 2 - 40, btnY, 75, 20, Component.literal("\u00a7a+ Add"), () -> {
            minecraft.setScreen(new AddSoundScreen(this));
        }));

        addRenderableWidget(new ModernButtonWidget(width / 2 + 40, btnY, 75, 20, Component.literal("\u2699 Edit"), () -> {
            SoundEntry selected = list.getSelected();
            if (selected != null && tab == 2) {
                minecraft.setScreen(new SoundEditorScreen(this, selected.name, selected.id));
            }
        }));

        addRenderableWidget(new ModernButtonWidget(width / 2 + 120, btnY, 75, 20, Component.literal("\u2714 Apply"), () -> {
            SoundEntry selected = list.getSelected();
            if (selected != null) saveEntry(selected);
        }));

        addRenderableWidget(new ModernButtonWidget(width / 2 - 200, btnY, 70, 20, Component.literal("§cRemove"), () -> {
            SoundEntry selected = list.getSelected();
            if (selected != null && tab == 2) {
                minecraft.setScreen(new ConfirmRemoveScreen(this, () -> {
                    try {
                        Files.deleteIfExists(Path.of(selected.id));
                        String base = selected.id.replaceFirst("\\.(ogg|mp3|wav)$", "");
                        Files.deleteIfExists(Path.of(base + ".json"));
                        Files.deleteIfExists(Path.of(base + ".orig"));
                        list.refresh(2);
                    } catch (IOException ignored) {}
                }));
            }
        }));
        addRenderableWidget(new ModernButtonWidget(width - 90, btnY, 70, 20, Component.literal("Cancel"), this::onClose));
    }

    private void openSoundsFolder() {
        try {
            net.minecraft.Util.getPlatform().openFile(PvpTweaksConfig.SOUNDS_DIR.toFile());
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("Failed to open sounds folder", e);
        }
    }

    private void switchTab(int t) {
        this.tab = t;
        list.refresh(t);
        searchField.setValue("");
        selectActiveSound();
    }

    private void selectActiveSound() {
        if (!"preset".equals(profile.mode)) return;
        String targetId = profile.presetId;
        if (targetId == null || targetId.isEmpty()) return;

        for (SoundEntry entry : list.children()) {
            if (entry.id.equals(targetId)) {
                list.setSelected(entry);
                list.scrollToEntry(entry);
                return;
            }
        }
    }

    private void saveEntry(SoundEntry entry) {
        if (tab == 2) {
            ResourceLocation customId = CustomSoundManager.registerCustomSound(entry.id);
            if (customId != null) {
                profile.mode = "preset";
                profile.presetId = customId.toString();
            }
        } else {
            profile.mode = "preset";
            profile.presetId = entry.id;
        }
        onSave.run();
        onClose();
    }

    private void previewSound(String idStr) {
        try {
            ResourceLocation id = tab == 2 ? CustomSoundManager.registerCustomSound(idStr) : (idStr.contains(":") ? ResourceLocation.parse(idStr) : ResourceLocation.withDefaultNamespace(idStr));
            if (id == null) return;
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(id), 1.0f, 1.0f));
            isPlaying = true;
            waveAnim = 0;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("Preview failed", e);
        }
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 10, 10, width - 20, height - 20, 1, UiPalette.BORDER);
        super.render(ctx, mx, my, delta);
        list.render(ctx, mx, my, delta);
        searchField.render(ctx, mx, my, delta);
        ctx.drawString(font, Component.literal("\u00a7l" + label), 20, 16, UiPalette.ACCENT_BLUE);
        if (isPlaying) {
            waveAnim += delta * 0.2f;
            drawSoundWave(ctx, width - 50, 25);
            if (waveAnim > 10) isPlaying = false;
        }
    }

    private void drawSoundWave(GuiGraphics ctx, int x, int y) {
        for (int i = 0; i < 5; i++) {
            float h = (float) Math.abs(Math.sin(waveAnim + i * 0.5)) * 10;
            ctx.fill(x + i * 4, (int)(y - h/2), x + i * 4 + 2, (int)(y + h/2), UiPalette.ACCENT_BLUE);
        }
    }

    /** Called by AddSoundScreen after a file has been copied to SOUNDS_DIR. */
    public void importAndApply(String absPath) {
        // Switch to Custom tab so the entry is visible
        switchTab(2);
        // Refresh list to pick up the new file
        list.refresh(2);
        // Try to find and select the matching entry
        for (SoundEntry entry : list.children()) {
            if (entry.id.equals(absPath)) {
                list.setSelected(entry);
                saveEntry(entry);  // registers + saves profile + closes
                return;
            }
        }
        // File is in SOUNDS_DIR but not auto-selected (shouldn't happen) — just show it
    }

    public void refreshCustomTab() { if (tab == 2) list.refresh(2); }

    public void selectAndSaveSound(String path) { importAndApply(path); }

    @Override public void onClose() { minecraft.setScreen(parent); }

    class SoundListWidget extends ObjectSelectionList<SoundEntry> {
        private final List<SoundEntry> all = new ArrayList<>();
        public SoundListWidget(Minecraft mc, int width, int height, int top, int entryH) { super(mc, width, height, top, entryH); }
        @Override protected int scrollBarX() { return -1000; }
        public void scrollToEntry(SoundEntry entry) { ensureVisible(entry); }
        public void refresh(int tab) {
            clearEntries(); all.clear();
            if (tab == 0) {
                String[][] presets = {{"Explosion", "minecraft:entity.generic.explode"}, {"Anvil", "minecraft:block.anvil.land"}, {"Totem", "minecraft:item.totem.use"}};
                for (String[] p : presets) all.add(new SoundEntry(p[0], p[1]));
            } else if (tab == 1) {
                BuiltInRegistries.SOUND_EVENT.keySet().stream().map(ResourceLocation::toString).sorted().forEach(id -> all.add(new SoundEntry(id, id)));
            } else {
                try {
                    Path dir = PvpTweaksConfig.SOUNDS_DIR;
                    if (Files.exists(dir)) {
                        Files.list(dir).sorted().forEach(p -> {
                            String fn = p.getFileName().toString();
                            if (fn.endsWith(".ogg") || fn.endsWith(".mp3") || fn.endsWith(".wav")) all.add(new SoundEntry(fn, p.toAbsolutePath().toString()));
                        });
                    }
                } catch (IOException ignored) {}
            }
            all.forEach(this::addEntry);
        }
        public void filter(String q) {
            String low = q.toLowerCase();
            clearEntries();
            all.stream().filter(e -> e.name.toLowerCase().contains(low)).forEach(this::addEntry);
        }
    }

    class SoundEntry extends ObjectSelectionList.Entry<SoundEntry> {
        final String name, id;
        private final long createdAt = System.currentTimeMillis();
        SoundEntry(String name, String id) { this.name = name; this.id = id; }
        @Override
        public void render(GuiGraphics ctx, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            boolean selected = list.getSelected() == this;
            if (selected) RenderUtils.drawRoundedRect(ctx, x, y, list.getRowWidth(), 32, 4, 0x4000A3FF);
            int availWidth = list.getRowWidth() - 10;
            drawMarqueeText(ctx, name, x + 5, y + 5, availWidth, selected ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY, selected);
            drawMarqueeText(ctx, id, x + 5, y + 18, availWidth, 0xFF666666, selected);
        }
        private void drawMarqueeText(GuiGraphics ctx, String text, int tx, int ty, int availWidth, int color, boolean selected) {
            int textWidth = font.width(text);
            ctx.enableScissor(tx, ty, tx + availWidth, ty + font.lineHeight);
            int offset = 0;
            if (selected && textWidth > availWidth) {
                long elapsed = System.currentTimeMillis() - createdAt;
                long pause = 1000;
                double pxPerMs = 40.0 / 1000.0;
                long scrollMs = (long)((textWidth - availWidth) / pxPerMs);
                long cycle = pause + scrollMs + pause;
                long phase = elapsed % cycle;
                if (phase >= pause) {
                    double prog = (phase - pause) / (double) scrollMs;
                    offset = -(int)(prog * (textWidth - availWidth));
                }
            }
            ctx.drawString(font, Component.literal(text), tx + offset, ty, color);
            ctx.disableScissor();
        }
        private long lastClickTime = 0;
        @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < 300 && !id.equals("invalid")) previewSound(id);
            lastClickTime = now;
            list.setSelected(this);
            return true;
        }
        @Override public Component getNarration() { return Component.literal(name); }
    }
    class ConfirmRemoveScreen extends Screen {
        private final Screen parent;
        private final Runnable onConfirm;

        ConfirmRemoveScreen(Screen parent, Runnable onConfirm) {
            super(Component.literal("Confirm"));
            this.parent = parent;
            this.onConfirm = onConfirm;
        }

        @Override
        protected void init() {
            addRenderableWidget(new ModernButtonWidget(width / 2 - 80, height / 2 - 15, 70, 20, Component.literal("§aYes"), () -> {
                onConfirm.run();
                minecraft.setScreen(parent);
            }));
            addRenderableWidget(new ModernButtonWidget(width / 2 + 10, height / 2 - 15, 70, 20, Component.literal("Cancel"), () -> {
                minecraft.setScreen(parent);
            }));
        }

        @Override
        public void render(GuiGraphics ctx, int mx, int my, float delta) {
            RenderUtils.drawGradientRect(ctx, 0, 0, width, height, 0xCC000000, 0xCC000000);
            RenderUtils.drawOutline(ctx, width / 2 - 100, height / 2 - 40, 200, 60, 1, UiPalette.BORDER);
            ctx.drawCenteredString(font, Component.literal("Remove?"), width / 2, height / 2 - 30, 0xFFFFFFFF);
            super.render(ctx, mx, my, delta);
        }

        @Override public void onClose() { minecraft.setScreen(parent); }
    }
}
