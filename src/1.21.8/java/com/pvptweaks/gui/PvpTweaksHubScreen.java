package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.PvpTweaksProfiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
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
    private int contentHeight = 300;
    private final java.util.IdentityHashMap<ClickableWidget, String> tooltips = new java.util.IdentityHashMap<>();

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
        categories.add(new Category("Info", "\u2139"));
        
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
        tooltips.clear();
        
        addTooltipped(this.width - 65, 12, 55, 20, "Done",
            "Save configuration and close", () -> {
            PvpTweaksConfig.save();
            if (fireChanged) {
                if (client.worldRenderer != null) client.worldRenderer.reload();
                client.reloadResources();
                fireChanged = false;
            }
            this.client.setScreen(parent);
        });

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int x = sidebarWidth + 25;
        int y = 65 - (int)contentScroll;
        int spacing = 44;
        int colWidth = 200;

        if (activeCategory.name.equals("Home")) {
            y += 10;
            addTooltipped(x, y, 180, 20, "\u2694 Apply Competitive Preset",
                "Apply competitive PVP settings preset", () -> { PvpTweaksProfiles.applyCompetitive(); refreshCategoryWidgets(); });
            y += spacing;
            
            String keyName = com.pvptweaks.PvpTweaksClient.openMenuKeyBinding.getBoundKeyLocalizedText().getString();
            Text btnText = listeningForKeybind ? Text.literal("> \u00a7e???\u00a7r <") : Text.literal("Menu Key: " + keyName);
            String keyTip = listeningForKeybind ? "Press a key to bind, Escape to unbind" : "Click to rebind the menu key";
            var keyBtn = addDrawableChild(new ModernButtonWidget(x, y, 180, 20, btnText, () -> {
                listeningForKeybind = true;
                refreshCategoryWidgets();
            }));
            tooltips.put(keyBtn, keyTip);
            y += spacing;
            
            addTooltipped(x, y, 180, 20, "\u21ba Reset to Vanilla",
                "Restore all vanilla Minecraft defaults", () -> { PvpTweaksProfiles.applyVanilla(); refreshCategoryWidgets(); });
            y += spacing;
            this.contentHeight = y + (int)contentScroll;
            addTooltipped(x, y, 180, 20, "\u26ef Reset to Profile Defaults",
                "Reset to the saved profile defaults", () -> { PvpTweaksProfiles.applyDefault(); refreshCategoryWidgets(); });
        } else if (activeCategory.name.equals("Item Sizes")) {
            int x1 = x;
            int x2 = x + colWidth;

            y += 5;

            String[] modes = {"listed", "unlisted", "custom", "off"};
            String[] modeLabels = {"\u00a7eListed", "\u00a7eUnlisted", "\u00a7eCustom", "\u00a77Off"};
            int modeIdx = java.util.Arrays.asList(modes).indexOf(cfg.itemScaleGlobalMode);
            if (modeIdx < 0) modeIdx = 3;
            final int fModeIdx = modeIdx;

            addTooltippedSlider(x1, y, 155, "Global", cfg.itemScaleGlobalPct, 25, 300, true,
                v -> { cfg.itemScaleGlobalPct = v.intValue(); refreshCategoryWidgets(); },
                "Master scale for all items (25-300%)");
            y += spacing;

            addTooltipped(x1, y, 180, 20, "Mode: " + modeLabels[fModeIdx],
                "Switch item scaling mode (Listed/Unlisted/Custom/Off)", () -> {
                int next = (fModeIdx + 1) % 4;
                cfg.itemScaleGlobalMode = modes[next];
                refreshCategoryWidgets();
            });
            y += spacing;

            int firstItemRow = y;

            addItemScaleSlider(x1, y, "Sword", "sword", cfg, v -> cfg.swordScalePct = v.intValue(), cfg.swordScalePct, "Scale for swords (25-300%)"); y += spacing;
            addItemScaleSlider(x1, y, "Axe", "axe", cfg, v -> cfg.axeScalePct = v.intValue(), cfg.axeScalePct, "Scale for axes (25-300%)"); y += spacing;
            addItemScaleSlider(x1, y, "Mace", "mace", cfg, v -> cfg.maceScalePct = v.intValue(), cfg.maceScalePct, "Scale for mace (25-300%)"); y += spacing;
            addItemScaleSlider(x1, y, "Trident", "trident", cfg, v -> cfg.tridentScalePct = v.intValue(), cfg.tridentScalePct, "Scale for tridents (25-300%)"); y += spacing;
            addItemScaleSlider(x1, y, "Shield", "shield", cfg, v -> cfg.shieldScalePct = v.intValue(), cfg.shieldScalePct, "Scale for shields (25-300%)"); y += spacing;
            addItemScaleSlider(x1, y, "Armor", "armor", cfg, v -> cfg.armorScalePct = v.intValue(), cfg.armorScalePct, "Scale for armor pieces (25-300%)"); y += spacing;
            int leftColEndItems = y;
            addTooltipped(x1, y, 180, 20, "\ud83d\udd0d Search Items...",
                "Search and customize scale for individual items", () -> {
                PvpTweaksConfig.save();
                client.setScreen(new com.pvptweaks.gui.ItemSearchScreen(this, false));
            });
            
            y = firstItemRow;
            addItemScaleSlider(x2, y, "Bow", "bow", cfg, v -> cfg.bowScalePct = v.intValue(), cfg.bowScalePct, "Scale for bows (25-300%)"); y += spacing;
            addItemScaleSlider(x2, y, "Crossbow", "crossbow", cfg, v -> cfg.crossbowScalePct = v.intValue(), cfg.crossbowScalePct, "Scale for crossbows (25-300%)"); y += spacing;
            addItemScaleSlider(x2, y, "Totem", "totem", cfg, v -> cfg.totemScalePct = v.intValue(), cfg.totemScalePct, "Scale for totems (25-300%)"); y += spacing;
            addItemScaleSlider(x2, y, "G-Apple", "goldenApple", cfg, v -> cfg.goldenAppleScalePct = v.intValue(), cfg.goldenAppleScalePct, "Scale for golden apples (25-300%)"); y += spacing;
            addItemScaleSlider(x2, y, "Anchor", "anchor", cfg, v -> cfg.anchorScalePct = v.intValue(), cfg.anchorScalePct, "Scale for respawn anchors (25-300%)"); y += spacing;
            addItemScaleSlider(x2, y, "Misc Items", "otherItems", cfg, v -> cfg.otherItemScalePct = v.intValue(), cfg.otherItemScalePct, "Scale for all other items (25-300%)"); y += spacing;
            
            List<String> customs = new ArrayList<>(cfg.customItemScales.keySet());
            for (String id : customs) {
                String label = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1) : id;
                addSlider(x2, y, label, cfg.customItemScales.get(id), 25, 300, 100, v -> cfg.customItemScales.put(id, v.intValue()), "Custom item scale (25-300%)");
                var delBtn = addDrawableChild(new ModernButtonWidget(x2 + 185, y - 10, 20, 20, Text.literal("\u2716"), () -> { cfg.customItemScales.remove(id); refreshCategoryWidgets(); }));
                tooltips.put(delBtn, "Remove this custom item entry");
                y += spacing;
            }
            this.contentHeight = Math.max(leftColEndItems, y) + (int)contentScroll;
        } else if (activeCategory.name.equals("Visuals")) {
            int x1 = x;
            int x2 = x + colWidth;
            int startY = y;

            y += 5;
            addTooltipped(x1, y, 180, 20, "Entity Fire Height: " + cfg.firePreset.toUpperCase(),
                "Cycle through fire height presets (vanilla/full/mid/low/flat/none)", () -> {
                int idx = (java.util.Arrays.asList("vanilla", "full", "mid", "low", "flat", "none").indexOf(cfg.firePreset) + 1) % 6;
                cfg.firePreset = new String[]{"vanilla", "full", "mid", "low", "flat", "none"}[idx];
                fireChanged = true;
                refreshCategoryWidgets();
            }); y += spacing;

            if (fireChanged) {
                addTooltipped(x1, y, 180, 20, "\u00a7aSave Fire",
                    "Apply fire changes immediately", () -> {
                    PvpTweaksConfig.save();
                    if (client.worldRenderer != null) client.worldRenderer.reload();
                    client.reloadResources();
                    fireChanged = false;
                    refreshCategoryWidgets();
                }); y += spacing;
            }
            addSlider(x1, y, "Fire Overlay", cfg.fireOverlayScalePct, 0, 200, 100, v -> { cfg.fireOverlayScalePct = v.intValue(); fireChanged = true; }, "Scale of the fire overlay effect (0-200%)"); y += spacing;
            addTooltipped(x1, y, 180, 20, "\ud83d\udca1 Fullbright...",
                "Open fullbright and gamma settings screen", () -> client.setScreen(new FullbrightScreen(this))); y += spacing;
            addTooltipped(x1, y, 180, 20, "Pumpkin Blur: " + (cfg.disablePumpkinBlur ? "HIDDEN" : "SHOWN"),
                "Toggle pumpkin overlay effect", () -> { cfg.disablePumpkinBlur = !cfg.disablePumpkinBlur; refreshCategoryWidgets(); }); y += spacing;
            addTooltipped(x1, y, 180, 20, "\u2728 Particles...",
                "Configure particle effects (crit, totem, crystal, explosions)", () -> client.setScreen(new ParticlesScreen(this))); y += spacing;
            addTooltipped(x1, y, 180, 20, "\u2756 Shield Adjuster...",
                "Configure shield rendering and position", () -> client.setScreen(new ShieldConfigScreen(this))); y += spacing;
            addTooltipped(x1, y, 180, 20, "\ud83d\udd0e Zoom Settings...",
                "Configure zoom controls and speed", () -> client.setScreen(new ZoomScreen(this)));
            int leftColEndVisuals = y;

            y = startY + 5;
            addTooltipped(x2, y, 180, 20, "\ud83c\udf3f Plants Control...",
                "Adjust plant and tall grass rendering", () -> client.setScreen(new com.pvptweaks.gui.PlantsControlScreen(this))); y += spacing;
            addSlider(x2, y, "Totem Anim Size", cfg.totemPopAnimScalePct, 0, 200, 100, v -> cfg.totemPopAnimScalePct = v.intValue(), "Size of totem pop animation (0-200%)"); y += spacing;
            addSlider(x2, y, "Crystal Entity Size", cfg.endCrystalScalePct, 25, 300, 100, v -> cfg.endCrystalScalePct = v.intValue(), "Scale of end crystal entities (25-300%)"); y += spacing;
            this.contentHeight = Math.max(leftColEndVisuals, y) + (int)contentScroll;
        } else if (activeCategory.name.equals("HUD")) {
            y += 10;
            addTooltipped(x, y, 180, 20, "\u26a1 CPS...",
                "Open CPS HUD settings", () -> client.setScreen(new CpsScreen(this))); y += spacing;
            addTooltipped(x, y, 180, 20, "\u2764 Durability HUD...",
                "Open durability HUD settings", () -> client.setScreen(new DurabilityScreen(this))); y += spacing;
            addTooltipped(x, y, 180, 20, "\ud83d\udd0d Item Background...",
                "Configure item background rendering in inventory", () -> client.setScreen(new ItemBackgroundScreen(this))); y += spacing;
            addTooltipped(x, y, 180, 20, "\u271c Crosshair...",
                "Open crosshair customization screen", () -> client.setScreen(new CrosshairAdjusterScreen(this))); y += spacing;
            String[] lbModes = {"keybinds", "numbers", "off"};
            String[] lbLabels = {"\u00a7eKeybinds", "\u00a7eNumbers", "\u00a77Off"};
            int lbIdx = java.util.Arrays.asList(lbModes).indexOf(cfg.hotbarSlotLabelMode);
            if (lbIdx < 0) lbIdx = 2;
            final int fLbIdx = lbIdx;
            addTooltipped(x, y, 180, 20, "Slot Labels: " + lbLabels[fLbIdx],
                "Toggle hotbar slot label display mode", () -> {
                int next = (fLbIdx + 1) % 3;
                cfg.hotbarSlotLabelMode = lbModes[next];
                refreshCategoryWidgets();
            });
            this.contentHeight = y + (int)contentScroll;
        } else if (activeCategory.name.equals("Sounds")) {
            y += 10;
            addSlider(x, y, "Hit Vol", cfg.hitVolumePct, 0, 200, 100, v -> cfg.hitVolumePct = v.intValue(), "Volume of hit sounds (0-200%)"); y += spacing;
            addTooltipped(x, y, 200, 20, "\ud83d\udca5 Explosions...",
                "Configure explosion sound settings", () -> client.setScreen(new SoundSubCategoryScreen(this, "Explosions")));
            y += spacing;
            addTooltipped(x, y, 200, 20, "\u2694 Combat...",
                "Configure combat sound settings", () -> client.setScreen(new SoundSubCategoryScreen(this, "Combat")));
            y += spacing;
            addTooltipped(x, y, 200, 20, "\ud83c\udfb6 Misc...",
                "Configure miscellaneous sound settings", () -> client.setScreen(new SoundSubCategoryScreen(this, "Misc")));
            y += spacing;
            addTooltipped(x, y, 200, 20, "\ud83c\udfb5 Custom...",
                "Add and configure custom sounds for any game event", () -> client.setScreen(new SoundSubCategoryScreen(this, "Custom")));
            y += spacing;
            addTooltipped(x, y, 200, 20, "\ud83d\udd14 Durability Alert...",
                "Configure the durability low alert sound", () -> client.setScreen(new ModernSoundPickerScreen(this, cfg.soundDurabilityLow, "Durability Low", PvpTweaksConfig::save)));
            this.contentHeight = y + (int)contentScroll;
        } else if (activeCategory.name.equals("Optimization")) {
            y += 10;
            addTooltipped(x, y, 180, 20, "Crystal Opt: " + (cfg.crystalOptimizer ? "ON" : "OFF"),
                "Toggle end crystal performance optimization", () -> { cfg.crystalOptimizer = !cfg.crystalOptimizer; refreshCategoryWidgets(); }); y += spacing;
            this.contentHeight = y + (int)contentScroll;
            addTooltipped(x, y, 180, 20, "Anchor Opt: " + (cfg.anchorOptimizer ? "ON" : "OFF"),
                "Toggle respawn anchor performance optimization", () -> { cfg.anchorOptimizer = !cfg.anchorOptimizer; refreshCategoryWidgets(); });
        } else if (activeCategory.name.equals("Profiles")) {
            y += 10;
            addTooltipped(x, y, 160, 20, "\u00a7a+ Create Profile",
                "Save current settings as a new profile", () -> client.setScreen(new ProfileNameScreen(this, null, name -> { PvpTweaksProfiles.save(name); client.execute(() -> client.setScreen(new PvpTweaksHubScreen(parent, "Profiles"))); })));
            y += spacing;
            List<String> profiles = PvpTweaksProfiles.list();
            for (String p : profiles) {
                if (y > height - 70) break;
                final String pn = p;
                var loadBtn = addDrawableChild(new ModernButtonWidget(x, y, 120, 20, Text.literal(pn), () -> { if (PvpTweaksProfiles.load(pn)) refreshCategoryWidgets(); }));
                tooltips.put(loadBtn, "Load this profile");
                var delBtn = addDrawableChild(new ModernButtonWidget(x + 125, y, 35, 20, Text.literal("\u00a7c\u2716"), () -> { PvpTweaksProfiles.delete(pn); refreshCategoryWidgets(); }));
                tooltips.put(delBtn, "Delete this profile");
                y += spacing;
            }
            
            this.contentHeight = y + (int)contentScroll;
            int x2 = x + colWidth;
            int y2 = 65 + 10 - (int)contentScroll;
            addTooltipped(x2, y2, 160, 20, "\ud83d\udce4 Export Mod Settings",
                "Export mod settings as a shareable string", () -> promptExport(true, false)); y2 += spacing;
            addTooltipped(x2, y2, 160, 20, "\ud83d\udce5 Import Mod Settings",
                "Import mod settings from clipboard", () -> handleImport(true, false)); y2 += (int)(spacing * 1.5);
            addTooltipped(x2, y2, 160, 20, "\ud83d\udce4 Export Keybinds",
                "Export keybindings as a shareable string", () -> promptExport(false, true)); y2 += spacing;
            addTooltipped(x2, y2, 160, 20, "\ud83d\udce5 Import Keybinds",
                "Import keybindings from clipboard", () -> handleImport(false, true)); y2 += (int)(spacing * 1.5);
            addTooltipped(x2, y2, 160, 20, "\ud83d\udcc1 Profiles Folder",
                "Open the profiles folder in file manager", () -> {
                try { net.minecraft.util.Util.getOperatingSystem().open(PvpTweaksProfiles.PROFILES_DIR.toFile()); } catch (Exception ignored) {}
            });
        } else if (activeCategory.name.equals("Info")) {
            int lx = sidebarWidth + 25;
            int ly = 65 - (int)contentScroll;
            ly += 136;

            addTooltipped(lx, ly, 300, 20, "\u00a79GitHub: \u00a77github.com/viper-trick/pvp-tweaks",
                "Open the GitHub repository", () -> {
                try { net.minecraft.util.Util.getOperatingSystem().open(new java.net.URI("https://github.com/viper-trick/pvp-tweaks")); } catch (Exception ignored) {}
            }); ly += 26;
            addTooltipped(lx, ly, 300, 20, "\u00a79Issues: \u00a77github.com/viper-trick/pvp-tweaks/issues",
                "Report bugs or request features", () -> {
                try { net.minecraft.util.Util.getOperatingSystem().open(new java.net.URI("https://github.com/viper-trick/pvp-tweaks/issues")); } catch (Exception ignored) {}
            }); ly += 26;
            addTooltipped(lx, ly, 300, 20, "\u00a79Modrinth: \u00a77modrinth.com/mod/pvptweak",
                "View the mod on Modrinth", () -> {
                try { net.minecraft.util.Util.getOperatingSystem().open(new java.net.URI("https://modrinth.com/mod/pvptweak")); } catch (Exception ignored) {}
            });

            this.contentHeight = 550;
        }
    }

    private ModernButtonWidget addTooltipped(int x, int y, int w, int h, String label, String tip, Runnable action) {
        var btn = addDrawableChild(new ModernButtonWidget(x, y, w, h, Text.literal(label), action));
        tooltips.put(btn, tip);
        return btn;
    }

    private void addSlider(int x, int y, String label, double val, double min, double max, double defVal, java.util.function.Consumer<Double> setter, String tip) {
        if (y < 40 || y > height - 10) return;
        var slider = addDrawableChild(new CustomSliderWidget(x, y, 155, 20, label, val, min, max, true, setter));
        tooltips.put(slider, tip);
        var resetBtn = addDrawableChild(new ModernButtonWidget(x + 160, y, 20, 20, Text.literal("\u21ba"), () -> {
            setter.accept(defVal);
            refreshCategoryWidgets();
        }));
        tooltips.put(resetBtn, "Reset " + label + " to default (" + (int)defVal + "%)");
    }

    private void addTooltippedSlider(int x, int y, int w, String label, double val, double min, double max, boolean isInt, java.util.function.Consumer<Double> setter, String tip) {
        if (y < 40 || y > height - 10) return;
        var slider = addDrawableChild(new CustomSliderWidget(x, y, w, 20, label, val, min, max, isInt, setter));
        tooltips.put(slider, tip);
    }

    private void addItemScaleSlider(int x, int y, String label, String key, PvpTweaksConfig cfg, java.util.function.Consumer<Double> setter, int currentVal, String tip) {
        if (y < 40 || y > height - 10) return;
        boolean linked = "custom".equals(cfg.itemScaleGlobalMode) && cfg.itemScaleGlobalLinked.contains(key);
        boolean forced = ("listed".equals(cfg.itemScaleGlobalMode) || linked
            || ("unlisted".equals(cfg.itemScaleGlobalMode) && "otherItems".equals(key)));
        int displayVal = forced ? cfg.itemScaleGlobalPct : currentVal;
        java.util.function.Consumer<Double> s = forced ? v -> {} : setter;
        CustomSliderWidget slider = new CustomSliderWidget(x, y, 155, 20, label, displayVal, 25, 300, true, s);
        slider.forced = forced;
        addDrawableChild(slider);
        tooltips.put(slider, forced ? "Currently controlled by Global slider" : tip);
        if ("custom".equals(cfg.itemScaleGlobalMode)) {
            var linkBtn = addDrawableChild(new ModernButtonWidget(x + 155, y, 20, 20,
                Text.literal(linked ? "\u26a1" : "\u26aa"), () -> {
                if (linked) cfg.itemScaleGlobalLinked.remove(key);
                else cfg.itemScaleGlobalLinked.add(key);
                refreshCategoryWidgets();
            }));
            tooltips.put(linkBtn, linked ? "Unlink from Global" : "Link to Global");
            var resetBtn = addDrawableChild(new ModernButtonWidget(x + 180, y, 20, 20, Text.literal("\u21ba"), () -> {
                setter.accept((double)100);
                refreshCategoryWidgets();
            }));
            tooltips.put(resetBtn, "Reset " + label + " to default (100%)");
        } else {
            var resetBtn = addDrawableChild(new ModernButtonWidget(x + 160, y, 20, 20, Text.literal("\u21ba"), () -> {
                setter.accept((double)100);
                refreshCategoryWidgets();
            }));
            tooltips.put(resetBtn, "Reset " + label + " to default (100%)");
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
            contentScroll = MathHelper.clamp(contentScroll - vy * 15, 0, Math.max(0, contentHeight - height + 35));
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
            if (selected) { RenderUtils.drawRoundedRect(context, 8, y, sidebarWidth - 16, 36, 6, 0x4000A3FF); RenderUtils.drawRoundedOutline(context, 8, y, sidebarWidth - 16, 36, 6, 1, UiPalette.ACCENT_BLUE); }
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
        } else if (activeCategory.name.equals("Info")) {
            int textX = sidebarWidth + 25;
            int iy = 65 - (int)contentScroll;
            int lh = 12;

            context.drawTextWithShadow(textRenderer, Text.literal("\u00a7l\u2139 ABOUT PVP TWEAKS"), textX, iy, UiPalette.ACCENT_BLUE);
            iy += 22;

            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77Version: \u00a7f1.9.1"), textX, iy, 0xFFAAAAAA);
            iy += lh + 2;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77Author: \u00a7fviper-trick"), textX, iy, 0xFFAAAAAA);
            iy += lh + 2;

            context.drawTextWithShadow(textRenderer, Text.literal("\u00a7lDescription"), textX, iy, UiPalette.ACCENT_BLUE);
            iy += 16;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77A comprehensive PVP optimization and visual customization mod"), textX, iy, 0xFFAAAAAA);
            iy += lh;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77for Minecraft 1.21+. Features item scaling, crosshair"), textX, iy, 0xFFAAAAAA);
            iy += lh;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77editor, shield adjuster, CPS counter, durability HUD,"), textX, iy, 0xFFAAAAAA);
            iy += lh;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77fire presets, fullbright, particle controls, optimizers,"), textX, iy, 0xFFAAAAAA);
            iy += lh;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77explosion sounds, zoom, and more."), textX, iy, 0xFFAAAAAA);
            iy += lh + 10;

            iy += 78;

            context.drawTextWithShadow(textRenderer, Text.literal("\u00a7lContact"), textX, iy, UiPalette.ACCENT_BLUE);
            iy += 16;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77Email: \u00a7fyag.fvt@gmail.com"), textX, iy, 0xFFAAAAAA);
            iy += lh + 2;
            context.drawTextWithShadow(textRenderer, Text.literal("\u00a77Discord: \u00a7fvipertrick"), textX, iy, 0xFFAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);

        ClickableWidget hovered = null;
        for (ClickableWidget cw : tooltips.keySet()) {
            if (cw.isHovered()) {
                hovered = cw;
                break;
            }
        }
        if (hovered != null) {
            renderTooltip(context, tooltips.get(hovered), mouseX, mouseY);
        }
    }

    private void renderTooltip(DrawContext ctx, String text, int mx, int my) {
        int tw = textRenderer.getWidth(text);
        int tx = Math.max(3, Math.min(mx - tw / 2, width - tw - 3));
        int ty = Math.max(3, my - 22);
        ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + textRenderer.fontHeight + 2, 0xC0202020);
        ctx.drawTextWithShadow(textRenderer, Text.literal(text), tx, ty, 0xFFFFFFFF);
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
