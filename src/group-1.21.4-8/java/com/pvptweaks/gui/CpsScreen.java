package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class CpsScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public CpsScreen(Screen parent) {
        super(Component.literal("CPS HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("CPS HUD: " + (cfg.cpsEnabled ? "ON" : "OFF")), () -> {
            cfg.cpsEnabled = !cfg.cpsEnabled;
            refresh();
        })); y += 26;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("\u26ef Move CPS"), () -> {
            minecraft.setScreen(new CpsAdjusterScreen(this));
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
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, width - PANEL_W, 0, PANEL_W, height, 0xCC101010, 0xCC050505);
        RenderUtils.drawOutline(ctx, width - PANEL_W - 1, 0, 1, height, 1, UiPalette.BORDER);
        ctx.drawString(font, Component.literal("\u00a7lCPS HUD"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_H || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean isPauseScreen() { return false; }
}
