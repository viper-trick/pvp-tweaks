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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModernSoundPickerScreen extends Screen {
    private final Screen parent;
    private final SoundProfile profile;
    private final String label;
    private final Runnable onSave;
    
    private SoundListWidget list;
    private TextFieldWidget searchField;
    private int tab = 0; 
    private float waveAnim = 0;
    private boolean isPlaying = false;

    public ModernSoundPickerScreen(Screen parent, SoundProfile profile, String label, Runnable onSave) {
        super(Text.literal("Sound Picker"));
        this.parent = parent;
        this.profile = profile;
        this.label = label;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int listT = 88;
        int listB = 52;
        list = new SoundListWidget(client, width - 40, height - listT - listB, listT, 36);
        list.setX(20);
        addSelectableChild(list);
        list.refresh(tab);

        searchField = new TextFieldWidget(textRenderer, width / 2 - 100, 60, 200, 16, Text.literal("Search..."));
        searchField.setChangedListener(list::filter);
        addSelectableChild(searchField);

        int tabW = 70;
        int tabX = width / 2 - 115;
        addDrawableChild(new ModernButtonWidget(tabX, 34, tabW, 20, Text.literal("Presets"), () -> switchTab(0)));
        addDrawableChild(new ModernButtonWidget(tabX + 80, 34, tabW, 20, Text.literal("Registry"), () -> switchTab(1)));
        addDrawableChild(new ModernButtonWidget(tabX + 160, 34, tabW, 20, Text.literal("Custom"), () -> switchTab(2)));

        int btnY = height - 36;
        addDrawableChild(new ModernButtonWidget(20, btnY, 70, 20, Text.literal("Default"), () -> {
            profile.mode = "default";
            onSave.run();
            close();
        }));

        addDrawableChild(new ModernButtonWidget(width / 2 - 120, btnY, 75, 20, Text.literal("\u25b6 Preview"), () -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null) previewSound(selected.id);
        }));

        addDrawableChild(new ModernButtonWidget(width / 2 - 40, btnY, 75, 20, Text.literal("\u00a7a+ Add"), () -> {
            client.setScreen(new AddSoundScreen(this));
        }));

        addDrawableChild(new ModernButtonWidget(width / 2 + 40, btnY, 75, 20, Text.literal("\u2699 Edit"), () -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null && tab == 2) {
                client.setScreen(new SoundEditorScreen(this, selected.name, selected.id));
            }
        }));

        addDrawableChild(new ModernButtonWidget(width / 2 + 120, btnY, 75, 20, Text.literal("\u2714 Apply"), () -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null) saveEntry(selected);
        }));

        addDrawableChild(new ModernButtonWidget(width / 2 - 200, btnY, 70, 20, Text.literal("§cRemove"), () -> {
            SoundEntry selected = list.getSelectedOrNull();
            if (selected != null && tab == 2) {
                client.setScreen(new ConfirmRemoveScreen(this, () -> {
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
        addDrawableChild(new ModernButtonWidget(width - 90, btnY, 70, 20, Text.literal("Cancel"), this::close));
    }

    private void openSoundsFolder() {
        try {
            net.minecraft.util.Util.getOperatingSystem().open(PvpTweaksConfig.SOUNDS_DIR.toFile());
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("Failed to open sounds folder", e);
        }
    }

    private void switchTab(int t) {
        this.tab = t;
        list.refresh(t);
        searchField.setText("");
    }

    private void saveEntry(SoundEntry entry) {
        if (tab == 2) {
            Identifier customId = CustomSoundManager.registerCustomSound(entry.id);
            if (customId != null) {
                profile.mode = "preset";
                profile.presetId = customId.toString();
            }
        } else {
            profile.mode = "preset";
            profile.presetId = entry.id;
        }
        onSave.run();
        close();
    }

    private void previewSound(String idStr) {
        try {
            Identifier id = tab == 2 ? CustomSoundManager.registerCustomSound(idStr) : (idStr.contains(":") ? Identifier.of(idStr) : Identifier.ofVanilla(idStr));
            if (id == null) return;
            client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvent.of(id), 1.0f));
            isPlaying = true;
            waveAnim = 0;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("Preview failed", e);
        }
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 10, 10, width - 20, height - 20, 1, UiPalette.BORDER);
        super.render(ctx, mx, my, delta);
        list.render(ctx, mx, my, delta);
        searchField.render(ctx, mx, my, delta);
        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a7l" + label), 20, 16, UiPalette.ACCENT_BLUE);
        if (isPlaying) {
            waveAnim += delta * 0.2f;
            drawSoundWave(ctx, width - 50, 25);
            if (waveAnim > 10) isPlaying = false;
        }
    }

    private void drawSoundWave(DrawContext ctx, int x, int y) {
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

    @Override public void close() { client.setScreen(parent); }

    class SoundListWidget extends AlwaysSelectedEntryListWidget<SoundEntry> {
        private final List<SoundEntry> all = new ArrayList<>();
        public SoundListWidget(MinecraftClient mc, int width, int height, int top, int entryH) { super(mc, width, height, top, entryH); }
        @Override protected void drawScrollbar(DrawContext context, int mouseX, int mouseY) {}
        public void refresh(int tab) {
            clearEntries(); all.clear();
            if (tab == 0) {
                String[][] presets = {{"Explosion", "minecraft:entity.generic.explode"}, {"Anvil", "minecraft:block.anvil.land"}, {"Totem", "minecraft:item.totem.use"}};
                for (String[] p : presets) all.add(new SoundEntry(p[0], p[1]));
            } else if (tab == 1) {
                Registries.SOUND_EVENT.getIds().stream().map(Identifier::toString).sorted().forEach(id -> all.add(new SoundEntry(id, id)));
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

    class SoundEntry extends AlwaysSelectedEntryListWidget.Entry<SoundEntry> {
        final String name, id;
        private final long createdAt = System.currentTimeMillis();
        SoundEntry(String name, String id) { this.name = name; this.id = id; }
        @Override
        public void render(DrawContext ctx, int mx, int my, boolean hovered, float delta) {
            int x = this.getX(); int y = this.getY();
            boolean selected = list.getSelectedOrNull() == this;
            if (selected) RenderUtils.drawRoundedRect(ctx, x, y, list.getRowWidth(), 32, 4, 0x4000A3FF);
            int availWidth = list.getRowWidth() - 10;
            drawMarqueeText(ctx, name, x + 5, y + 5, availWidth, selected ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY, selected);
            drawMarqueeText(ctx, id, x + 5, y + 18, availWidth, 0xFF666666, selected);
        }
        private void drawMarqueeText(DrawContext ctx, String text, int tx, int ty, int availWidth, int color, boolean selected) {
            int textWidth = textRenderer.getWidth(text);
            ctx.enableScissor(tx, ty, tx + availWidth, ty + textRenderer.fontHeight);
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
            ctx.drawTextWithShadow(textRenderer, Text.literal(text), tx + offset, ty, color);
            ctx.disableScissor();
        }
        @Override public boolean mouseClicked(Click click, boolean doubled) {
            list.setSelected(this);
            if (doubled && !id.equals("invalid")) previewSound(id);
            return true;
        }
        @Override public Text getNarration() { return Text.literal(name); }
    }
    class ConfirmRemoveScreen extends Screen {
        private final Screen parent;
        private final Runnable onConfirm;

        ConfirmRemoveScreen(Screen parent, Runnable onConfirm) {
            super(Text.literal("Confirm"));
            this.parent = parent;
            this.onConfirm = onConfirm;
        }

        @Override
        protected void init() {
            addDrawableChild(new ModernButtonWidget(width / 2 - 80, height / 2 - 15, 70, 20, Text.literal("§aYes"), () -> {
                onConfirm.run();
                client.setScreen(parent);
            }));
            addDrawableChild(new ModernButtonWidget(width / 2 + 10, height / 2 - 15, 70, 20, Text.literal("Cancel"), () -> {
                client.setScreen(parent);
            }));
        }

        @Override
        public void render(DrawContext ctx, int mx, int my, float delta) {
            RenderUtils.drawGradientRect(ctx, 0, 0, width, height, 0xCC000000, 0xCC000000);
            RenderUtils.drawOutline(ctx, width / 2 - 100, height / 2 - 40, 200, 60, 1, UiPalette.BORDER);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Remove?"), width / 2, height / 2 - 30, 0xFFFFFFFF);
            super.render(ctx, mx, my, delta);
        }

        @Override public void close() { client.setScreen(parent); }
    }
}
