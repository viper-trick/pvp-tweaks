package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ParticlesScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public ParticlesScreen(Screen parent) {
        super(Text.literal("Particles"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;
        int spacing = 26;

        addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
            Text.literal("Crit Particles: " + (cfg.showHitParticles ? "ON" : "OFF")), () -> {
            cfg.showHitParticles = !cfg.showHitParticles;
            refresh();
        }));
        y += spacing;

        addSlider(x, y, "Totem Particles", cfg.totemPopScalePct, 0, 200, 50,
            v -> cfg.totemPopScalePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Crystal Particles", cfg.crystalParticlePct, 0, 200, 0,
            v -> cfg.crystalParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Crystal Expl Part.", cfg.enderExplosionParticlePct, 0, 200, 5,
            v -> cfg.enderExplosionParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Anchor Expl Part.", cfg.anchorExplosionParticlePct, 0, 200, 0,
            v -> cfg.anchorExplosionParticlePct = v.intValue());
        y += spacing;

        addDrawableChild(new ModernButtonWidget(this.width - PANEL_W + 20, height - 35, 60, 20,
            Text.literal("Done"), () -> {
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
        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a7lPARTICLES"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
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
