package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class CpsAdjusterScreen extends Screen {
    private final Screen parent;
    private final PvpTweaksConfig config;
    private boolean dragging = false;
    private double dragOffsetX, dragOffsetY;

    public CpsAdjusterScreen(Screen parent) {
        super(Component.literal("CPS Adjuster"));
        this.parent = parent;
        this.config = PvpTweaksConfig.get();
    }

    @Override
    protected void init() {
        int btnY = height - 35;
        addRenderableWidget(new ModernButtonWidget(width / 2 - 125, btnY, 80, 20, (Component)Component.literal("Reset"), () -> {
            config.cpsX = 5f; config.cpsY = 15f; config.cpsScale = 1.0f;
        }));
        addRenderableWidget(new ModernButtonWidget(width / 2 - 40, btnY, 80, 20, (Component)Component.literal("Save"), () -> {
            PvpTweaksConfig.save(); minecraft.setScreen(parent);
        }));
        addRenderableWidget(new ModernButtonWidget(width / 2 + 45, btnY, 80, 20, (Component)Component.literal("Cancel"), () -> {
            minecraft.setScreen(parent);
        }));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, width, 40, 0x88000000, 0x00000000);
        RenderUtils.drawGradientRect(context, 0, height - 50, width, 50, 0x00000000, 0x88000000);
        context.drawCenteredString(font, Component.literal("\u00a7lCPS ADJUSTER"), width / 2, 10, UiPalette.ACCENT_BLUE);
        context.drawCenteredString(font, Component.literal("Click and drag to move"), width / 2, 22, UiPalette.TEXT_SECONDARY);
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        String text = config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0";
        var matrices = context.pose();
        matrices.pushMatrix();
        matrices.scale((float)config.cpsScale, (float)config.cpsScale);
        int color = config.cpsRainbow ? CpsHudRenderer.getRainbowColor() : config.cpsColor;
        context.drawString(font, text, (int)(renderX / config.cpsScale), (int)(renderY / config.cpsScale), color);
        if (dragging) {
            int w = font.width(text);
            RenderUtils.drawOutline(context, (int)(renderX / config.cpsScale) - 2, (int)(renderY / config.cpsScale) - 2, w + 4, 12, 1, UiPalette.ACCENT_BLUE);
        }
        matrices.popMatrix();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        int w = (int) (font.width(config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0") * config.cpsScale);
        int h = (int) (10 * config.cpsScale);
        if (mouseX >= renderX && mouseX <= renderX + w && mouseY >= renderY && mouseY <= renderY + h) {
            this.dragging = true;
            this.dragOffsetX = mouseX - renderX;
            this.dragOffsetY = mouseY - renderY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.dragging) {
            config.cpsX = (float) (((mouseX - dragOffsetX) * 100.0) / width);
            config.cpsY = (float) (((mouseY - dragOffsetY) * 100.0) / height);
            config.cpsX = Mth.clamp(config.cpsX, 0, 95);
            config.cpsY = Mth.clamp(config.cpsY, 0, 95);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { this.dragging = false; return super.mouseReleased(mouseX, mouseY, button); }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override public boolean isPauseScreen() { return false; }
}
