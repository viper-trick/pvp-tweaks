package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SoundSubCategoryScreen extends Screen {
    private final Screen parent;
    private final String category;

    public SoundSubCategoryScreen(Screen parent, String category) {
        super(Text.literal(category + " Settings"));
        this.parent = parent;
        this.category = category;
    }

    @Override
    protected void init() {
        this.clearChildren();
        
        int x = width / 2 - 100;
        int y = 60;
        int spacing = 44;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        if (category.equals("Explosions")) {
            addSoundRow(x, y, "Crystal", cfg.soundCrystal); y += spacing;
            addSoundRow(x, y, "TNT", cfg.soundTnt); y += spacing;
            addSoundRow(x, y, "Bed", cfg.soundBed); y += spacing;
            addSoundRow(x, y, "Creeper", cfg.soundCreeper); y += spacing;
            addSlider(x, y, "Global Vol", cfg.explosionVolumePct, 0, 200, 100, v -> cfg.explosionVolumePct = v.intValue());
        } else if (category.equals("Combat")) {
            addSoundRow(x, y, "Hit", cfg.soundHit); y += spacing;
            addSoundRow(x, y, "Totem", cfg.soundTotem); y += spacing;
            addSoundRow(x, y, "Shield Brk", cfg.soundShieldBreak); y += spacing;
            addSoundRow(x, y, "Anchor", cfg.soundAnchor); y += spacing;
            addSlider(x, y, "Hit Vol", cfg.hitVolumePct, 0, 200, 100, v -> cfg.hitVolumePct = v.intValue());
        } else if (category.equals("Misc")) {
            addSoundRow(x, y, "Durability Low", cfg.soundDurabilityLow); y += spacing;
            addSoundRow(x, y, "Ghast", cfg.soundGhast); y += spacing;
            addSoundRow(x, y, "Wind Charge", cfg.soundWindCharge);
        }

        addDrawableChild(new ModernButtonWidget(width / 2 - 50, height - 35, 100, 20, Text.literal("Back"), () -> client.setScreen(parent)));
    }

    private void addSoundRow(int x, int y, String label, com.pvptweaks.config.SoundProfile profile) {
        addDrawableChild(new CustomSliderWidget(x, y, 115, 20, label + " P", profile.pitchPct, 0, 200, true, v -> profile.pitchPct = v.intValue()));
        addDrawableChild(new ModernButtonWidget(x + 120, y, 20, 20, Text.literal("\u21ba"), () -> {
            profile.pitchPct = 100;
            init();
        }));
        addDrawableChild(new ModernButtonWidget(x + 145, y, 55, 20, Text.literal("Adv..."), () -> {
            this.client.setScreen(new ModernSoundPickerScreen(this, profile, label, PvpTweaksConfig::save));
        }));
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter) {
        addDrawableChild(new CustomSliderWidget(x, y, 175, 20, label, val, min, max, true, setter)); 
        addDrawableChild(new ModernButtonWidget(x + 180, y, 20, 20, Text.literal("\u21ba"), () -> {
            setter.accept(defVal);
            init();
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, this.width, this.height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(context, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);
        
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7l" + category.toUpperCase() + " SOUNDS"), width / 2, 25, UiPalette.ACCENT_BLUE);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Customize pitch and advanced sound overrides"), width / 2, 38, UiPalette.TEXT_SECONDARY);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() { this.client.setScreen(parent); }
}
