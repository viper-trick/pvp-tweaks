package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class DurabilityScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public DurabilityScreen(Screen parent) {
        super(Text.literal("Durability HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;

        addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
            Text.literal("Durability HUD: " + (cfg.durabilityHudEnabled ? "ON" : "OFF")), () -> {
            cfg.durabilityHudEnabled = !cfg.durabilityHudEnabled;
            refresh();
        })); y += 26;

        addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
            Text.literal("Alert Sound Once: " + (cfg.durabilityAlertSoundOnce ? "ON" : "OFF")), () -> {
            cfg.durabilityAlertSoundOnce = !cfg.durabilityAlertSoundOnce;
            refresh();
        })); y += 26;

        addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
            Text.literal("\u26E8 Move Durability"), () -> {
            client.setScreen(new DurabilityAdjusterScreen(this));
        })); y += 26;

        addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
            Text.literal("\ud83d\udd14 Alert Sound..."), () -> {
            client.setScreen(new ModernSoundPickerScreen(this, cfg.soundDurabilityLow, "Durability Low", PvpTweaksConfig::save));
        })); y += 26;

        addDrawableChild(new ModernButtonWidget(this.width - PANEL_W + 20, height - 35, 60, 20,
            Text.literal("Done"), () -> {
            PvpTweaksConfig.save();
            client.setScreen(parent);
        }));
    }

    private void refresh() {
        this.clearChildren();
        this.init();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, width - PANEL_W, 0, PANEL_W, height, 0xCC101010, 0xCC050505);
        RenderUtils.drawOutline(ctx, width - PANEL_W - 1, 0, 1, height, 1, UiPalette.BORDER);
        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a7lDURABILITY HUD"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_H || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            PvpTweaksConfig.save();
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean shouldPause() { return false; }
}
