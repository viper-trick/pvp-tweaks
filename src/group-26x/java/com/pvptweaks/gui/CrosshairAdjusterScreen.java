package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class CrosshairAdjusterScreen extends Screen {

    private final Screen parent;
    private static final String[] STYLE_NAMES = { "Cross", "Dot", "T-Shape", "X-Cross" };
    private EditBox codeField;
    private String importStatus = "";
    private final java.util.IdentityHashMap<AbstractWidget, String> tooltips = new java.util.IdentityHashMap<>();

    public CrosshairAdjusterScreen(Screen parent) {
        super(Component.literal("Crosshair Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        String existingText = (codeField != null) ? codeField.getValue() : "";
        boolean wasFocused = (codeField != null) && codeField.isFocused();

        this.clearWidgets();
        tooltips.clear();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        int cx = this.width / 2;
        int btnH = 20;
        int sldH = 20;

        // ── TOP TOGGLES ROW (Y = 32) ──
        addTooltipped(cx - 190, 32, 90, btnH,
            "Custom: " + (cfg.customCrosshairEnabled ? "§aON" : "§7OFF"),
            "Enable or disable the custom crosshair",
            () -> { cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled; init(); });

        addTooltipped(cx - 95, 32, 90, btnH,
            "Style: §e" + STYLE_NAMES[Math.max(0, Math.min(cfg.crosshairStyle, STYLE_NAMES.length-1))],
            "Switch crosshair shape (Cross / Dot / T / X)",
            () -> { cfg.crosshairStyle = (cfg.crosshairStyle + 1) % STYLE_NAMES.length; init(); });

        addTooltipped(cx + 5, 32, 90, btnH,
            "Dot: " + (cfg.crosshairDot ? "§aON" : "§7OFF"),
            "Toggle center dot on or off",
            () -> { cfg.crosshairDot = !cfg.crosshairDot; init(); });

        addTooltipped(cx + 100, 32, 90, btnH,
            "Outline: " + (cfg.crosshairOutline ? "§aON" : "§7OFF"),
            "Toggle black outline around arms",
            () -> { cfg.crosshairOutline = !cfg.crosshairOutline; init(); });

        // ── SLIDERS ──
        addSlider(cx - 190, 62, 160, "Size", cfg.crosshairSize, 0.0, 10.0, false,
            v -> cfg.crosshairSize = v.floatValue(), "Arm length in pixels (at 1080p)");
        addTooltipped(cx - 28, 62, 22, sldH, "↺",
            "Reset Size to default (3.0)",
            () -> { cfg.crosshairSize = 3.0f; init(); });

        addSlider(cx - 190, 86, 160, "Gap", cfg.crosshairGap, -5.0, 5.0, false,
            v -> cfg.crosshairGap = v.floatValue(), "Arm offset from center (negative = overlap)");
        addTooltipped(cx - 28, 86, 22, sldH, "↺",
            "Reset Gap to default (1.0)",
            () -> { cfg.crosshairGap = 1.0f; init(); });

        addSlider(cx - 190, 110, 160, "Thickness", cfg.crosshairThickness, 0.0, 6.0, false,
            v -> cfg.crosshairThickness = v.floatValue(), "Arm width in pixels");
        addTooltipped(cx - 28, 110, 22, sldH, "↺",
            "Reset Thickness to default (1.0)",
            () -> { cfg.crosshairThickness = 1.0f; init(); });

        addSlider(cx - 190, 134, 160, "Split", cfg.crosshairSplitDistance, 0.0, 10.0, false,
            v -> cfg.crosshairSplitDistance = v.floatValue(), "Extra outward offset for each arm");
        addTooltipped(cx - 28, 134, 22, sldH, "↺",
            "Reset Split to default (0.0)",
            () -> { cfg.crosshairSplitDistance = 0.0f; init(); });

        if (cfg.crosshairOutline) {
            addSlider(cx - 190, 158, 160, "Outline px", cfg.crosshairOutlineThickness, 0.5, 3.5, false,
                v -> cfg.crosshairOutlineThickness = v.floatValue(), "Outline border thickness in pixels");
            addTooltipped(cx - 28, 158, 22, sldH, "↺",
                "Reset Outline to default (1.0)",
                () -> { cfg.crosshairOutlineThickness = 1.0f; init(); });
        }

        // RIGHT Column (Colors)
        addSlider(cx + 5, 62, 185, "Red", cfg.crosshairRed, 0, 255, true,
            v -> cfg.crosshairRed = v.intValue(), "Red color component (0\u2013255)");
        addSlider(cx + 5, 86, 185, "Green", cfg.crosshairGreen, 0, 255, true,
            v -> cfg.crosshairGreen = v.intValue(), "Green color component (0\u2013255)");
        addSlider(cx + 5, 110, 185, "Blue", cfg.crosshairBlue, 0, 255, true,
            v -> cfg.crosshairBlue = v.intValue(), "Blue color component (0\u2013255)");
        addSlider(cx + 5, 134, 185, "Alpha", cfg.crosshairAlpha, 0, 255, true,
            v -> cfg.crosshairAlpha = v.intValue(), "Opacity (255 = fully opaque)");

        // ── TOGGLE ROW (Y = 184) ──
        int toggleRowY = 184;
        addTooltipped(cx - 190, toggleRowY, 75, btnH,
            "Recoil: " + (cfg.crosshairFollowRecoil ? "§aON" : "§7OFF"),
            "Crosshair follows weapon recoil pattern",
            () -> { cfg.crosshairFollowRecoil = !cfg.crosshairFollowRecoil; init(); });
        addTooltipped(cx - 110, toggleRowY, 60, btnH,
            "FixGap: " + (cfg.crosshairFixedGap ? "§aON" : "§7OFF"),
            "Lock gap to a fixed value regardless of weapon",
            () -> { cfg.crosshairFixedGap = !cfg.crosshairFixedGap; init(); });
        addTooltipped(cx - 45, toggleRowY, 75, btnH,
            "WpnGap: " + (cfg.crosshairGapUseWeapon ? "§aON" : "§7OFF"),
            "Adjust gap based on the equipped weapon",
            () -> { cfg.crosshairGapUseWeapon = !cfg.crosshairGapUseWeapon; init(); });

        // ── IMPORT/EXPORT ──
        int bottomY = 210;
        codeField = new EditBox(font, cx - 120, bottomY + 2, 195, 18, Component.literal(""));
        codeField.setMaxLength(200);
        codeField.setHint(Component.literal("PVP1;size;gap;thick;outline;r;g;b;a;style;dot;split"));
        codeField.setValue(existingText);
        if (wasFocused) {
            codeField.setFocused(true);
        }
        addRenderableWidget(codeField);

        int btnY = bottomY + 24;
        addTooltipped(cx - 120, btnY, 100, btnH,
            "⬇ Import",
            "Parse PVP format from the text field above",
            () -> {
                String raw = codeField.getValue().trim();
                if (raw.isEmpty()) { importStatus = "§cNothing to import"; return; }
                boolean ok = importPvpFormat(raw, cfg);
                importStatus = ok ? "§aImported!" : "§cInvalid format";
                init();
            });

        addTooltipped(cx - 15, btnY, 90, btnH,
            "📋 Export",
            "Copy current settings as a PVP format string",
            () -> {
                String code = exportPvpFormat(cfg);
                minecraft.keyboardHandler.setClipboard(code);
                importStatus = "§aCopied to clipboard!";
            });

        addTooltipped(cx + 80, btnY, 110, btnH,
            "↺ Reset All",
            "Restore all crosshair settings to defaults",
            () -> {
                cfg.crosshairSize = 3.0f;
                cfg.crosshairGap = 1.0f;
                cfg.crosshairThickness = 1.0f;
                cfg.crosshairOutlineThickness = 1.0f;
                cfg.crosshairRed = 0;
                cfg.crosshairGreen = 255;
                cfg.crosshairBlue = 0;
                cfg.crosshairAlpha = 255;
                cfg.crosshairDot = false;
                cfg.crosshairOutline = false;
                cfg.crosshairStyle = 0;
                cfg.crosshairSplitDistance = 0.0f;
                cfg.crosshairFollowRecoil = false;
                cfg.crosshairFixedGap = false;
                cfg.crosshairGapUseWeapon = false;
                cfg.crosshairSplitSizeRatio = 0.0f;
                importStatus = "§eReset crosshair settings";
                init();
            });

        // ── SAVE & CANCEL ──
        int bY = this.height - 24;
        addTooltipped(cx - 105, bY, 100, btnH,
            "Save & Close",
            "Save crosshair config to disk and close",
            () -> { PvpTweaksConfig.save(); minecraft.setScreen(parent); });
        addTooltipped(cx + 5, bY, 100, btnH,
            "Cancel",
            "Close without saving changes",
            () -> minecraft.setScreen(parent));
    }

    private void addTooltipped(int x, int y, int w, int h, String label, String tip, Runnable action) {
        var btn = addRenderableWidget(new ModernButtonWidget(x, y, w, h, Component.literal(label), action));
        tooltips.put(btn, tip);
    }

    private void addSlider(int x, int y, int w, String label, double val, double min, double max,
                           boolean isInt, java.util.function.Consumer<Double> setter, String tip) {
        var slider = addRenderableWidget(new CustomSliderWidget(x, y, w, 20, label, val, min, max, isInt, setter));
        tooltips.put(slider, tip);
    }

    private static boolean importPvpFormat(String text, PvpTweaksConfig cfg) {
        String[] parts = text.trim().split(";");
        if (parts.length < 12 || !parts[0].equals("PVP1")) return false;
        try {
            cfg.crosshairSize = Float.parseFloat(parts[1]);
            cfg.crosshairGap = Float.parseFloat(parts[2]);
            cfg.crosshairThickness = Float.parseFloat(parts[3]);
            cfg.crosshairOutlineThickness = Float.parseFloat(parts[4]);
            cfg.crosshairRed = Integer.parseInt(parts[5]);
            cfg.crosshairGreen = Integer.parseInt(parts[6]);
            cfg.crosshairBlue = Integer.parseInt(parts[7]);
            cfg.crosshairAlpha = Integer.parseInt(parts[8]);
            cfg.crosshairStyle = Integer.parseInt(parts[9]);
            cfg.crosshairDot = Integer.parseInt(parts[10]) != 0;
            cfg.crosshairSplitDistance = Float.parseFloat(parts[11]);
            cfg.customCrosshairEnabled = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String exportPvpFormat(PvpTweaksConfig cfg) {
        return String.format("PVP1;%s;%s;%s;%s;%d;%d;%d;%d;%d;%d;%s",
            fmt(cfg.crosshairSize), fmt(cfg.crosshairGap), fmt(cfg.crosshairThickness),
            fmt(cfg.crosshairOutlineThickness),
            cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue, cfg.crosshairAlpha,
            cfg.crosshairStyle, cfg.crosshairDot ? 1 : 0, fmt(cfg.crosshairSplitDistance));
    }

    private static String fmt(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            if (codeField != null && codeField.isFocused()) {
                codeField.setFocused(false);
                return true;
            }
            minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, this.width, this.height,
            UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);

        ctx.centeredText(font,
            Component.literal("§lCrosshair Adjuster"), this.width / 2, 10, 0xFFFFFFFF);

        RenderUtils.drawOutline(ctx, this.width / 2 - 1, 30, 1, this.height - 60, 1, 0x30FFFFFF);

        if (!importStatus.isEmpty()) {
            ctx.centeredText(font,
                Component.literal(importStatus), this.width / 2, this.height - 46, 0xFFFFFFFF);
        }

        int prevW = 60, prevH = 60;
        int px = this.width / 2 - 190;
        int py = 210;
        RenderUtils.drawRoundedRect(ctx, px, py, prevW, prevH, 4, 0xA0101020);
        RenderUtils.drawOutline(ctx, px, py, prevW, prevH, 1, UiPalette.BORDER);

        super.extractRenderState(ctx, mouseX, mouseY, delta);

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        var window = minecraft.getWindow();
        float s = (float) window.getGuiScale();
        ctx.pose().pushMatrix();
        ctx.pose().scale(1.0f / s, 1.0f / s);
        int cx = window.getWidth() / 2;
        int cy = window.getHeight() / 2;
        float pixelScale = (float) window.getHeight() / 1080.0f;
        CrosshairRenderer.drawNative(ctx, cx, cy, cfg, pixelScale);
        ctx.pose().popMatrix();

        int swatchColor = (cfg.crosshairAlpha << 24) | (cfg.crosshairRed << 16) |
                          (cfg.crosshairGreen << 8) | cfg.crosshairBlue;
        ctx.fill(px + prevW - 10, py + 2, px + prevW - 2, py + 10, 0xFF000000);
        ctx.fill(px + prevW - 9, py + 3, px + prevW - 3, py + 9, swatchColor);

        // ── TOOLTIP ON HOVER ──
        AbstractWidget hovered = null;
        for (AbstractWidget cw : tooltips.keySet()) {
            if (cw.isHovered()) {
                hovered = cw;
                break;
            }
        }
        if (hovered != null && !importStatus.contains("Imported") && !importStatus.contains("Reset")) {
            renderTooltip(ctx, tooltips.get(hovered), mouseX, mouseY);
        }
    }

    private void renderTooltip(GuiGraphicsExtractor ctx, String text, int mx, int my) {
        int tw = font.width(text);
        int tx = Math.max(3, Math.min(mx - tw / 2, width - tw - 3));
        int ty = Math.max(3, my - 22);
        ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + font.lineHeight + 2, 0xC0202020);
        ctx.text(font, Component.literal(text), tx, ty, 0xFFFFFFFF);
    }

    @Override
    public void onClose() { minecraft.setScreen(parent); }
}
