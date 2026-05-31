package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.PvpTweaksProfiles;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PvpTweaksHubScreen extends Screen {
    private final Screen parent;
    private final List<Category> categories = new ArrayList<>();
    private Category activeCategory;
    private boolean listeningForKeybind = false;
    
    private int sidebarWidth = 95;
    private double sidebarScroll = 0;
    private double contentScroll = 0;
    private static boolean fireChanged = false;
    private static String lastCategoryName = "Home";

    public PvpTweaksHubScreen(Screen parent) {
        this(parent, lastCategoryName);
    }

    public PvpTweaksHubScreen(Screen parent, String initialCategory) {
        super(Text.literal("PVP Tweaks Hub"));
        this.parent = parent;
        
        categories.add(new Category("Home", "\u2605"));
        categories.add(new Category("Item Sizes", "\ud83d\udca0"));
        categories.add(new Category("Visuals", "\ud83d\udc41"));
        categories.add(new Category("HUD", "\ud83d\udcca"));
        categories.add(new Category("Sounds", "\ud83d\udd0a"));
        categories.add(new Category("Optimization", "\u26a1"));
        categories.add(new Category("Profiles", "\ud83d\udcc1"));
        
        lastCategoryName = initialCategory;
        for (Category cat : categories) {
            if (cat.name.equalsIgnoreCase(initialCategory)) {
                activeCategory = cat;
                break;
            }
        }
        if (activeCategory == null) activeCategory = categories.get(0);
    }

    @Override
    protected void init() {
        refreshCategoryWidgets();
    }

    private void refreshCategoryWidgets() {
        this.clearChildren();
        
        this.addDrawableChild(new ModernButtonWidget(this.width - 65, 12, 55, 20, Text.literal("Done"), () -> {
            PvpTweaksConfig.save();
            if (fireChanged) {
                if (client.worldRenderer != null) client.worldRenderer.reload();
                client.reloadResources();
                fireChanged = false;
            }
            this.client.setScreen(parent);
        }));

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = sidebarWidth + 25;
        int y = 65 - (int)contentScroll;
        int spacing = 44;
        int colWidth = 200;

        if (activeCategory.name.equals("Home")) {
            y += 10;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\u2694 Apply Competitive Preset"), () -> { PvpTweaksProfiles.applyCompetitive(); refreshCategoryWidgets(); }));
            y += spacing;
            
            String keyName = com.pvptweaks.PvpTweaksClient.openMenuKeyBinding.getBoundKeyLocalizedText().getString();
            Text btnText = listeningForKeybind ? Text.literal("> \u00a7e???\u00a7r <") : Text.literal("Menu Key: " + keyName);
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, btnText, () -> {
                listeningForKeybind = true;
                refreshCategoryWidgets();
            }));
            y += spacing;
            
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\u21ba Reset to Vanilla"), () -> { PvpTweaksProfiles.applyVanilla(); refreshCategoryWidgets(); }));
            y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\u26ef Reset to Profile Defaults"), () -> { PvpTweaksProfiles.applyDefault(); refreshCategoryWidgets(); }));
        } else if (activeCategory.name.equals("Item Sizes")) {
            int x1 = x;
            int x2 = x + colWidth;

            y += 5;

            // Global slider row
            String[] modes = {"listed", "unlisted", "custom", "off"};
            String[] modeLabels = {"\u00a7eListed", "\u00a7eUnlisted", "\u00a7eCustom", "\u00a77Off"};
            int modeIdx = java.util.Arrays.asList(modes).indexOf(cfg.itemScaleGlobalMode);
            if (modeIdx < 0) modeIdx = 3;
            final int fModeIdx = modeIdx;

            addDrawableChild(new CustomSliderWidget(x1, y, 155, 20, "Global", cfg.itemScaleGlobalPct, 25, 300, true,
                v -> { cfg.itemScaleGlobalPct = v.intValue(); refreshCategoryWidgets(); }));
            y += spacing;

            // Mode selector row
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20,
                Text.literal("Mode: " + modeLabels[fModeIdx]), () -> {
                int next = (fModeIdx + 1) % 4;
                cfg.itemScaleGlobalMode = modes[next];
                refreshCategoryWidgets();
            }));
            y += spacing;

            // Both columns start here
            int firstItemRow = y;

            // Left column
            addItemScaleSlider(x1, y, "Sword", "sword", cfg, v -> cfg.swordScalePct = v.intValue(), cfg.swordScalePct); y += spacing;
            addItemScaleSlider(x1, y, "Axe", "axe", cfg, v -> cfg.axeScalePct = v.intValue(), cfg.axeScalePct); y += spacing;
            addItemScaleSlider(x1, y, "Mace", "mace", cfg, v -> cfg.maceScalePct = v.intValue(), cfg.maceScalePct); y += spacing;
            addItemScaleSlider(x1, y, "Trident", "trident", cfg, v -> cfg.tridentScalePct = v.intValue(), cfg.tridentScalePct); y += spacing;
            addItemScaleSlider(x1, y, "Shield", "shield", cfg, v -> cfg.shieldScalePct = v.intValue(), cfg.shieldScalePct); y += spacing;
            addItemScaleSlider(x1, y, "Armor", "armor", cfg, v -> cfg.armorScalePct = v.intValue(), cfg.armorScalePct); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("\ud83d\udd0d Search Items..."), () -> {
                PvpTweaksConfig.save();
                client.setScreen(new com.pvptweaks.gui.ItemSearchScreen(this, false));
            }));
            
            // Right column
            y = firstItemRow;
            addItemScaleSlider(x2, y, "Bow", "bow", cfg, v -> cfg.bowScalePct = v.intValue(), cfg.bowScalePct); y += spacing;
            addItemScaleSlider(x2, y, "Crossbow", "crossbow", cfg, v -> cfg.crossbowScalePct = v.intValue(), cfg.crossbowScalePct); y += spacing;
            addItemScaleSlider(x2, y, "Totem", "totem", cfg, v -> cfg.totemScalePct = v.intValue(), cfg.totemScalePct); y += spacing;
            addItemScaleSlider(x2, y, "G-Apple", "goldenApple", cfg, v -> cfg.goldenAppleScalePct = v.intValue(), cfg.goldenAppleScalePct); y += spacing;
            addItemScaleSlider(x2, y, "Anchor", "anchor", cfg, v -> cfg.anchorScalePct = v.intValue(), cfg.anchorScalePct); y += spacing;
            addItemScaleSlider(x2, y, "Misc Items", "otherItems", cfg, v -> cfg.otherItemScalePct = v.intValue(), cfg.otherItemScalePct); y += spacing;
            
            List<String> customs = new ArrayList<>(cfg.customItemScales.keySet());
            for (String id : customs) {
                String label = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1) : id;
                addSlider(x2, y, label, cfg.customItemScales.get(id), 25, 300, 100, v -> cfg.customItemScales.put(id, v.intValue()));
                addDrawableChild(new ModernButtonWidget(x2 + 185, y - 10, 20, 20, Text.literal("\u2716"), () -> { cfg.customItemScales.remove(id); refreshCategoryWidgets(); }));
                y += spacing;
            }
        } else if (activeCategory.name.equals("Visuals")) {
            int x1 = x;
            int x2 = x + colWidth;
            int startY = y;

            y += 5;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("Entity Fire Height: " + cfg.firePreset.toUpperCase()), () -> {
                int idx = (java.util.Arrays.asList("vanilla", "full", "mid", "low", "flat", "none").indexOf(cfg.firePreset) + 1) % 6;
                cfg.firePreset = new String[]{"vanilla", "full", "mid", "low", "flat", "none"}[idx];
                fireChanged = true;
                refreshCategoryWidgets();
            })); y += spacing;

            if (fireChanged) {
                addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("\u00a7aSave Fire"), () -> {
                    PvpTweaksConfig.save();
                    if (client.worldRenderer != null) client.worldRenderer.reload();
                    client.reloadResources();
                    fireChanged = false;
                    refreshCategoryWidgets();
                })); y += spacing;
            }
            addSlider(x1, y, "Fire Overlay", cfg.fireOverlayScalePct, 0, 200, 100, v -> { cfg.fireOverlayScalePct = v.intValue(); fireChanged = true; }); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("Fullbright: " + (cfg.fullbright ? "ON" : "OFF")), () -> { cfg.fullbright = !cfg.fullbright; refreshCategoryWidgets(); })); y += spacing;
            addSlider(x1, y, "Gamma", (int)(cfg.fullbrightGamma * 100), 100, 1500, 100, v -> cfg.fullbrightGamma = v.floatValue() / 100f); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("Pumpkin Blur: " + (cfg.disablePumpkinBlur ? "HIDDEN" : "SHOWN")), () -> { cfg.disablePumpkinBlur = !cfg.disablePumpkinBlur; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("Crit Particles: " + (cfg.showHitParticles ? "ON" : "OFF")), () -> { cfg.showHitParticles = !cfg.showHitParticles; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("\u2756 Shield Adjuster..."), () -> client.setScreen(new ShieldConfigScreen(this)))); y += spacing;
            addDrawableChild(new ModernButtonWidget(x1, y, 180, 20, Text.literal("\ud83d\udd0e Zoom Settings..."), () -> client.setScreen(new ZoomScreen(this))));

            y = startY + 5;
            addDrawableChild(new ModernButtonWidget(x2, y, 180, 20, Text.literal("\ud83c\udf3f Plants Control..."), () -> client.setScreen(new com.pvptweaks.gui.PlantsControlScreen(this)))); y += spacing;
            addSlider(x2, y, "Totem Anim Size", cfg.totemPopAnimScalePct, 0, 200, 100, v -> cfg.totemPopAnimScalePct = v.intValue()); y += spacing;
            addSlider(x2, y, "Totem Particles", cfg.totemPopScalePct, 0, 200, 100, v -> cfg.totemPopScalePct = v.intValue()); y += spacing;
            addSlider(x2, y, "Crystal Entity Size", cfg.endCrystalScalePct, 25, 300, 100, v -> cfg.endCrystalScalePct = v.intValue()); y += spacing;
            addSlider(x2, y, "Crystal Particles", cfg.crystalParticlePct, 0, 200, 100, v -> cfg.crystalParticlePct = v.intValue()); y += spacing;
            addSlider(x2, y, "Crystal Expl Part.", cfg.enderExplosionParticlePct, 0, 200, 100, v -> cfg.enderExplosionParticlePct = v.intValue()); y += spacing;
            addSlider(x2, y, "Anchor Expl Part.", cfg.anchorExplosionParticlePct, 0, 200, 100, v -> cfg.anchorExplosionParticlePct = v.intValue());
        } else if (activeCategory.name.equals("HUD")) {
            y += 10;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("CPS HUD: " + (cfg.cpsEnabled ? "ON" : "OFF")), () -> { cfg.cpsEnabled = !cfg.cpsEnabled; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\u26ef Move CPS"), () -> client.setScreen(new CpsAdjusterScreen(this)))); y += spacing * 1.5;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("Durability HUD: " + (cfg.durabilityHudEnabled ? "ON" : "OFF")), () -> { cfg.durabilityHudEnabled = !cfg.durabilityHudEnabled; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\u26E8 Move Durability"), () -> client.setScreen(new DurabilityAdjusterScreen(this)))); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("Alert Sound Once: " + (cfg.durabilityAlertSoundOnce ? "ON" : "OFF")), () -> { cfg.durabilityAlertSoundOnce = !cfg.durabilityAlertSoundOnce; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("\ud83d\udd0d Item Background..."), () -> client.setScreen(new ItemBackgroundScreen(this)))); y += spacing;
            String[] lbModes = {"keybinds", "numbers", "off"};
            String[] lbLabels = {"\u00a7eKeybinds", "\u00a7eNumbers", "\u00a77Off"};
            int lbIdx = java.util.Arrays.asList(lbModes).indexOf(cfg.hotbarSlotLabelMode);
            if (lbIdx < 0) lbIdx = 2;
            final int fLbIdx = lbIdx;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20,
                Text.literal("Slot Labels: " + lbLabels[fLbIdx]), () -> {
                int next = (fLbIdx + 1) % 3;
                cfg.hotbarSlotLabelMode = lbModes[next];
                refreshCategoryWidgets();
            }));
        } else if (activeCategory.name.equals("Sounds")) {
            y += 10;
            addSlider(x, y, "Hit Vol", cfg.hitVolumePct, 0, 200, 100, v -> cfg.hitVolumePct = v.intValue()); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 200, 20, Text.literal("\ud83d\udca5 Explosions..."), () -> client.setScreen(new SoundSubCategoryScreen(this, "Explosions"))));
            y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 200, 20, Text.literal("\u2694 Combat..."), () -> client.setScreen(new SoundSubCategoryScreen(this, "Combat"))));
            y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 200, 20, Text.literal("\ud83c\udfb6 Misc..."), () -> client.setScreen(new SoundSubCategoryScreen(this, "Misc"))));
        } else if (activeCategory.name.equals("Optimization")) {
            y += 10;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("Crystal Opt: " + (cfg.crystalOptimizer ? "ON" : "OFF")), () -> { cfg.crystalOptimizer = !cfg.crystalOptimizer; refreshCategoryWidgets(); })); y += spacing;
            addDrawableChild(new ModernButtonWidget(x, y, 180, 20, Text.literal("Anchor Opt: " + (cfg.anchorOptimizer ? "ON" : "OFF")), () -> { cfg.anchorOptimizer = !cfg.anchorOptimizer; refreshCategoryWidgets(); }));
        } else if (activeCategory.name.equals("Profiles")) {
            y += 10;
            addDrawableChild(new ModernButtonWidget(x, y, 160, 20, Text.literal("\u00a7a+ Create Profile"), () -> client.setScreen(new ProfileNameScreen(this, null, name -> { PvpTweaksProfiles.save(name); client.execute(() -> client.setScreen(new PvpTweaksHubScreen(parent, "Profiles"))); }))));
            y += spacing * 1.5;
            List<String> profiles = PvpTweaksProfiles.list();
            for (String p : profiles) {
                if (y > height - 70) break;
                final String pn = p;
                addDrawableChild(new ModernButtonWidget(x, y, 120, 20, Text.literal(pn), () -> { if (PvpTweaksProfiles.load(pn)) refreshCategoryWidgets(); }));
                addDrawableChild(new ModernButtonWidget(x + 125, y, 35, 20, Text.literal("\u00a7c\u2716"), () -> { PvpTweaksProfiles.delete(pn); refreshCategoryWidgets(); }));
                y += spacing;
            }
            
            int x2 = x + colWidth;
            int y2 = 65 + 10 - (int)contentScroll;
            addDrawableChild(new ModernButtonWidget(x2, y2, 160, 20, Text.literal("\ud83d\udce4 Export Mod Settings"), () -> promptExport(true, false))); y2 += spacing;
            addDrawableChild(new ModernButtonWidget(x2, y2, 160, 20, Text.literal("\ud83d\udce5 Import Mod Settings"), () -> handleImport(true, false))); y2 += spacing * 1.5;
            addDrawableChild(new ModernButtonWidget(x2, y2, 160, 20, Text.literal("\ud83d\udce4 Export Keybinds"), () -> promptExport(false, true))); y2 += spacing;
            addDrawableChild(new ModernButtonWidget(x2, y2, 160, 20, Text.literal("\ud83d\udce5 Import Keybinds"), () -> handleImport(false, true))); y2 += spacing * 1.5;
            addDrawableChild(new ModernButtonWidget(x2, y2, 160, 20, Text.literal("\ud83d\udcc1 Profiles Folder"), () -> {
                try { net.minecraft.util.Util.getOperatingSystem().open(PvpTweaksProfiles.PROFILES_DIR.toFile()); } catch (Exception ignored) {}
            }));
        }
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter) {
        if (y < 40 || y > height - 10) return;
        addDrawableChild(new CustomSliderWidget(x, y, 155, 20, label, val, min, max, true, setter)); 
        addDrawableChild(new ModernButtonWidget(x + 160, y, 20, 20, Text.literal("\u21ba"), () -> {
            setter.accept(defVal);
            refreshCategoryWidgets();
        }));
    }

    private void addItemScaleSlider(int x, int y, String label, String key, PvpTweaksConfig cfg, java.util.function.Consumer<Double> setter, int currentVal) {
        if (y < 40 || y > height - 10) return;
        boolean linked = "custom".equals(cfg.itemScaleGlobalMode) && cfg.itemScaleGlobalLinked.contains(key);
        boolean forced = ("listed".equals(cfg.itemScaleGlobalMode) || linked
            || ("unlisted".equals(cfg.itemScaleGlobalMode) && "otherItems".equals(key)));
        int displayVal = forced ? cfg.itemScaleGlobalPct : currentVal;
        java.util.function.Consumer<Double> s = forced ? v -> {} : setter;
        CustomSliderWidget slider = new CustomSliderWidget(x, y, 155, 20, label, displayVal, 25, 300, true, s);
        slider.forced = forced;
        addDrawableChild(slider);
        if ("custom".equals(cfg.itemScaleGlobalMode)) {
            addDrawableChild(new ModernButtonWidget(x + 155, y, 20, 20,
                Text.literal(linked ? "\u26a1" : "\u26aa"), () -> {
                if (linked) cfg.itemScaleGlobalLinked.remove(key);
                else cfg.itemScaleGlobalLinked.add(key);
                refreshCategoryWidgets();
            }));
            addDrawableChild(new ModernButtonWidget(x + 180, y, 20, 20, Text.literal("\u21ba"), () -> {
                setter.accept((double)100);
                refreshCategoryWidgets();
            }));
        } else {
            addDrawableChild(new ModernButtonWidget(x + 160, y, 20, 20, Text.literal("\u21ba"), () -> {
                setter.accept((double)100);
                refreshCategoryWidgets();
            }));
        }
    }

    private void promptExport(boolean cfg, boolean keys) { client.setScreen(new ProfileNameScreen(this, null, name -> { try { String json = PvpTweaksProfiles.exportJson(name, cfg, keys); client.keyboard.setClipboard(json); if (client.player != null) client.player.sendMessage(Text.literal("\u00a7a[PVP Tweaks] Profile copied!"), false); } catch (Exception ignored) {} client.execute(() -> client.setScreen(new PvpTweaksHubScreen(parent, "Profiles"))); })); }
    private void handleImport(boolean cfg, boolean keys) { try { String json = client.keyboard.getClipboard(); boolean ok = PvpTweaksProfiles.importPackage(json, cfg, keys); if (client.player != null) client.player.sendMessage(Text.literal(ok ? "\u00a7a[PVP Tweaks] Imported successfully!" : "\u00a7c[PVP Tweaks] Import failed!"), false); if (ok) refreshCategoryWidgets(); } catch (Exception ignored) {} }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForKeybind) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                com.pvptweaks.PvpTweaksClient.openMenuKeyBinding.setBoundKey(net.minecraft.client.util.InputUtil.UNKNOWN_KEY);
            } else {
                com.pvptweaks.PvpTweaksClient.openMenuKeyBinding.setBoundKey(net.minecraft.client.util.InputUtil.fromKeyCode(keyCode, scanCode));
            }
            net.minecraft.client.option.KeyBinding.updateKeysByCode();
            client.options.write();
            listeningForKeybind = false;
            refreshCategoryWidgets();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        if (mx < sidebarWidth) {
            int totalH = categories.size() * 44;
            sidebarScroll = MathHelper.clamp(sidebarScroll - vy * 15, 0, Math.max(0, totalH - (height - 40)));
            return true;
        } else {
            contentScroll = MathHelper.clamp(contentScroll - vy * 15, 0, 1000);
            refreshCategoryWidgets();
            return true;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, this.width, this.height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawGradientRect(context, 0, 0, sidebarWidth, this.height, 0x50000000, 0x20000000);
        RenderUtils.drawOutline(context, sidebarWidth - 1, 0, 1, this.height, 1, UiPalette.BORDER);
        
        int startY = 40;
        context.enableScissor(0, 40, sidebarWidth, height);
        int y = startY - (int)sidebarScroll;
        for (Category cat : categories) {
            boolean hovered = mouseX < sidebarWidth && mouseY >= y && mouseY < y + 40;
            boolean selected = activeCategory == cat;
            int color = selected ? UiPalette.ACCENT_BLUE : (hovered ? UiPalette.TEXT_PRIMARY : UiPalette.TEXT_SECONDARY);
            if (selected) { RenderUtils.drawRoundedRect(context, 8, y, sidebarWidth - 16, 36, 6, 0x4000A3FF); RenderUtils.drawOutline(context, 8, y, sidebarWidth - 16, 36, 1, UiPalette.ACCENT_BLUE); }
            else if (hovered) RenderUtils.drawRoundedRect(context, 8, y, sidebarWidth - 16, 36, 6, 0x20FFFFFF);
            context.drawCenteredTextWithShadow(textRenderer, cat.icon, sidebarWidth / 2, y + 6, color);
            context.drawCenteredTextWithShadow(textRenderer, cat.name, sidebarWidth / 2, y + 20, color);
            y += 44;
        }
        context.disableScissor();

        context.drawTextWithShadow(textRenderer, Text.literal("\u00a7lPVP TWEAKS"), sidebarWidth + 20, 18, UiPalette.ACCENT_BLUE);
        context.drawTextWithShadow(textRenderer, Text.literal(" / " + activeCategory.name.toUpperCase()), sidebarWidth + 20 + textRenderer.getWidth("PVP TWEAKS "), 18, UiPalette.TEXT_SECONDARY);
        RenderUtils.drawOutline(context, sidebarWidth + 20, 32, width - sidebarWidth - 45, 1, 1, 0x30FFFFFF);
        
        if (activeCategory.name.equals("Optimization")) {
            int textX = sidebarWidth + 20;
            int textY = 65 + 10 + 44 + 44 - (int)contentScroll;
            if (textY > 40 && textY < height - 15) {
                context.drawTextWithShadow(textRenderer, Text.literal("\u00a7e\u26a0 Experimental Features"), textX, textY, 0xFFFFB300);
                context.drawTextWithShadow(textRenderer, Text.literal("\u00a77Optimizers are disabled by default as they modify"), textX, textY + 15, 0xFFAAAAAA);
                context.drawTextWithShadow(textRenderer, Text.literal("\u00a77entity interactions and may be disallowed by some"), textX, textY + 27, 0xFFAAAAAA);
                context.drawTextWithShadow(textRenderer, Text.literal("\u00a77multiplayer servers. Toggle with caution."), textX, textY + 39, 0xFFAAAAAA);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < sidebarWidth) {
            int y = 40 - (int)sidebarScroll;
            for (Category cat : categories) {
                if (mouseY >= y && mouseY < y + 40) { 
                    activeCategory = cat; 
                    lastCategoryName = cat.name;
                    contentScroll = 0;
                    refreshCategoryWidgets(); 
                    return true; 
                }
                y += 44;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public void close() { this.client.setScreen(parent); }
    private static class Category { String name, icon; Category(String name, String icon) { this.name = name; this.icon = icon; } }
}
