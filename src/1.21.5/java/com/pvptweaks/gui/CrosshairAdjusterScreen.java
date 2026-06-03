package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

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
        String existingText = (codeField != null) ? codeField.getText() : "";

        this.clearChildren();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        int cx = this.width / 2;
        int btnH = 20;
        int sldH = 20;

        // ── TOP TOGGLES ROW (Y = 32) ──
        addDrawableChild(new ModernButtonWidget(cx - 190, 32, 90, btnH,
            Text.literal("Custom: " + (cfg.customCrosshairEnabled ? "§aON" : "§7OFF")),
            () -> { cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled; init(); }));

        addDrawableChild(new ModernButtonWidget(cx - 95, 32, 90, btnH,
            Text.literal("Style: §e" + STYLE_NAMES[Math.max(0, Math.min(cfg.crosshairStyle, STYLE_NAMES.length-1))]),
            () -> { cfg.crosshairStyle = (cfg.crosshairStyle + 1) % STYLE_NAMES.length; init(); }));

        addDrawableChild(new ModernButtonWidget(cx + 5, 32, 90, btnH,
            Text.literal("Dot: " + (cfg.crosshairDot ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairDot = !cfg.crosshairDot; init(); }));

        addDrawableChild(new ModernButtonWidget(cx + 100, 32, 90, btnH,
            Text.literal("Outline: " + (cfg.crosshairOutline ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairOutline = !cfg.crosshairOutline; init(); }));

        // ── LEFT SLIDERS (Dimensions) ──
        addSlider(cx - 190, 62, 160, "Size", cfg.crosshairSize, 0.0, 10.0, false,
            v -> cfg.crosshairSize = v.floatValue());
        addDrawableChild(new ModernButtonWidget(cx - 28, 62, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairSize = 3.0f; init(); }));

        addSlider(cx - 190, 86, 160, "Gap", cfg.crosshairGap, -10.0, 10.0, false,
            v -> cfg.crosshairGap = v.floatValue());
        addDrawableChild(new ModernButtonWidget(cx - 28, 86, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairGap = -2.0f; init(); }));

        addSlider(cx - 190, 110, 160, "Thickness", cfg.crosshairThickness, 0.0, 6.0, false,
            v -> cfg.crosshairThickness = v.floatValue());
        addDrawableChild(new ModernButtonWidget(cx - 28, 110, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairThickness = 1.0f; init(); }));

        addSlider(cx - 190, 134, 160, "Split", cfg.crosshairSplitDistance, 0.0, 10.0, false,
            v -> cfg.crosshairSplitDistance = v.floatValue());
        addDrawableChild(new ModernButtonWidget(cx - 28, 134, 22, sldH, Text.literal("↺"),
            () -> { cfg.crosshairSplitDistance = 0.0f; init(); }));

        if (cfg.crosshairOutline) {
            addSlider(cx - 190, 158, 160, "Outline px", cfg.crosshairOutlineThickness, 0.5, 3.5, false,
                v -> cfg.crosshairOutlineThickness = v.floatValue());
            addDrawableChild(new ModernButtonWidget(cx - 28, 158, 22, sldH, Text.literal("↺"),
                () -> { cfg.crosshairOutlineThickness = 1.0f; init(); }));
        }

        // ── RIGHT SLIDERS (Colors) ──
        addSlider(cx + 5, 62, 185, "Red", cfg.crosshairRed, 0, 255, true,
            v -> cfg.crosshairRed = v.intValue());
        addSlider(cx + 5, 86, 185, "Green", cfg.crosshairGreen, 0, 255, true,
            v -> cfg.crosshairGreen = v.intValue());
        addSlider(cx + 5, 110, 185, "Blue", cfg.crosshairBlue, 0, 255, true,
            v -> cfg.crosshairBlue = v.intValue());
        addSlider(cx + 5, 134, 185, "Alpha", cfg.crosshairAlpha, 0, 255, true,
            v -> cfg.crosshairAlpha = v.intValue());

        // ── TOGGLE ROW ──
        int toggleRowY = 164;
        addDrawableChild(new ModernButtonWidget(cx - 190, toggleRowY, 75, btnH,
            Text.literal("Recoil: " + (cfg.crosshairFollowRecoil ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairFollowRecoil = !cfg.crosshairFollowRecoil; init(); }));
        addDrawableChild(new ModernButtonWidget(cx - 110, toggleRowY, 60, btnH,
            Text.literal("FixGap: " + (cfg.crosshairFixedGap ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairFixedGap = !cfg.crosshairFixedGap; init(); }));
        addDrawableChild(new ModernButtonWidget(cx - 45, toggleRowY, 75, btnH,
            Text.literal("WpnGap: " + (cfg.crosshairGapUseWeapon ? "§aON" : "§7OFF")),
            () -> { cfg.crosshairGapUseWeapon = !cfg.crosshairGapUseWeapon; init(); }));

        // ── IMPORT / EXPORT ──
        int bottomY = 190;
        codeField = new TextFieldWidget(textRenderer, cx - 120, bottomY + 2, 195, 18, Text.literal(""));
        codeField.setMaxLength(200);
        codeField.setPlaceholder(Text.literal("§8Paste CS2 code or config…"));
        codeField.setText(existingText);
        addDrawableChild(codeField);

        int btnY = bottomY + 24;
        addDrawableChild(new ModernButtonWidget(cx - 120, btnY, 100, btnH,
            Text.literal("⬇ Import"), () -> {
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

        addDrawableChild(new ModernButtonWidget(cx - 15, btnY, 90, btnH,
            Text.literal("📋 Copy Code"), () -> {
                String code = exportCs2ShareCode(cfg);
                if (!code.isEmpty()) {
                    client.keyboard.setClipboard(code);
                    importStatus = "§aCopied CS2 share code!";
                } else {
                    importStatus = "§cFailed to generate code";
                }
            }));

        addDrawableChild(new ModernButtonWidget(cx + 80, btnY, 110, btnH,
            Text.literal("↺ Reset All"), () -> {
                cfg.crosshairSize = 3.0f; cfg.crosshairGap = -2.0f; cfg.crosshairThickness = 1.0f;
                cfg.crosshairOutlineThickness = 1.0f; cfg.crosshairRed = 0; cfg.crosshairGreen = 255;
                cfg.crosshairBlue = 0; cfg.crosshairAlpha = 255; cfg.crosshairDot = false;
                cfg.crosshairOutline = false; cfg.crosshairStyle = 0;
                cfg.crosshairSplitDistance = 0.0f; cfg.crosshairFollowRecoil = false;
                cfg.crosshairFixedGap = false; cfg.crosshairGapUseWeapon = false;
                cfg.crosshairSplitSizeRatio = 0.0f;
                importStatus = "§eReset crosshair settings"; init();
            }));

        // ── BOTTOM BUTTONS ──
        int bY = this.height - 24;
        addDrawableChild(new ModernButtonWidget(cx - 105, bY, 100, btnH,
            Text.literal("Save & Close"),
            () -> { PvpTweaksConfig.save(); client.setScreen(parent); }));
        addDrawableChild(new ModernButtonWidget(cx + 5, bY, 100, btnH,
            Text.literal("Cancel"),
            () -> client.setScreen(parent)));
    }

    private void addSlider(int x, int y, int w, String label, double val, double min, double max,
                           boolean isInt, java.util.function.Consumer<Double> setter) {
        addDrawableChild(new CustomSliderWidget(x, y, w, 20, label, val, min, max, isInt, setter));
    }

    private static final String CS2_DICT =
        "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefhijkmnopqrstuvwxyz23456789";

    private static boolean importCs2ShareCode(String code, PvpTweaksConfig cfg) {
        try {
            code = code.replace("CSGO-", "").replace("-", "");
            if (code.length() != 25) return false;
            for (char c : code.toCharArray()) {
                if (CS2_DICT.indexOf(c) < 0) return false;
            }

            java.math.BigInteger val = java.math.BigInteger.ZERO;
            java.math.BigInteger base = java.math.BigInteger.valueOf(57);
            for (int i = code.length() - 1; i >= 0; i--) {
                char c = code.charAt(i);
                int idx = CS2_DICT.indexOf(c);
                if (idx < 0) return false;
                val = val.multiply(base).add(java.math.BigInteger.valueOf(idx));
            }

            byte[] raw = new byte[18];
            for (int i = 17; i >= 0; i--) {
                raw[i] = (byte) (val.and(java.math.BigInteger.valueOf(0xFF)).intValue());
                val = val.shiftRight(8);
            }

            int sum = 0;
            for (int i = 1; i < 18; i++) {
                sum += raw[i] & 0xFF;
            }
            if ((raw[0] & 0xFF) != (sum % 256)) return false;

            float gap = ((byte) raw[2]) / 10.0f;
            float outline = (raw[3] & 0xFF) / 2.0f;
            int r = raw[4] & 0xFF;
            int g = raw[5] & 0xFF;
            int b = raw[6] & 0xFF;
            int a = raw[7] & 0xFF;
            int splitDist = raw[8] & 0x7F;
            boolean followRecoil = (raw[8] & 0x80) != 0;
            float fixedGap = ((byte) raw[9]) / 10.0f;
            boolean outlineEnabled = (raw[10] & 8) == 8;
            float splitSizeRatio = ((raw[11] >> 4) & 0xF) / 10.0f;
            float thickness = (raw[12] & 0x3F) / 10.0f;
            int flags = raw[13] & 0xFF;
            boolean centerDotEnabled = ((flags >> 4) & 1) == 1;
            boolean gapUseWeapon = ((flags >> 4) & 2) == 2;
            boolean alphaEnabled = ((flags >> 4) & 4) == 4;
            boolean tStyleEnabled = ((flags >> 4) & 8) == 8;
            int csStyle = (flags & 0x0e) >> 1;
            float size = (((raw[15] & 0x1F) << 8) | (raw[14] & 0xFF)) / 10.0f;

            // Map CS2 style to our style enum:
            if (tStyleEnabled) {
                cfg.crosshairStyle = 2;
            } else if (csStyle == 2) {
                cfg.crosshairStyle = 3;
            } else if (size <= 0 && centerDotEnabled) {
                cfg.crosshairStyle = 1;
            } else {
                cfg.crosshairStyle = 0;
            }

            cfg.crosshairSize = size;
            cfg.crosshairGap = gap;
            cfg.crosshairThickness = thickness;
            cfg.crosshairRed = r;
            cfg.crosshairGreen = g;
            cfg.crosshairBlue = b;
            cfg.crosshairAlpha = alphaEnabled ? a : 255;
            cfg.crosshairDot = centerDotEnabled;
            cfg.crosshairOutline = outlineEnabled;
            cfg.crosshairOutlineThickness = outline;
            cfg.crosshairSplitDistance = splitDist;
            cfg.crosshairFollowRecoil = followRecoil;
            cfg.crosshairFixedGap = fixedGap != 0.0f;
            cfg.crosshairGapUseWeapon = gapUseWeapon;
            cfg.crosshairSplitSizeRatio = splitSizeRatio;
            cfg.customCrosshairEnabled = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
                case "size": case "_size": cfg.crosshairSize = fv; anyHit = true; break;
                case "gap": case "_gap": cfg.crosshairGap = fv; anyHit = true; break;
                case "thickness": case "_thickness": cfg.crosshairThickness = fv; anyHit = true; break;
                case "color_r": cfg.crosshairRed = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "color_g": cfg.crosshairGreen = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "color_b": cfg.crosshairBlue = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "alpha": cfg.crosshairAlpha = Math.max(0, Math.min(255, (int)fv)); anyHit = true; break;
                case "dot": case "_dot": cfg.crosshairDot = fv > 0; anyHit = true; break;
                case "_t": cfg.crosshairStyle = fv > 0 ? 2 : 0; anyHit = true; break;
                case "drawoutline": case "_drawoutline": cfg.crosshairOutline = fv > 0; anyHit = true; break;
                case "outlinethickness": case "_outlinethickness": cfg.crosshairOutlineThickness = fv; anyHit = true; break;
                case "dynamic_splitdist": case "_dynamic_splitdist": cfg.crosshairSplitDistance = fv; anyHit = true; break;
                case "follow_recoil": case "_follow_recoil": cfg.crosshairFollowRecoil = fv > 0; anyHit = true; break;
                case "fixed_gap": case "_fixed_gap": cfg.crosshairFixedGap = fv > 0; anyHit = true; break;
                case "gap_use_weapon_value": case "_gap_use_weapon_value": cfg.crosshairGapUseWeapon = fv > 0; anyHit = true; break;
                case "dynamic_splitalpha_ot": case "_dynamic_splitalpha_ot": cfg.crosshairSplitSizeRatio = fv; anyHit = true; break;
            }
        }
        if (anyHit) cfg.customCrosshairEnabled = true;
        return anyHit;
    }

    public static String exportCs2ShareCode(PvpTweaksConfig cfg) {
        try {
            int gap = Math.round(cfg.crosshairGap * 10.0f);
            int outline = Math.round(cfg.crosshairOutlineThickness * 2.0f);
            int thickness = Math.round(cfg.crosshairThickness * 10.0f);
            int length = Math.round(cfg.crosshairSize * 10.0f);

            int[] bytes = new int[18];
            bytes[0] = 0;
            bytes[1] = 1;
            bytes[2] = gap & 0xFF;
            bytes[3] = outline & 0xFF;
            bytes[4] = cfg.crosshairRed & 0xFF;
            bytes[5] = cfg.crosshairGreen & 0xFF;
            bytes[6] = cfg.crosshairBlue & 0xFF;
            bytes[7] = cfg.crosshairAlpha & 0xFF;

            bytes[8] = (Math.round(cfg.crosshairSplitDistance) & 0x7F) | (cfg.crosshairFollowRecoil ? 0x80 : 0);
            bytes[9] = cfg.crosshairFixedGap ? 10 & 0xFF : 0;
            bytes[10] = 5 | (cfg.crosshairOutline ? 8 : 0) | (10 << 4);
            bytes[11] = 10 | ((Math.round(cfg.crosshairSplitSizeRatio * 10.0f) & 0xF) << 4);

            bytes[12] = thickness & 0xFF;

            int flags = 0;
            switch (cfg.crosshairStyle) {
                case 2 -> {
                    flags |= 8;     // Classic Static (style 4 << 1)
                    flags |= (1 << 7);  // tStyle flag
                }
                case 3 -> flags |= 4;   // Classic (style 2 << 1)
                default -> flags |= 8;  // Classic Static (style 4 << 1)
            }
            if (cfg.crosshairDot) flags |= (1 << 4);
            if (cfg.crosshairGapUseWeapon) flags |= (1 << 5);
            flags |= (1 << 6);
            bytes[13] = flags & 0xFF;

            // Size uses 13 bits across bytes 14-15 (5 from byte 15, 8 from byte 14)
            bytes[14] = length & 0xFF;
            bytes[15] = (length >> 8) & 0x1F;
            bytes[16] = 0;
            bytes[17] = 0;

            int sum = 0;
            for (int i = 1; i < 18; i++) {
                sum += bytes[i];
            }
            bytes[0] = sum % 256;

            java.math.BigInteger val = java.math.BigInteger.ZERO;
            for (int i = 0; i < 18; i++) {
                val = val.shiftLeft(8).add(java.math.BigInteger.valueOf(bytes[i]));
            }

            StringBuilder sb = new StringBuilder();
            java.math.BigInteger base = java.math.BigInteger.valueOf(57);
            for (int i = 0; i < 25; i++) {
                java.math.BigInteger[] divRem = val.divideAndRemainder(base);
                sb.append(CS2_DICT.charAt(divRem[1].intValue()));
                val = divRem[0];
            }
            String chars = sb.toString();
            return "CSGO-" + chars.substring(0, 5) + "-" + chars.substring(5, 10) + "-" +
                   chars.substring(10, 15) + "-" + chars.substring(15, 20) + "-" + chars.substring(20, 25);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (codeField != null && codeField.isFocused()) { codeField.setFocused(false); return true; }
            client.setScreen(parent); return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, this.width, this.height,
            UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§lCrosshair Adjuster"), this.width / 2, 10, 0xFFFFFFFF);
        RenderUtils.drawOutline(ctx, this.width / 2 - 1, 30, 1, this.height - 60, 1, 0x30FFFFFF);
        if (!importStatus.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(importStatus), this.width / 2, this.height - 46, 0xFFFFFFFF);
        }
        int prevW = 60, prevH = 60;
        int px = this.width / 2 - 190;
        int py = 190;
        RenderUtils.drawRoundedRect(ctx, px, py, prevW, prevH, 4, 0xA0101020);
        RenderUtils.drawOutline(ctx, px, py, prevW, prevH, 1, UiPalette.BORDER);
        super.render(ctx, mouseX, mouseY, delta);
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float s = (float) this.client.getWindow().getScaleFactor();
        ctx.getMatrices().push();
        ctx.getMatrices().scale(1.0f / s, 1.0f / s, 1.0f);
        int cx = Math.round((px + prevW / 2.0f) * s);
        int cy = Math.round((py + prevH / 2.0f) * s);
        float cs2Scale = (float) client.getWindow().getWidth() / 640.0f;
        CrosshairRenderer.drawNative(ctx, cx, cy, cfg, cs2Scale);
        ctx.getMatrices().pop();
        int swatchColor = (cfg.crosshairAlpha << 24) | (cfg.crosshairRed << 16) |
                          (cfg.crosshairGreen << 8) | cfg.crosshairBlue;
        ctx.fill(px + prevW - 10, py + 2, px + prevW - 2, py + 10, 0xFF000000);
        ctx.fill(px + prevW - 9, py + 3, px + prevW - 3, py + 9, swatchColor);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
