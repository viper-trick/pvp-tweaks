package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

public class CpsAdjusterScreen extends Screen {
    private final Screen parent;
    private final PvpTweaksConfig config;
    private boolean dragging = false;
    private double dragOffsetX, dragOffsetY;

    public CpsAdjusterScreen(Screen parent) {
        super(Text.literal("CPS Adjuster"));
        this.parent = parent;
        this.config = PvpTweaksConfig.get();
    }

    @Override
    protected void init() {
        int btnY = height - 35;
        addDrawableChild(new ModernButtonWidget(width / 2 - 125, btnY, 80, 20, (Text)Text.literal("Reset"), () -> {
            config.cpsX = 5f; config.cpsY = 15f; config.cpsScale = 1.0f;
        }));
        addDrawableChild(new ModernButtonWidget(width / 2 - 40, btnY, 80, 20, (Text)Text.literal("Save"), () -> {
            PvpTweaksConfig.save(); client.setScreen(parent);
        }));
        addDrawableChild(new ModernButtonWidget(width / 2 + 45, btnY, 80, 20, (Text)Text.literal("Cancel"), () -> {
            client.setScreen(parent);
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, width, 40, 0x88000000, 0x00000000);
        RenderUtils.drawGradientRect(context, 0, height - 50, width, 50, 0x00000000, 0x88000000);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lCPS ADJUSTER"), width / 2, 10, UiPalette.ACCENT_BLUE);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Click and drag to move"), width / 2, 22, UiPalette.TEXT_SECONDARY);
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        String text = config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0";
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scale((float)config.cpsScale, (float)config.cpsScale);
        int color = config.cpsRainbow ? CpsHudRenderer.getRainbowColor() : config.cpsColor;
        context.drawTextWithShadow(textRenderer, text, (int)(renderX / config.cpsScale), (int)(renderY / config.cpsScale), color);
        if (dragging) {
            int w = textRenderer.getWidth(text);
            RenderUtils.drawOutline(context, (int)(renderX / config.cpsScale) - 2, (int)(renderY / config.cpsScale) - 2, w + 4, 12, 1, UiPalette.ACCENT_BLUE);
        }
        matrices.popMatrix();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int renderX = (int) (width * (config.cpsX / 100.0f));
        int renderY = (int) (height * (config.cpsY / 100.0f));
        int w = (int) (textRenderer.getWidth(config.cpsShowLabel ? "L: 0  R: 0" : "0 | 0") * config.cpsScale);
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
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (this.dragging) {
            config.cpsX = (float) (((click.x() - dragOffsetX) * 100.0) / width);
            config.cpsY = (float) (((click.y() - dragOffsetY) * 100.0) / height);
            config.cpsX = MathHelper.clamp(config.cpsX, 0, 95);
            config.cpsY = MathHelper.clamp(config.cpsY, 0, 95);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) { this.dragging = false; return super.mouseReleased(click); }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override public boolean shouldPause() { return false; }
}
