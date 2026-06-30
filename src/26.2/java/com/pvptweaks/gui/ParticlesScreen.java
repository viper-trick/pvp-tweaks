package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ParticlesScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_W = 220;

    public ParticlesScreen(Screen parent) {
        super(Component.literal("Particles"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = this.width - PANEL_W + 20;
        int y = 45;
        int spacing = 26;

        addRenderableWidget(new ModernButtonWidget(x, y, 180, 20,
            Component.literal("Crit Particles: " + (cfg.showHitParticles ? "ON" : "OFF")), () -> {
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

        addSlider(x, y, "TNT Particles", cfg.tntExplosionParticlePct, 0, 200, 100,
            v -> cfg.tntExplosionParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Creeper Particles", cfg.creeperExplosionParticlePct, 0, 200, 100,
            v -> cfg.creeperExplosionParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Bed Particles", cfg.bedExplosionParticlePct, 0, 200, 100,
            v -> cfg.bedExplosionParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Ghast Particles", cfg.ghastExplosionParticlePct, 0, 200, 100,
            v -> cfg.ghastExplosionParticlePct = v.intValue());
        y += spacing;

        addSlider(x, y, "Wind Particles", cfg.windChargeParticlePct, 0, 200, 100,
            v -> cfg.windChargeParticlePct = v.intValue());
        y += spacing;

        addRenderableWidget(new ModernButtonWidget(this.width - PANEL_W + 20, height - 35, 60, 20,
            Component.literal("Done"), () -> {
            PvpTweaksConfig.save();
            minecraft.setScreenAndShow(parent);
        }));
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter) {
        addRenderableWidget(new CustomSliderWidget(x, y, 115, 20, label, val, min, max, true, setter));
        addRenderableWidget(new ModernButtonWidget(x + 120, y, 20, 20, Component.literal("\u21ba"), () -> {
            setter.accept(defVal);
            refresh();
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
        ctx.text(font, Component.literal("\u00a7lPARTICLES"), width - PANEL_W + 20, 15, UiPalette.ACCENT_BLUE);
        super.extractRenderState(ctx, mx, my, delta);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_H || input.key() == GLFW.GLFW_KEY_ESCAPE) {
            PvpTweaksConfig.save();
            minecraft.setScreenAndShow(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override public boolean isPauseScreen() { return false; }
}
