package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ExplosionSettingsScreen extends Screen {
    private final Screen parent;

    public ExplosionSettingsScreen(Screen parent) {
        super(Component.literal("Explosion Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = width / 2 - 190;
        int y = 60;
        int spacing = 36;
        int x2 = width / 2 + 10;

        // Column 1: Volume
        addSlider(x, y, "TNT Volume", cfg.tntExplosionVolumePct, 0, 200, v -> cfg.tntExplosionVolumePct = v.intValue()); y += spacing;
        addSlider(x, y, "Creeper Volume", cfg.creeperExplosionVolumePct, 0, 200, v -> cfg.creeperExplosionVolumePct = v.intValue()); y += spacing;
        addSlider(x, y, "Bed Volume", cfg.bedExplosionVolumePct, 0, 200, v -> cfg.bedExplosionVolumePct = v.intValue()); y += spacing;
        addSlider(x, y, "Ghast Volume", cfg.ghastExplosionVolumePct, 0, 200, v -> cfg.ghastExplosionVolumePct = v.intValue()); y += spacing;
        addSlider(x, y, "Wind Volume", cfg.windChargeVolumePct, 0, 200, v -> cfg.windChargeVolumePct = v.intValue()); y += spacing;
        addSlider(x, y, "Anchor Volume", cfg.respawnAnchorExplosionPct, 0, 200, v -> cfg.respawnAnchorExplosionPct = v.intValue());

        // Column 2: Particles
        y = 60;
        addSlider(x2, y, "TNT Particles", cfg.tntExplosionParticlePct, 0, 200, v -> cfg.tntExplosionParticlePct = v.intValue()); y += spacing;
        addSlider(x2, y, "Creeper Particles", cfg.creeperExplosionParticlePct, 0, 200, v -> cfg.creeperExplosionParticlePct = v.intValue()); y += spacing;
        addSlider(x2, y, "Bed Particles", cfg.bedExplosionParticlePct, 0, 200, v -> cfg.bedExplosionParticlePct = v.intValue()); y += spacing;
        addSlider(x2, y, "Ghast Particles", cfg.ghastExplosionParticlePct, 0, 200, v -> cfg.ghastExplosionParticlePct = v.intValue()); y += spacing;
        addSlider(x2, y, "Wind Particles", cfg.windChargeParticlePct, 0, 200, v -> cfg.windChargeParticlePct = v.intValue()); y += spacing;
        addSlider(x2, y, "Anchor Particles", cfg.anchorExplosionParticlePct, 0, 200, v -> cfg.anchorExplosionParticlePct = v.intValue());

        addRenderableWidget(new ModernButtonWidget(width / 2 - 50, height - 35, 100, 20, Component.literal("Done"), () -> {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
        }));
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, java.util.function.Consumer<Double> setter) {
        addRenderableWidget(new CustomSliderWidget(x, y, 180, 20, label, val, min, max, true, setter));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);
        ctx.centeredText(font, Component.literal("\u00a7lDETAILED EXPLOSION SETTINGS"), width / 2, 25, UiPalette.ACCENT_BLUE);
        super.extractRenderState(ctx, mx, my, delta);
    }
}
