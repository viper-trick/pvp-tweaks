package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class DurabilityScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public DurabilityScreen(Screen parent) {
        super(Component.literal("Durability HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("Durability HUD: " + (cfg.durabilityHudEnabled ? "ON" : "OFF")), () -> {
            cfg.durabilityHudEnabled = !cfg.durabilityHudEnabled;
            refresh();
        })); y += 26;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("Alert Sound Once: " + (cfg.durabilityAlertSoundOnce ? "ON" : "OFF")), () -> {
            cfg.durabilityAlertSoundOnce = !cfg.durabilityAlertSoundOnce;
            refresh();
        })); y += 26;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("\u26E8 Move Durability"), () -> {
            minecraft.setScreen(new DurabilityAdjusterScreen(this));
        })); y += 26;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("\ud83d\udd14 Alert Sound..."), () -> {
            minecraft.setScreen(new ModernSoundPickerScreen(this, cfg.soundDurabilityLow, "Durability Low", PvpTweaksConfig::save));
        })); y += 26;

        addRenderableWidget(new ModernButtonWidget(this.width - PANEL_W + 20, height - 35, 60, 20,
            Component.literal("Done"), () -> {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
        }));
    }

    private void refresh() {
        this.clearWidgets();
        this.init();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, width - PANEL_W, 0, PANEL_W, height, 0xCC101010, 0xCC050505);
        RenderUtils.drawOutline(ctx, width - PANEL_W - 1, 0, 1, height, 1, UiPalette.BORDER);
        ctx.text(font, Component.literal("\u00a7lDURABILITY HUD"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
        super.extractRenderState(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_H || input.key() == GLFW.GLFW_KEY_ESCAPE) {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override public boolean isPauseScreen() { return false; }
}
