package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Stateless helper — draws the custom crosshair at (cx,cy) in virtual pixel space. */
public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    /**
     * In-game HUD: converts CS2 pixel values → virtual pixels, clamps to ≥1 virtual pixel,
     * renders at integer positions with full opacity (no AA).
     *
     * @param cx  virtual-pixel center x (screen centre)
     * @param cy  virtual-pixel center y
     */
    public static void drawNative(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        float scale = MinecraftClient.getInstance().getWindow().getScaleFactor();

        // Convert native pixel values → virtual pixels, clamp minimum dimensions
        float sizeV  = Math.max(1.0f, cfg.crosshairSize / scale);
        float gapV   = cfg.crosshairGap / scale;
        float splitV = Math.max(0.0f, cfg.crosshairSplitDistance / scale);
        float thickV = Math.max(1.0f, cfg.crosshairThickness / scale);
        float outTV  = Math.max(1.0f, cfg.crosshairOutlineThickness / scale);

        float halfThick = thickV * 0.5f;

        if (cfg.crosshairOutline) {
            switch (cfg.crosshairStyle) {
                case 1 -> fillRectSnapped(ctx, cx - halfThick - outTV, cy - halfThick - outTV, thickV + 2.0f * outTV, thickV + 2.0f * outTV, outline);
                case 2 -> {
                    fillRectSnapped(ctx, cx - gapV - splitV - sizeV - outTV, cy - halfThick - outTV, sizeV + 2.0f * outTV, thickV + 2.0f * outTV, outline);
                    fillRectSnapped(ctx, cx + gapV + splitV - outTV, cy - halfThick - outTV, sizeV + 2.0f * outTV, thickV + 2.0f * outTV, outline);
                    fillRectSnapped(ctx, cx - halfThick - outTV, cy + gapV + splitV - outTV, thickV + 2.0f * outTV, sizeV + 2.0f * outTV, outline);
                }
                case 3 -> {
                    float start = gapV + splitV;
                    float end = gapV + splitV + sizeV;
                    float tOuter = thickV + 2.0f * outTV;
                    for (float i = start; i < end; i++) {
                        fillRectSnapped(ctx, cx + i - outTV, cy + i - outTV, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx - i - outTV, cy + i - outTV, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx + i - outTV, cy - i - outTV, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx - i - outTV, cy - i - outTV, tOuter, tOuter, outline);
                    }
                }
                default -> {
                    fillRectSnapped(ctx, cx - gapV - splitV - sizeV - outTV, cy - halfThick - outTV, sizeV + 2.0f * outTV, thickV + 2.0f * outTV, outline);
                    fillRectSnapped(ctx, cx + gapV + splitV - outTV, cy - halfThick - outTV, sizeV + 2.0f * outTV, thickV + 2.0f * outTV, outline);
                    fillRectSnapped(ctx, cx - halfThick - outTV, cy - gapV - splitV - sizeV - outTV, thickV + 2.0f * outTV, sizeV + 2.0f * outTV, outline);
                    fillRectSnapped(ctx, cx - halfThick - outTV, cy + gapV + splitV - outTV, thickV + 2.0f * outTV, sizeV + 2.0f * outTV, outline);
                }
            }
            if (cfg.crosshairDot) {
                float dotOuter = thickV + 2.0f * outTV;
                fillRectSnapped(ctx, cx - halfThick - outTV, cy - halfThick - outTV, dotOuter, dotOuter, outline);
            }
        }

        switch (cfg.crosshairStyle) {
            case 1 -> fillRectSnapped(ctx, cx - halfThick, cy - halfThick, thickV, thickV, color);
            case 2 -> {
                fillRectSnapped(ctx, cx - gapV - splitV - sizeV, cy - halfThick, sizeV, thickV, color);
                fillRectSnapped(ctx, cx + gapV + splitV,        cy - halfThick, sizeV, thickV, color);
                fillRectSnapped(ctx, cx - halfThick,            cy + gapV + splitV, thickV, sizeV, color);
            }
            case 3 -> {
                float start = gapV + splitV;
                float end = gapV + splitV + sizeV;
                for (float i = start; i < end; i++) {
                    fillRectSnapped(ctx, cx + i, cy + i, thickV, thickV, color);
                    fillRectSnapped(ctx, cx - i, cy + i, thickV, thickV, color);
                    fillRectSnapped(ctx, cx + i, cy - i, thickV, thickV, color);
                    fillRectSnapped(ctx, cx - i, cy - i, thickV, thickV, color);
                }
            }
            default -> {
                fillRectSnapped(ctx, cx - gapV - splitV - sizeV, cy - halfThick, sizeV, thickV, color);
                fillRectSnapped(ctx, cx + gapV + splitV,        cy - halfThick, sizeV, thickV, color);
                fillRectSnapped(ctx, cx - halfThick,            cy - gapV - splitV - sizeV, thickV, sizeV, color);
                fillRectSnapped(ctx, cx - halfThick,            cy + gapV + splitV, thickV, sizeV, color);
            }
        }

        if (cfg.crosshairDot) {
            fillRectSnapped(ctx, cx - halfThick, cy - halfThick, thickV, thickV, color);
        }
    }

    /** Adjuster screen preview — same as drawNative: scaled to virtual pixels, integer snapping, full opacity. */
    public static void draw(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        drawNative(ctx, cx, cy, cfg);
    }

    /** Round to integer pixel, full alpha. Guarantees at least 1 pixel in each dimension. */
    private static void fillRectSnapped(DrawContext ctx, float x, float y, float w, float h, int color) {
        if (w <= 0.0f || h <= 0.0f) return;
        int ix1 = Math.round(x);
        int iy1 = Math.round(y);
        int ix2 = Math.round(x + w);
        int iy2 = Math.round(y + h);
        if (ix1 >= ix2) ix2 = ix1 + 1;
        if (iy1 >= iy2) iy2 = iy1 + 1;
        ctx.fill(ix1, iy1, ix2, iy2, color);
    }

    private static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
