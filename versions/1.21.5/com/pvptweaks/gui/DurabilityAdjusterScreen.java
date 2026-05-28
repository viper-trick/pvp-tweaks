package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class DurabilityAdjusterScreen extends Screen {
    private final Screen parent;
    private boolean dragging = false;
    private double dragOffsetX, dragOffsetY;

    public DurabilityAdjusterScreen(Screen parent) {
        super(Text.literal("Durability Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int btnY = height - 35;
        addDrawableChild(new ModernButtonWidget(20, btnY, 70, 20, (Text)Text.literal("Align: " + cfg.durabilityHudAlign), () -> {
            cfg.durabilityHudAlign = "vertical".equals(cfg.durabilityHudAlign) ? "horizontal" : "vertical";
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(95, btnY, 70, 20, (Text)Text.literal("Frame: " + (cfg.durabilityHudBackground ? "ON" : "OFF")), () -> {
            cfg.durabilityHudBackground = !cfg.durabilityHudBackground;
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(170, btnY, 70, 20, (Text)Text.literal("Exact: " + (cfg.durabilityHudShowExact ? "ON" : "OFF")), () -> {
            cfg.durabilityHudShowExact = !cfg.durabilityHudShowExact;
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(width - 160, btnY, 70, 20, (Text)Text.literal("Reset"), () -> {
            cfg.durabilityHudX = 5f; cfg.durabilityHudY = 5f;
        }));
        addDrawableChild(new ModernButtonWidget(width - 85, btnY, 70, 20, (Text)Text.literal("Done"), () -> {
            PvpTweaksConfig.save(); client.setScreen(parent);
        }));
    }

    private void refresh() { this.clearChildren(); this.init(); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, width, 40, 0x88000000, 0x00000000);
        RenderUtils.drawGradientRect(context, 0, height - 50, width, 50, 0x00000000, 0x88000000);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lDURABILITY HUD ADJUSTER"), width / 2, 10, UiPalette.ACCENT_BLUE);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag to move, arrows for precise nudging"), width / 2, 22, UiPalette.TEXT_SECONDARY);
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int hudX = (int) (width * (cfg.durabilityHudX / 100.0f));
        int hudY = (int) (height * (cfg.durabilityHudY / 100.0f));
        if (dragging) RenderUtils.drawOutline(context, hudX - 5, hudY - 5, 80, 80, 1, UiPalette.ACCENT_BLUE);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int hudX = (int) (width * (cfg.durabilityHudX / 100.0f));
        int hudY = (int) (height * (cfg.durabilityHudY / 100.0f));
        if (mouseX >= hudX - 10 && mouseX <= hudX + 100 && mouseY >= hudY - 10 && mouseY <= hudY + 100) {
            dragging = true;
            dragOffsetX = mouseX - hudX;
            dragOffsetY = mouseY - hudY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudX = (float) MathHelper.clamp(((mouseX - dragOffsetX) * 100.0) / width, 0, 95);
            cfg.durabilityHudY = (float) MathHelper.clamp(((mouseY - dragOffsetY) * 100.0) / height, 0, 95);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { dragging = false; return super.mouseReleased(mouseX, mouseY, button); }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float step = 0.1f;
        if (keyCode == GLFW.GLFW_KEY_UP) { cfg.durabilityHudY -= step; }
        else if (keyCode == GLFW.GLFW_KEY_DOWN) { cfg.durabilityHudY += step; }
        else if (keyCode == GLFW.GLFW_KEY_LEFT) { cfg.durabilityHudX -= step; }
        else if (keyCode == GLFW.GLFW_KEY_RIGHT) { cfg.durabilityHudX += step; }
        else if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_H) {
            PvpTweaksConfig.save(); client.setScreen(parent); return true;
        }
        cfg.durabilityHudX = MathHelper.clamp(cfg.durabilityHudX, 0, 100);
        cfg.durabilityHudY = MathHelper.clamp(cfg.durabilityHudY, 0, 100);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override public boolean shouldPause() { return false; }
}
