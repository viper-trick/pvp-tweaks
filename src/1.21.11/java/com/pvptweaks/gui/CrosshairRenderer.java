package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;

/** Stateless helper — draws the custom crosshair at (cx,cy). */
public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    public static void draw(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        // Convert CS2 units to Minecraft GUI pixels (1:1, float for sub-pixel AA)
        float size  = Math.max(0.0f, cfg.crosshairSize);
        float gap   = cfg.crosshairGap;
        float thick = Math.max(0.1f, cfg.crosshairThickness);
        float outT  = Math.max(0.1f, cfg.crosshairOutlineThickness);

        float halfThick = thick * 0.5f;

        if (cfg.crosshairOutline) {
            switch (cfg.crosshairStyle) {
                case 1 -> fillRectF(ctx, cx - halfThick - outT, cy - halfThick - outT, thick + 2.0f * outT, thick + 2.0f * outT, outline);
                case 2 -> {
                    fillRectF(ctx, cx - gap - size - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx + gap - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, cy + gap - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                }
                case 3 -> {
                    float start = gap;
                    float end = gap + size;
                    float tOuter = thick + 2.0f * outT;
                    for (float i = start; i < end; i++) {
                        fillRectF(ctx, cx + i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx - i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx + i - outT, cy - i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx - i - outT, cy - i - outT, tOuter, tOuter, outline);
                    }
                }
                default -> {
                    fillRectF(ctx, cx - gap - size - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx + gap - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, cy - gap - size - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, cy + gap - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                }
            }
            if (cfg.crosshairDot) {
                float dotOuter = thick + 2.0f * outT;
                fillRectF(ctx, cx - halfThick - outT, cy - halfThick - outT, dotOuter, dotOuter, outline);
            }
        }

        // Draw main crosshair
        switch (cfg.crosshairStyle) {
            case 1 -> fillRectF(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
            case 2 -> {
                fillRectF(ctx, cx - gap - size, cy - halfThick, size, thick, color);
                fillRectF(ctx, cx + gap,        cy - halfThick, size, thick, color);
                fillRectF(ctx, cx - halfThick,  cy + gap,       thick, size, color);
            }
            case 3 -> {
                float start = gap;
                float end = gap + size;
                for (float i = start; i < end; i++) {
                    fillRectF(ctx, cx + i, cy + i, thick, thick, color);
                    fillRectF(ctx, cx - i, cy + i, thick, thick, color);
                    fillRectF(ctx, cx + i, cy - i, thick, thick, color);
                    fillRectF(ctx, cx - i, cy - i, thick, thick, color);
                }
            }
            default -> {
                fillRectF(ctx, cx - gap - size, cy - halfThick, size, thick, color);
                fillRectF(ctx, cx + gap,        cy - halfThick, size, thick, color);
                fillRectF(ctx, cx - halfThick,  cy - gap - size, thick, size, color);
                fillRectF(ctx, cx - halfThick,  cy + gap,        thick, size, color);
            }
        }

        if (cfg.crosshairDot) {
            fillRectF(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
        }
    }

    /** Draw a filled rectangle with sub-pixel anti-aliasing. */
    private static void fillRectF(DrawContext ctx, float x, float y, float w, float h, int color) {
        if (w <= 0.0f || h <= 0.0f) return;

        float x2 = x + w;
        float y2 = y + h;

        int ix1 = (int) Math.floor(x);
        int iy1 = (int) Math.floor(y);
        int ix2 = (int) Math.ceil(x2);
        int iy2 = (int) Math.ceil(y2);

        if (ix1 >= ix2 || iy1 >= iy2) return;

        // Fast path: integer-aligned rectangle
        if (x == (float) ix1 && x2 == (float) ix2 && y == (float) iy1 && y2 == (float) iy2) {
            ctx.fill(ix1, iy1, ix2, iy2, color);
            return;
        }

        // Full-coverage interior (if any)
        if (ix1 + 1 < ix2 && iy1 + 1 < iy2) {
            ctx.fill(ix1 + 1, iy1 + 1, ix2 - 1, iy2 - 1, color);
        }

        // Handle thin rects (<=2 px) per-pixel to avoid overlapping passes
        if (ix2 - ix1 <= 2 || iy2 - iy1 <= 2) {
            for (int row = iy1; row < iy2; row++) {
                float rowCov = Math.min(y2, row + 1) - Math.max(y, row);
                if (rowCov <= 0.0f) continue;
                rowCov = Math.min(1.0f, rowCov);
                for (int col = ix1; col < ix2; col++) {
                    float colCov = Math.min(x2, col + 1) - Math.max(x, col);
                    if (colCov <= 0.0f) continue;
                    ctx.fill(col, row, col + 1, row + 1,
                        mulAlpha(color, Math.min(1.0f, rowCov * colCov)));
                }
            }
            return;
        }

        // 9-part decomposition for normal-sized rects
        float leftCov  = 1.0f - (x - ix1);
        float rightCov = x2 - (ix2 - 1);
        float topCov   = 1.0f - (y - iy1);
        float botCov   = y2 - (iy2 - 1);

        // Left column (mid-section)
        if (leftCov < 0.999f && iy1 + 1 < iy2 - 1) {
            ctx.fill(ix1, iy1 + 1, ix1 + 1, iy2 - 1, mulAlpha(color, leftCov));
        }
        // Right column
        if (rightCov < 0.999f && iy1 + 1 < iy2 - 1) {
            ctx.fill(ix2 - 1, iy1 + 1, ix2, iy2 - 1, mulAlpha(color, rightCov));
        }
        // Top row
        if (topCov < 0.999f && ix1 + 1 < ix2 - 1) {
            ctx.fill(ix1 + 1, iy1, ix2 - 1, iy1 + 1, mulAlpha(color, topCov));
        }
        // Bottom row
        if (botCov < 0.999f && ix1 + 1 < ix2 - 1) {
            ctx.fill(ix1 + 1, iy2 - 1, ix2 - 1, iy2, mulAlpha(color, botCov));
        }
        // Four corner pixels
        ctx.fill(ix1, iy1, ix1 + 1, iy1 + 1, mulAlpha(color, Math.min(leftCov, topCov)));
        ctx.fill(ix2 - 1, iy1, ix2, iy1 + 1, mulAlpha(color, Math.min(rightCov, topCov)));
        ctx.fill(ix1, iy2 - 1, ix1 + 1, iy2, mulAlpha(color, Math.min(leftCov, botCov)));
        ctx.fill(ix2 - 1, iy2 - 1, ix2, iy2, mulAlpha(color, Math.min(rightCov, botCov)));
    }

    private static int mulAlpha(int color, float factor) {
        if (factor >= 0.999f) return color;
        if (factor <= 0.001f) return color & 0x00FFFFFF;
        int a = Math.round(((color >> 24) & 0xFF) * factor);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    private static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
