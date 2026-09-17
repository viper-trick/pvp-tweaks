package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

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
        addRenderableWidget(new ModernButtonWidget(width / 2 - 125, btnY, 80, 20, Component.literal("Reset"), () -> {
            config.cpsX = 5f; config.cpsY = 15f; config.cpsScale = 1.0f;
        }));
        addRenderableWidget(new ModernButtonWidget(width / 2 - 40, btnY, 80, 20, Component.literal("Save"), () -> {
            PvpTweaksConfig.save(); minecraft.setScreenAndShow(parent);
        }));
        addRenderableWidget(new ModernButtonWidget(width / 2 + 45, btnY, 80, 20, Component.literal("Cancel"), () -> {
            minecraft.setScreenAndShow(parent);
        }));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, width, 40, 0x88000000, 0x00000000);
        RenderUtils.drawGradientRect(context, 0, height - 50, width, 50, 0x00000000, 0x88000000);
        context.text(font, Component.literal("\u00a7lCPS ADJUSTER"), width / 2, 10, UiPalette.ACCENT_BLUE);
        context.text(font, Component.literal("MouseButtonEvent and drag to move"), width / 2, 22, UiPalette.TEXT_SECONDARY);
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        String text = config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0";
        var matrices = context.pose();
        matrices.pushMatrix();
        matrices.scale((float)config.cpsScale, (float)config.cpsScale);
        int color = config.cpsRainbow ? CpsHudRenderer.getRainbowColor() : config.cpsColor;
        context.text(font, text, (int)(renderX / config.cpsScale), (int)(renderY / config.cpsScale), color);
        if (dragging) {
            int w = font.width(text);
            RenderUtils.drawOutline(context, (int)(renderX / config.cpsScale) - 2, (int)(renderY / config.cpsScale) - 2, w + 4, 12, 1, UiPalette.ACCENT_BLUE);
        }
        matrices.popMatrix();
        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        int w = (int) (font.width(config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0") * config.cpsScale);
        int h = (int) (10 * config.cpsScale);
        if (click.x() >= renderX && click.x() <= renderX + w && click.y() >= renderY && click.y() <= renderY + h) {
            this.dragging = true;
            this.dragOffsetX = click.x() - renderX;
            this.dragOffsetY = click.y() - renderY;
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        if (this.dragging) {
            config.cpsX = (float) (((click.x() - dragOffsetX) * 100.0) / width);
            config.cpsY = (float) (((click.y() - dragOffsetY) * 100.0) / height);
            config.cpsX = Mth.clamp(config.cpsX, 0, 95);
            config.cpsY = Mth.clamp(config.cpsY, 0, 95);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) { this.dragging = false; return super.mouseReleased(click); }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override public boolean isPauseScreen() { return false; }
}
