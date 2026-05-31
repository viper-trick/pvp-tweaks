package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Full crosshair customisation screen.
 *
 * Supports:
 *  • Style (Cross / Dot / T-shape / X-cross)
 *  • Color (R, G, B, Alpha) via sliders
 *  • Size, Thickness, Gap sliders
 *  • Dot + Outline toggles
 *  • CS2 share-code import  (CSGO-XXXXX-…)
 *  • CS2 console-cfg import (cl_crosshair_size "3"; …)
 */
public class CrosshairAdjusterScreen extends Screen {

    private final Screen parent;
    private static final String[] STYLE_NAMES = {"Cross", "Dot", "T-Shape", "X-Cross"};

    // --- text field for code import ---
    private net.minecraft.client.gui.widget.TextFieldWidget codeField;

    public CrosshairAdjusterScreen(Screen parent) {
        super(Text.literal("Crosshair Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        int cx = this.width / 2;
        int leftX  = 25;
        int rightX = cx + 15;
        int y = 35;
        int spacing = 28;

        // ── Enable toggle ──────────────────────────────────────────────────────
        addDrawableChild(new ModernButtonWidget(leftX, y, 180, 20,
            Text.literal("Custom Crosshair: " + (cfg.customCrosshairEnabled ? "§aON" : "§7OFF")),
            () -> { cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled; init(); }));
        y += spacing;

        // ── Style ──────────────────────────────────────────────────────────────
        addDrawableChild(new ModernButtonWidget(leftX, y, 180, 20,
            Text.literal("Style: §e" + STYLE_NAMES[cfg.crosshairStyle]),
            () -> { cfg.crosshairStyle = (cfg.crosshairStyle + 1) % STYLE_NAMES.length; init(); }));
        y += spacing;

        // ── Dot ───────────────────────────────────────────────────────────────
        addDrawableChild(new ModernButtonWidget(leftX, y, 180, 20,
            Text.literal("Center Dot: " + (cfg.crosshairDot ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairDot = !cfg.crosshairDot; init(); }));
        y += spacing;

        // ── Outline ───────────────────────────────────────────────────────────
        addDrawableChild(new ModernButtonWidget(leftX, y, 180, 20,
            Text.literal("Outline: " + (cfg.crosshairOutline ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairOutline = !cfg.crosshairOutline; init(); }));
        y += spacing;

        // ── Size slider ───────────────────────────────────────────────────────
        addDrawableChild(new CustomSliderWidget(leftX, y, 155, 20, "Size",
            cfg.crosshairSize, 1, 30, false,
            v -> cfg.crosshairSize = v.floatValue()));
        addDrawableChild(new ModernButtonWidget(leftX + 160, y, 20, 20, Text.literal("↺"),
            () -> { cfg.crosshairSize = 6f; init(); }));
        y += spacing;

        // ── Gap slider ────────────────────────────────────────────────────────
        addDrawableChild(new CustomSliderWidget(leftX, y, 155, 20, "Gap",
            cfg.crosshairGap, 0, 20, false,
            v -> cfg.crosshairGap = v.floatValue()));
        addDrawableChild(new ModernButtonWidget(leftX + 160, y, 20, 20, Text.literal("↺"),
            () -> { cfg.crosshairGap = 3f; init(); }));
        y += spacing;

        // ── Thickness slider ──────────────────────────────────────────────────
        addDrawableChild(new CustomSliderWidget(leftX, y, 155, 20, "Thickness",
            cfg.crosshairThickness, 0.5, 6, false,
            v -> cfg.crosshairThickness = v.floatValue()));
        addDrawableChild(new ModernButtonWidget(leftX + 160, y, 20, 20, Text.literal("↺"),
            () -> { cfg.crosshairThickness = 1.5f; init(); }));
        y += spacing;

        // ── Outline thickness slider ──────────────────────────────────────────
        if (cfg.crosshairOutline) {
            addDrawableChild(new CustomSliderWidget(leftX, y, 155, 20, "Outline Px",
                cfg.crosshairOutlineThickness, 0.5, 4, false,
                v -> cfg.crosshairOutlineThickness = v.floatValue()));
            addDrawableChild(new ModernButtonWidget(leftX + 160, y, 20, 20, Text.literal("↺"),
                () -> { cfg.crosshairOutlineThickness = 1f; init(); }));
            y += spacing;
        }

        // ── Right column — colour sliders ──────────────────────────────────────
        int ry = 35;
        addDrawableChild(new CustomSliderWidget(rightX, ry, 155, 20, "Red",
            cfg.crosshairRed, 0, 255, true,
            v -> cfg.crosshairRed = v.intValue()));
        ry += spacing;
        addDrawableChild(new CustomSliderWidget(rightX, ry, 155, 20, "Green",
            cfg.crosshairGreen, 0, 255, true,
            v -> cfg.crosshairGreen = v.intValue()));
        ry += spacing;
        addDrawableChild(new CustomSliderWidget(rightX, ry, 155, 20, "Blue",
            cfg.crosshairBlue, 0, 255, true,
            v -> cfg.crosshairBlue = v.intValue()));
        ry += spacing;
        addDrawableChild(new CustomSliderWidget(rightX, ry, 155, 20, "Alpha",
            cfg.crosshairAlpha, 0, 255, true,
            v -> cfg.crosshairAlpha = v.intValue()));
        ry += spacing + 8;

        // ── Import section ────────────────────────────────────────────────────
        codeField = new net.minecraft.client.gui.widget.TextFieldWidget(
            textRenderer, rightX, ry, 200, 18, Text.literal(""));
        codeField.setMaxLength(256);
        codeField.setPlaceholder(Text.literal("§7Paste CS2 code or cfg…"));
        addDrawableChild(codeField);
        ry += 24;

        addDrawableChild(new ModernButtonWidget(rightX, ry, 200, 20,
            Text.literal("⬇ Import"), () -> {
            String raw = codeField.getText().trim();
            if (!raw.isEmpty()) {
                if (raw.startsWith("CSGO-") || raw.matches("[A-Za-z0-9]{25}")) {
                    importCs2ShareCode(raw, cfg);
                } else {
                    importCs2ConsoleConfig(raw, cfg);
                }
                init();
            }
        }));

        // ── Bottom buttons ────────────────────────────────────────────────────
        int bY = this.height - 28;
        addDrawableChild(new ModernButtonWidget(this.width / 2 - 110, bY, 100, 20,
            Text.literal("Save & Close"), () -> { PvpTweaksConfig.save(); client.setScreen(parent); }));
        addDrawableChild(new ModernButtonWidget(this.width / 2 + 10, bY, 100, 20,
            Text.literal("Cancel"), () -> client.setScreen(parent)));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CS2 share-code decoder
    //  Format: CSGO-AAAAA-BBBBB-CCCCC-DDDDD-EEEEE (25 chars after stripping)
    //  Each char encodes 6 bits; 25×6 = 150 bits = 18 bytes
    // ═══════════════════════════════════════════════════════════════════════════
    private static final String CS2_DICT =
        "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefhijkmnopqrstuvwxyz23456789";

    private static void importCs2ShareCode(String code, PvpTweaksConfig cfg) {
        try {
            code = code.replace("CSGO-", "").replace("-", "");
            if (code.length() != 25) return;

            // Decode 25 × 6-bit values → 18 bytes (bit-stream, MSB first)
            int bits = 0, bitCount = 0;
            byte[] bytes = new byte[18];
            int byteIdx = 0;
            for (char c : code.toCharArray()) {
                int v = CS2_DICT.indexOf(c);
                if (v < 0) return;
                bits = (bits << 6) | v;
                bitCount += 6;
                while (bitCount >= 8 && byteIdx < 18) {
                    bitCount -= 8;
                    bytes[byteIdx++] = (byte)((bits >> bitCount) & 0xFF);
                }
            }

            // Parse byte layout (community-documented CS2 struct):
            // [0]  : always 0 (skip)
            // [1]  : gap, stored as signed byte (raw value ≈ cl_crosshairgap × 1)
            // [2]  : outline byte (bit0 = drawoutline, bits1-7 = thickness × 42)
            // [3-5]: R, G, B
            // [6]  : Alpha
            // [7]  : split distance (unused)
            // [8]  : inner split alpha / 255
            // [9]  : outer split alpha / 255
            // [10] : split ratio / 255
            // [11] : thickness, scaled: val / 63.75f (range 0-4)
            // [12] : bitfield — bit0=dot, bit1=deployed_gap, bit2=has_alpha,
            //                   bit3=t_style, bits4-5=style, bit6=fixed_gap
            // [13] : size, scaled: val / 25.5f  (range 0-10)
            // [14-17]: padding

            float gap       = (float)(byte)bytes[1];          // signed
            int   outlineByte = bytes[2] & 0xFF;
            int   r         = bytes[3] & 0xFF;
            int   g         = bytes[4] & 0xFF;
            int   b         = bytes[5] & 0xFF;
            int   a         = bytes[6] & 0xFF;
            float thickness = (bytes[11] & 0xFF) / 63.75f;
            int   bitfield  = bytes[12] & 0xFF;
            float size      = (bytes[13] & 0xFF) / 25.5f;

            boolean dot    = (bitfield & 0x01) != 0;
            boolean tStyle = (bitfield & 0x08) != 0;
            int     style  = (bitfield >> 4) & 0x03;
            boolean outline = (outlineByte & 0x01) != 0;
            float   outlineThick = ((outlineByte >> 1) & 0x7F) / 42.0f;

            // Map CS2 style (0=default, 1=default static, 2=classic, 3=dynamic, 4=classic static, 5=legacy)
            // to our styles: 0=cross, 1=dot, 2=T, 3=X
            int mcStyle = tStyle ? 2 : 0;

            // Map CS2 size (0-10) to Minecraft pixels (multiply by 1.5 as approximation)
            cfg.crosshairSize      = Math.max(1f, size * 1.5f);
            cfg.crosshairGap       = Math.max(0f, gap + 3f);   // CS2 gap -10..+10 → shift to positive range
            cfg.crosshairThickness = Math.max(0.5f, thickness * 2f);
            cfg.crosshairRed       = r;
            cfg.crosshairGreen     = g;
            cfg.crosshairBlue      = b;
            cfg.crosshairAlpha     = (a == 0) ? 200 : a;
            cfg.crosshairDot       = dot;
            cfg.crosshairOutline   = outline;
            cfg.crosshairOutlineThickness = Math.max(0.5f, outlineThick);
            cfg.crosshairStyle     = mcStyle;
            cfg.customCrosshairEnabled = true;

        } catch (Exception ignored) {}
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CS2 console-command string parser
    //  e.g.: cl_crosshair_size "3"; cl_crosshairgap "-1"; ...
    // ═══════════════════════════════════════════════════════════════════════════
    private static void importCs2ConsoleConfig(String cfg_text, PvpTweaksConfig cfg) {
        try {
            // normalize separators
            String text = cfg_text.replace(";", "\n");
            for (String line : text.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) continue;
                String key = parts[0].toLowerCase();
                String val = parts[1].replaceAll("[\"']", "").trim();
                float fv;
                try { fv = Float.parseFloat(val); } catch (NumberFormatException e) { continue; }

                switch (key) {
                    case "cl_crosshairsize":
                    case "cl_crosshair_size":
                        cfg.crosshairSize = Math.max(1f, fv * 1.5f); break;
                    case "cl_crosshairgap":
                    case "cl_crosshair_gap":
                        cfg.crosshairGap = Math.max(0f, fv + 3f); break;
                    case "cl_crosshairthickness":
                    case "cl_crosshair_thickness":
                        cfg.crosshairThickness = Math.max(0.5f, fv * 2f); break;
                    case "cl_crosshaircolor_r": cfg.crosshairRed   = (int)fv; break;
                    case "cl_crosshaircolor_g": cfg.crosshairGreen = (int)fv; break;
                    case "cl_crosshaircolor_b": cfg.crosshairBlue  = (int)fv; break;
                    case "cl_crosshairalpha":   cfg.crosshairAlpha = (int)fv; break;
                    case "cl_crosshairdot":
                    case "cl_crosshair_dot":    cfg.crosshairDot   = fv > 0; break;
                    case "cl_crosshair_t":      cfg.crosshairStyle = (fv > 0) ? 2 : 0; break;
                    case "cl_crosshairdrawoutline":
                    case "cl_crosshair_drawoutline": cfg.crosshairOutline = fv > 0; break;
                    case "cl_crosshairoutlinethickness":
                    case "cl_crosshair_outlinethickness":
                        cfg.crosshairOutlineThickness = Math.max(0.5f, fv); break;
                }
            }
            cfg.customCrosshairEnabled = true;
        } catch (Exception ignored) {}
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Render – draw a live preview of the crosshair
    // ═══════════════════════════════════════════════════════════════════════════
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, this.width, this.height,
            UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);

        // Title
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§lCrosshair Adjuster"), this.width / 2, 12, 0xFFFFFFFF);

        // Preview panel background
        int previewSize = 80;
        int px = this.width / 2 - previewSize / 2;
        int py = this.height - 120;
        RenderUtils.drawRoundedRect(ctx, px, py, previewSize, previewSize, 6, 0x90000000);
        RenderUtils.drawOutline(ctx, px, py, previewSize, previewSize, 1, UiPalette.BORDER);

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§7Preview"), this.width / 2, py - 12, UiPalette.TEXT_SECONDARY);

        // Draw crosshair preview
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.customCrosshairEnabled) {
            int cx = this.width / 2;
            int cy = py + previewSize / 2;
            CrosshairRenderer.draw(ctx, cx, cy, cfg);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
