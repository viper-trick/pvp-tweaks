package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class CpsAdjusterScreen extends Screen {

    private final Screen parent;
    private PvpTweaksConfig config;

    private float currentCpsX;
    private float currentCpsY;
    private int currentCpsColor;
    private boolean currentCpsRainbow;
    private boolean currentCpsShadow;
    private float currentCpsScale;
    private boolean currentCpsShowLabel;

    private boolean dragging = false;
    private double dragOffsetX, dragOffsetY;

    public CpsAdjusterScreen(Screen parent) {
        super(Text.literal("CPS Adjuster"));
        this.parent = parent;
        this.config = PvpTweaksConfig.get();
        
        this.currentCpsX = config.cpsX;
        this.currentCpsY = config.cpsY;
        this.currentCpsColor = config.cpsColor;
        this.currentCpsRainbow = config.cpsRainbow;
        this.currentCpsShadow = config.cpsShadow;
        this.currentCpsScale = config.cpsScale;
        this.currentCpsShowLabel = config.cpsShowLabel;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 120;
        int buttonHeight = 20;
        int padding = 5;
        int centerX = this.width / 2;
        int startY = this.height - 40 - buttonHeight;

        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), (button) -> {
            config.cpsX = this.currentCpsX;
            config.cpsY = this.currentCpsY;
            config.cpsColor = this.currentCpsColor;
            config.cpsRainbow = this.currentCpsRainbow;
            config.cpsShadow = this.currentCpsShadow;
            config.cpsScale = this.currentCpsScale;
            config.cpsShowLabel = this.currentCpsShowLabel;
            PvpTweaksConfig.save();
            client.setScreen(parent);
        }).position(centerX - buttonWidth / 2 - buttonWidth - padding, startY).size(buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), (button) -> {
            client.setScreen(parent);
        }).position(centerX - buttonWidth / 2, startY).size(buttonWidth, buttonHeight).build());
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), (button) -> {
            this.currentCpsX = 5f;
            this.currentCpsY = 15f;
            this.currentCpsColor = 0xFFFFFFFF;
            this.currentCpsRainbow = false;
            this.currentCpsShadow = true;
            this.currentCpsScale = 1.0f;
            this.currentCpsShowLabel = true;
        }).position(centerX + buttonWidth + padding, startY).size(buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int screenWidth = this.width;
        int screenHeight = this.height;
        int renderX = (int) (screenWidth * (currentCpsX / 100.0f));
        int renderY = (int) (screenHeight * (currentCpsY / 100.0f));

        String text;
        if (currentCpsShowLabel) {
            text = "L: " + CpsTracker.getLeftCps() + "  R: " + CpsTracker.getRightCps();
        } else {
            text = CpsTracker.getLeftCps() + " | " + CpsTracker.getRightCps();
        }

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scale(currentCpsScale, currentCpsScale);
        
        float scaledRenderX = renderX / currentCpsScale;
        float scaledRenderY = renderY / currentCpsScale;

        int color = currentCpsRainbow ? CpsHudRenderer.getRainbowColor() : currentCpsColor;

        if (currentCpsShadow) {
            context.drawTextWithShadow(textRenderer, text, (int)scaledRenderX, (int)scaledRenderY, color);
        } else {
            context.drawText(textRenderer, text, (int)scaledRenderX, (int)scaledRenderY, color, false);
        }
        matrices.popMatrix();

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag the CPS counter to reposition"), this.width / 2, 10, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Use buttons below to change other settings"), this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }

        int renderX = (int) (this.width * (currentCpsX / 100.0f));
        int renderY = (int) (this.height * (currentCpsY / 100.0f));
        
        int clickAreaWidth = 100;
        int clickAreaHeight = 20;

        if (click.x() >= renderX && click.x() <= renderX + clickAreaWidth && click.y() >= renderY && click.y() <= renderY + clickAreaHeight) {
             this.dragging = true;
             this.dragOffsetX = click.x() - renderX;
             this.dragOffsetY = click.y() - renderY;
             return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        if (this.dragging) {
            this.currentCpsX = (float) (((click.x() - dragOffsetX) * 100.0) / (double)this.width);
            this.currentCpsY = (float) (((click.y() - dragOffsetY) * 100.0) / (double)this.height);

            this.currentCpsX = Math.max(0, Math.min(100, this.currentCpsX));
            this.currentCpsY = Math.max(0, Math.min(100, this.currentCpsY));
            
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        if (this.dragging) {
            this.dragging = false;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
