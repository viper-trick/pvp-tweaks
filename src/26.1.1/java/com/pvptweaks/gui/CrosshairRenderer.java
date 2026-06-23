package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;

public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    public static void drawNative(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg, float pixelScale) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        float size  = Math.max(0.0f, cfg.crosshairSize) * pixelScale;
        float gap   = cfg.crosshairGap * pixelScale;
        float split = Math.max(0.0f, cfg.crosshairSplitDistance) * pixelScale;
        float thick = Math.max(0.5f, cfg.crosshairThickness * pixelScale);
        float outT  = Math.max(0.0f, cfg.crosshairOutlineThickness);

        float gapExt = Math.max(-gap, 0.0f);
        float halfThick = thick * 0.5f;

        float rightX  = cx + gap + split;
        float rightW  = size + gapExt;
        float leftX   = cx - gap - split - size - gapExt;
        float leftW   = size + gapExt;
        float downY   = cy + gap + split;
        float downH   = size + gapExt;
        float upY     = cy - gap - split - size - gapExt;
        float upH     = size + gapExt;

        if (cfg.crosshairOutline) {
            switch (cfg.crosshairStyle) {
                case 1 -> fillRectF(ctx, cx - halfThick - outT, cy - halfThick - outT, thick + 2.0f * outT, thick + 2.0f * outT, outline);
                case 2 -> {
                    fillRectF(ctx, leftX - outT, cy - halfThick - outT, leftW + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, rightX - outT, cy - halfThick - outT, rightW + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, downY - outT, thick + 2.0f * outT, downH + 2.0f * outT, outline);
                }
                case 3 -> {
                    float start = gap + split;
                    float end = gap + split + size + gapExt;
                    float tOuter = thick + 2.0f * outT;
                    for (float i = start; i < end; i++) {
                        fillRectF(ctx, cx + i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx - i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx + i - outT, cy - i - outT, tOuter, tOuter, outline);
                        fillRectF(ctx, cx - i - outT, cy - i - outT, tOuter, tOuter, outline);
                    }
                }
                default -> {
                    fillRectF(ctx, leftX - outT, cy - halfThick - outT, leftW + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, rightX - outT, cy - halfThick - outT, rightW + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, upY - outT, thick + 2.0f * outT, upH + 2.0f * outT, outline);
                    fillRectF(ctx, cx - halfThick - outT, downY - outT, thick + 2.0f * outT, downH + 2.0f * outT, outline);
                }
            }
            if (cfg.crosshairDot) {
                float dotOuter = thick + 2.0f * outT;
                fillRectF(ctx, cx - halfThick - outT, cy - halfThick - outT, dotOuter, dotOuter, outline);
            }
        }

        switch (cfg.crosshairStyle) {
            case 1 -> fillRectF(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
            case 2 -> {
                fillRectF(ctx, leftX, cy - halfThick, leftW, thick, color);
                fillRectF(ctx, rightX, cy - halfThick, rightW, thick, color);
                fillRectF(ctx, cx - halfThick, downY, thick, downH, color);
            }
            case 3 -> {
                float start = gap + split;
                float end = gap + split + size + gapExt;
                for (float i = start; i < end; i++) {
                    fillRectF(ctx, cx + i, cy + i, thick, thick, color);
                    fillRectF(ctx, cx - i, cy + i, thick, thick, color);
                    fillRectF(ctx, cx + i, cy - i, thick, thick, color);
                    fillRectF(ctx, cx - i, cy - i, thick, thick, color);
                }
            }
            default -> {
                fillRectF(ctx, leftX, cy - halfThick, leftW, thick, color);
                fillRectF(ctx, rightX, cy - halfThick, rightW, thick, color);
                fillRectF(ctx, cx - halfThick, upY, thick, upH, color);
                fillRectF(ctx, cx - halfThick, downY, thick, downH, color);
            }
        }

        if (cfg.crosshairDot) {
            fillRectF(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
        }
    }

    private static void fillRectF(DrawContext ctx, float x, float y, float w, float h, int color) {
        if (w <= 0.0f || h <= 0.0f) return;

        float x2 = x + w;
        float y2 = y + h;

        int ix1 = (int) Math.floor(x);
        int iy1 = (int) Math.floor(y);
        int ix2 = (int) Math.ceil(x2);
        int iy2 = (int) Math.ceil(y2);

        if (ix1 >= ix2 || iy1 >= iy2) return;

        if (x == (float) ix1 && x2 == (float) ix2 && y == (float) iy1 && y2 == (float) iy2) {
            ctx.fill(ix1, iy1, ix2, iy2, color);
            return;
        }

        float leftCov  = 1.0f - (x - ix1);
        float rightCov = x2 - (ix2 - 1);
        float topCov   = 1.0f - (y - iy1);
        float botCov   = y2 - (iy2 - 1);

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

        int innerIx1 = ix1 + (leftCov < 0.999f ? 1 : 0);
        int innerIy1 = iy1 + (topCov < 0.999f ? 1 : 0);
        int innerIx2 = ix2 - (rightCov < 0.999f ? 1 : 0);
        int innerIy2 = iy2 - (botCov < 0.999f ? 1 : 0);

        if (innerIx1 < innerIx2 && innerIy1 < innerIy2) {
            ctx.fill(innerIx1, innerIy1, innerIx2, innerIy2, color);
        }

        if (leftCov < 0.999f && innerIy1 < innerIy2) {
            ctx.fill(ix1, innerIy1, ix1 + 1, innerIy2, mulAlpha(color, leftCov));
        }
        if (rightCov < 0.999f && innerIy1 < innerIy2) {
            ctx.fill(ix2 - 1, innerIy1, ix2, innerIy2, mulAlpha(color, rightCov));
        }
        if (topCov < 0.999f && innerIx1 < innerIx2) {
            ctx.fill(innerIx1, iy1, innerIx2, iy1 + 1, mulAlpha(color, topCov));
        }
        if (botCov < 0.999f && innerIx1 < innerIx2) {
            ctx.fill(innerIx1, iy2 - 1, innerIx2, iy2, mulAlpha(color, botCov));
        }
        if (leftCov < 0.999f && topCov < 0.999f)
            ctx.fill(ix1, iy1, ix1 + 1, iy1 + 1, mulAlpha(color, Math.min(leftCov, topCov)));
        if (rightCov < 0.999f && topCov < 0.999f)
            ctx.fill(ix2 - 1, iy1, ix2, iy1 + 1, mulAlpha(color, Math.min(rightCov, topCov)));
        if (leftCov < 0.999f && botCov < 0.999f)
            ctx.fill(ix1, iy2 - 1, ix1 + 1, iy2, mulAlpha(color, Math.min(leftCov, botCov)));
        if (rightCov < 0.999f && botCov < 0.999f)
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
