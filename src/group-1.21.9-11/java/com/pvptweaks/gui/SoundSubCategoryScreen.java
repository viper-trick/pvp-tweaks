package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoundSubCategoryScreen extends Screen {
    private final Screen parent;
    private final String category;

    public SoundSubCategoryScreen(Screen parent, String category) {
        super(Component.literal(category + " Settings"));
        this.parent = parent;
        this.category = category;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        
        int x = width / 2 - 100;
        int y = 60;
        int spacing = 26;
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
        } else if (category.equals("Custom")) {
            for (java.util.Map.Entry<String, com.pvptweaks.config.SoundProfile> e : cfg.extraSounds.entrySet()) {
                String id = e.getKey();
                com.pvptweaks.config.SoundProfile profile = e.getValue();
                String shortLabel = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1) : id;
                addSoundRow(x, y, shortLabel, profile);
                addRenderableWidget(new ModernButtonWidget(x + 205, y, 20, 20, Component.literal("\u2716"), () -> {
                    cfg.extraSounds.remove(id);
                    PvpTweaksConfig.save();
                    init();
                }));
                y += spacing;
            }
            if (cfg.extraSounds.isEmpty()) {
                addRenderableWidget(new ModernButtonWidget(x, y, 200, 20, Component.literal("No extra sounds yet — add one below"), () -> {}));
                y += spacing;
            }
            addRenderableWidget(new ModernButtonWidget(x, y, 110, 20, Component.literal("+ Add Sound"), () -> {
                minecraft.setScreen(new SoundSearchScreen(this));
            }));
        }

        addRenderableWidget(new ModernButtonWidget(width / 2 - 50, height - 35, 100, 20, Component.literal("Back"), () -> {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
        }));
    }

    private void addSoundRow(int x, int y, String label, com.pvptweaks.config.SoundProfile profile) {
        addRenderableWidget(new CustomSliderWidget(x, y, 115, 20, label + " V", profile.volumePct, 0, 200, true, v -> profile.volumePct = v.intValue()));
        addRenderableWidget(new ModernButtonWidget(x + 120, y, 20, 20, Component.literal("\u21ba"), () -> {
            profile.volumePct = 100;
            init();
        }));
        addRenderableWidget(new ModernButtonWidget(x + 145, y, 55, 20, Component.literal("Adv..."), () -> {
            this.minecraft.setScreen(new ModernSoundPickerScreen(this, profile, label, PvpTweaksConfig::save));
        }));
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter) {
        addRenderableWidget(new CustomSliderWidget(x, y, 175, 20, label, val, min, max, true, setter)); 
        addRenderableWidget(new ModernButtonWidget(x + 180, y, 20, 20, Component.literal("\u21ba"), () -> {
            setter.accept(defVal);
            init();
        }));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, this.width, this.height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(context, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);
        
        context.drawCenteredString(font, Component.literal("\u00a7l" + category.toUpperCase() + " SOUNDS"), width / 2, 25, UiPalette.ACCENT_BLUE);
        context.drawCenteredString(font, Component.literal("Customize pitch and advanced sound overrides"), width / 2, 38, UiPalette.TEXT_SECONDARY);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() { this.minecraft.setScreen(parent); }
}
