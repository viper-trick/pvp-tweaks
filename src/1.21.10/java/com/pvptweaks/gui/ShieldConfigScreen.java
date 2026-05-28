package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

public class ShieldConfigScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public ShieldConfigScreen(Screen parent) {
        super(Text.literal("Shield Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;
        int spacing = 26;

        addSlider(x, y, "Offset X", cfg.shieldOffsetX, -100, 100, 0, v -> cfg.shieldOffsetX = v.intValue()); y += spacing;
        addSlider(x, y, "Offset Y", cfg.shieldOffsetY, -100, 100, 0, v -> cfg.shieldOffsetY = v.intValue()); y += spacing;
        addSlider(x, y, "Offset Z", cfg.shieldOffsetZ, -100, 100, 0, v -> cfg.shieldOffsetZ = v.intValue()); y += spacing * 1.35;
        
        addSlider(x, y, "Rot X", cfg.shieldRotX, -180, 180, 0, v -> cfg.shieldRotX = v.intValue()); y += spacing;
        addSlider(x, y, "Rot Y", cfg.shieldRotY, -180, 180, 0, v -> cfg.shieldRotY = v.intValue()); y += spacing;
        addSlider(x, y, "Rot Z", cfg.shieldRotZ, -180, 180, 0, v -> cfg.shieldRotZ = v.intValue()); y += spacing * 1.35;

        addSlider(x, y, "Scale", cfg.shieldScalePct, 25, 300, 100, v -> cfg.shieldScalePct = v.intValue());
        
        addDrawableChild(new ModernButtonWidget(this.width - PANEL_W + 20, height - 35, 50, 20, Text.literal("Vanilla"), () -> {
            cfg.shieldOffsetX = 0; cfg.shieldOffsetY = 0; cfg.shieldOffsetZ = 0;
            cfg.shieldRotX = 0; cfg.shieldRotY = 0; cfg.shieldRotZ = 0;
            cfg.shieldScalePct = 100;
            refresh();
        }));

        addDrawableChild(new ModernButtonWidget(this.width - PANEL_W + 75, height - 35, 60, 20, Text.literal("Profile"), () -> {
            cfg.shieldOffsetX = -55; cfg.shieldOffsetY = -1; cfg.shieldOffsetZ = 10;
            cfg.shieldRotX = 0; cfg.shieldRotY = 0; cfg.shieldRotZ = 0;
            cfg.shieldScalePct = 70;
            refresh();
        }));

        addDrawableChild(new ModernButtonWidget(this.width - PANEL_W + 140, height - 35, 60, 20, Text.literal("Done"), () -> {
            PvpTweaksConfig.save();
            client.setScreen(parent);
        }));
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter) {
        addDrawableChild(new CustomSliderWidget(x, y, 115, 20, label, val, min, max, true, setter));
        addDrawableChild(new ModernButtonWidget(x + 120, y, 20, 20, Text.literal("\u21ba"), () -> {
            setter.accept(defVal);
            refresh();
        }));
    }

    private void refresh() { this.clearChildren(); this.init(); }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, width - PANEL_W, 0, PANEL_W, height, 0xCC101010, 0xCC050505);
        RenderUtils.drawOutline(ctx, width - PANEL_W - 1, 0, 1, height, 1, UiPalette.BORDER);
        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a7lSHIELD ADJUSTER"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Live Preview Active"), width - PANEL_W + 20, 27, UiPalette.TEXT_SECONDARY);

        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_H || input.key() == GLFW.GLFW_KEY_ESCAPE) {
            PvpTweaksConfig.save();
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override public boolean shouldPause() { return false; }
}
