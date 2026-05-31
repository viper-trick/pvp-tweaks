package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Crosshair customisation screen.
 *
 * CS2 share-code decoder uses proper base-57 arithmetic (little-endian).
 * Byte layout after decode (18 bytes):
 *   [0]  gap           signed int8 (-10..+10)
 *   [1]  outline byte  bit0=drawOutline, bits1-7=outlineThickness*2
 *   [2]  R, [3] G, [4] B
 *   [5]  alpha
 *   [10] thickness     uint8 / 63.75 → 0-4
 *   [11] flags         bit0=dot, bit2=hasAlpha, bit3=tStyle, bits4-5=style, bit6=fixedGap
 *   [12] size          uint8 / 25.5 → 0-10
 */
public class CrosshairAdjusterScreen extends Screen {

    private final Screen parent;
    private static final String[] STYLE_NAMES = { "Cross", "Dot", "T-Shape", "X-Cross" };
    private TextFieldWidget codeField;
    private String importStatus = "";

    public CrosshairAdjusterScreen(Screen parent) {
        super(Text.literal("Crosshair Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        // ── Layout constants ───────────────────────────────────────────────────
        // Left column: toggles and dimension sliders
        // Right column: colour sliders + import
        // Preview: bottom-centre

        int lx = 20;              // left column X
        int lw = 175;             // left widget width
        int rx = this.width / 2 + 10;  // right column X
        int rw = 175;             // right widget width
        int btnH  = 20;
        int sldH  = 20;
        int btnSp = 30;           // spacing after button (btn height + gap)
        int sldSp = 35;           // spacing after slider (label + slider height + gap)

        // Left column
        int ly = 34;

        // Enable
        addDrawableChild(new ModernButtonWidget(lx, ly, lw, btnH,
            Text.literal("Custom Crosshair: " + (cfg.customCrosshairEnabled ? "§aON" : "§7OFF")),
            () -> { cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled; init(); }));
        ly += btnSp;

        // Style
        addDrawableChild(new ModernButtonWidget(lx, ly, lw, btnH,
            Text.literal("Style: §e" + STYLE_NAMES[Math.max(0, Math.min(cfg.crosshairStyle, STYLE_NAMES.length-1))]),
            () -> { cfg.crosshairStyle = (cfg.crosshairStyle + 1) % STYLE_NAMES.length; init(); }));
        ly += btnSp;

        // Center dot
        addDrawableChild(new ModernButtonWidget(lx, ly, lw, btnH,
            Text.literal("Center Dot: " + (cfg.crosshairDot ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairDot = !cfg.crosshairDot; init(); }));
        ly += btnSp;

        // Outline toggle
        addDrawableChild(new ModernButtonWidget(lx, ly, lw, btnH,
            Text.literal("Outline: " + (cfg.crosshairOutline ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairOutline = !cfg.crosshairOutline; init(); }));
        ly += btnSp + 10;  // extra gap before sliders

        // Size slider  (label renders 10px above slider)
        addSlider(lx, ly, lw - 26, "Size", cfg.crosshairSize, 1, 30, false,
            v -> cfg.crosshairSize = v.floatValue());
        addDrawableChild(new ModernButtonWidget(lx + lw - 22, ly, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairSize = 6f; init(); }));
        ly += sldSp;

        // Gap slider
        addSlider(lx, ly, lw - 26, "Gap", cfg.crosshairGap, 0, 20, false,
            v -> cfg.crosshairGap = v.floatValue());
        addDrawableChild(new ModernButtonWidget(lx + lw - 22, ly, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairGap = 3f; init(); }));
        ly += sldSp;

        // Thickness slider
        addSlider(lx, ly, lw - 26, "Thickness", cfg.crosshairThickness, 1, 8, false,
            v -> cfg.crosshairThickness = v.floatValue());
        addDrawableChild(new ModernButtonWidget(lx + lw - 22, ly, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairThickness = 1.5f; init(); }));
        ly += sldSp;

        // Outline thickness (only if outline enabled)
        if (cfg.crosshairOutline) {
            addSlider(lx, ly, lw - 26, "Outline px", cfg.crosshairOutlineThickness, 1, 5, false,
                v -> cfg.crosshairOutlineThickness = v.floatValue());
            addDrawableChild(new ModernButtonWidget(lx + lw - 22, ly, 22, sldH, Text.literal("↺"),
                () -> { cfg.crosshairOutlineThickness = 1f; init(); }));
            ly += sldSp;
        }

        // Right column
        int ry = 34;

        // Colour label header
        // R
        addSlider(rx, ry, rw, "Red", cfg.crosshairRed, 0, 255, true,
            v -> cfg.crosshairRed = v.intValue());
        ry += sldSp;

        // G
        addSlider(rx, ry, rw, "Green", cfg.crosshairGreen, 0, 255, true,
            v -> cfg.crosshairGreen = v.intValue());
        ry += sldSp;

        // B
        addSlider(rx, ry, rw, "Blue", cfg.crosshairBlue, 0, 255, true,
            v -> cfg.crosshairBlue = v.intValue());
        ry += sldSp;

        // Alpha
        addSlider(rx, ry, rw, "Alpha", cfg.crosshairAlpha, 0, 255, true,
            v -> cfg.crosshairAlpha = v.intValue());
        ry += sldSp + 14;

        // Import text field
        codeField = new TextFieldWidget(textRenderer, rx, ry, rw, 18, Text.literal(""));
        codeField.setMaxLength(200);
        codeField.setPlaceholder(Text.literal("§8Paste CS2 code or cfg commands…"));
        addDrawableChild(codeField);
        ry += 24;

        // Import button
        addDrawableChild(new ModernButtonWidget(rx, ry, rw, btnH,
            Text.literal("⬇ Import from CS2"), () -> {
                String raw = codeField.getText().trim();
                if (raw.isEmpty()) { importStatus = "§cNothing to import"; return; }
                boolean ok;
                if (raw.startsWith("CSGO-") || (raw.length() == 25 && raw.matches("[A-Za-z0-9]+"))) {
                    ok = importCs2ShareCode(raw, cfg);
                } else {
                    ok = importCs2ConsoleConfig(raw, cfg);
                }
                importStatus = ok ? "§aImported!" : "§cInvalid format";
                init();
            }));
        ry += btnSp;

        // Bottom buttons
        int bY = this.height - 28;
        addDrawableChild(new ModernButtonWidget(this.width / 2 - 106, bY, 100, btnH,
            Text.literal("Save & Close"),
            () -> { PvpTweaksConfig.save(); client.setScreen(parent); }));
        addDrawableChild(new ModernButtonWidget(this.width / 2 + 6, bY, 100, btnH,
            Text.literal("Cancel"),
            () -> client.setScreen(parent)));
    }

    private void addSlider(int x, int y, int w, String label, double val, double min, double max,
                           boolean isInt, java.util.function.Consumer<Double> setter) {
        addDrawableChild(new CustomSliderWidget(x, y, w, 20, label, val, min, max, isInt, setter));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CS2 share-code decoder (base-57, little-endian)
    // ══════════════════════════════════════════════════════════════════════════
    private static final String CS2_DICT =
        "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefhijkmnopqrstuvwxyz23456789";

    private static boolean importCs2ShareCode(String code, PvpTweaksConfig cfg) {
        try {
            code = code.replace("CSGO-", "").replace("-", "");
            if (code.length() != 25) return false;
            for (char c : code.toCharArray()) {
                if (CS2_DICT.indexOf(c) < 0) return false;
            }

            // Decode base-57 (little-endian) → big integer → 18 bytes LE
            java.math.BigInteger val = java.math.BigInteger.ZERO;
            java.math.BigInteger base = java.math.BigInteger.valueOf(57);
            java.math.BigInteger mult = java.math.BigInteger.ONE;
            for (char c : code.toCharArray()) {
                val = val.add(java.math.BigInteger.valueOf(CS2_DICT.indexOf(c)).multiply(mult));
                mult = mult.multiply(base);
            }
            byte[] raw = new byte[18];
            for (int i = 0; i < 18; i++) {
                raw[i] = val.and(java.math.BigInteger.valueOf(0xFF)).byteValue();
                val = val.shiftRight(8);
            }

            // Parse struct (verified layout)
            int gap        = raw[0];           // signed int8: -10..+10
            int outlineByte = raw[1] & 0xFF;
            int r          = raw[2] & 0xFF;
            int g          = raw[3] & 0xFF;
            int b          = raw[4] & 0xFF;
            int a          = raw[5] & 0xFF;
            float thickness = (raw[10] & 0xFF) / 63.75f;  // 0-4
            int flags       = raw[11] & 0xFF;
            float size      = (raw[12] & 0xFF) / 25.5f;   // 0-10

            boolean dot     = (flags & 0x01) != 0;
            boolean hasAlpha = (flags & 0x04) != 0;
            boolean tStyle  = (flags & 0x08) != 0;
            boolean outline = (outlineByte & 0x01) != 0;
            float outThick  = ((outlineByte >> 1) & 0x7F) / 2.0f;  // 0-3.5 range

            // Map CS2 → Minecraft coordinate space
            // CS2 size 0-10 → MC size 1-20 (×2)
            cfg.crosshairSize      = Math.max(1f, size * 2f);
            // CS2 gap -10..+10 → MC gap 0-20 (offset +4 for typical default)
            cfg.crosshairGap       = Math.max(0f, Math.min(20f, gap + 4f));
            // CS2 thickness 0-4 → MC thickness 1-8 (×2)
            cfg.crosshairThickness = Math.max(1f, thickness * 2f);
            cfg.crosshairRed       = r;
            cfg.crosshairGreen     = g;
            cfg.crosshairBlue      = b;
            cfg.crosshairAlpha     = hasAlpha ? a : 255;
            cfg.crosshairDot       = dot;
            cfg.crosshairOutline   = outline;
            cfg.crosshairOutlineThickness = Math.max(1f, outThick);
            cfg.crosshairStyle     = tStyle ? 2 : 0;   // T-Shape or Cross
            cfg.customCrosshairEnabled = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CS2 console-command string parser
    //  e.g.  cl_crosshairsize "3"; cl_crosshairgap "-1"; cl_crosshaircolor_r "0";
    // ══════════════════════════════════════════════════════════════════════════
    private static boolean importCs2ConsoleConfig(String text, PvpTweaksConfig cfg) {
        boolean anyHit = false;
        for (String segment : text.replace(";", "\n").split("\n")) {
            segment = segment.trim();
            if (segment.isEmpty()) continue;
            String[] parts = segment.split("\\s+", 2);
            if (parts.length < 2) continue;
            String key = parts[0].toLowerCase().replace("cl_crosshair", "");
            String valStr = parts[1].replaceAll("[\"' ]", "");
            float fv;
            try { fv = Float.parseFloat(valStr); } catch (NumberFormatException e) { continue; }

            switch (key) {
                case "size":      case "_size":      cfg.crosshairSize      = Math.max(1f, fv * 2f);  anyHit = true; break;
                case "gap":       case "_gap":       cfg.crosshairGap       = Math.max(0f, fv + 4f); anyHit = true; break;
                case "thickness": case "_thickness": cfg.crosshairThickness = Math.max(1f, fv * 2f);  anyHit = true; break;
                case "color_r":                      cfg.crosshairRed       = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "color_g":                      cfg.crosshairGreen     = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "color_b":                      cfg.crosshairBlue      = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "alpha":                        cfg.crosshairAlpha     = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "dot":       case "_dot":       cfg.crosshairDot       = fv > 0; anyHit = true; break;
                case "_t":                           cfg.crosshairStyle     = fv > 0 ? 2 : 0; anyHit = true; break;
                case "drawoutline": case "_drawoutline": cfg.crosshairOutline = fv > 0; anyHit = true; break;
                case "outlinethickness": case "_outlinethickness":
                    cfg.crosshairOutlineThickness = Math.max(1f, fv); anyHit = true; break;
            }
        }
        if (anyHit) cfg.customCrosshairEnabled = true;
        return anyHit;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Render
    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Background
        RenderUtils.drawGradientRect(ctx, 0, 0, this.width, this.height,
            UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);

        // Title
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§lCrosshair Adjuster"), this.width / 2, 12, 0xFFFFFFFF);

        // Column divider
        RenderUtils.drawOutline(ctx, this.width / 2 - 1, 30, 1, this.height - 60, 1, 0x30FFFFFF);

        // Import status
        if (!importStatus.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(importStatus), this.width / 2, this.height - 46, 0xFFFFFFFF);
        }

        // Preview panel — centred at bottom
        int prevW = 90, prevH = 90;
        int px = this.width / 2 - prevW / 2;
        int py = this.height - prevH - 35;
        RenderUtils.drawRoundedRect(ctx, px, py, prevW, prevH, 6, 0xA0101020);
        RenderUtils.drawOutline(ctx, px, py, prevW, prevH, 1, UiPalette.BORDER);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§8Preview"), this.width / 2, py - 11, 0xFFAAAAAA);

        // Draw all widgets
        super.render(ctx, mouseX, mouseY, delta);

        // Crosshair preview ON TOP of the panel (drawn after super so it's visible over panel)
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        CrosshairRenderer.draw(ctx, px + prevW / 2, py + prevH / 2, cfg);

        // Preview colour swatch
        int swatchColor = (cfg.crosshairAlpha << 24) | (cfg.crosshairRed << 16) |
                          (cfg.crosshairGreen << 8) | cfg.crosshairBlue;
        ctx.fill(px + prevW - 14, py + 3, px + prevW - 3, py + 14, 0xFF000000);
        ctx.fill(px + prevW - 13, py + 4, px + prevW - 4, py + 13, swatchColor);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
