package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;

/**
 * Stateless helper — draws the custom crosshair at (cx,cy) in native (framebuffer) pixels.
 * Callers must push a scale(1/scaleFactor) transform on the DrawContext matrix stack
 * so that native pixel coordinates are mapped to the correct virtual GUI positions.
 */
public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    /** Draw at native pixel coordinates. Matrix stack must have scale(1/scaleFactor) applied. */
    public static void drawNative(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        float size  = Math.max(0.0f, cfg.crosshairSize);
        float gap   = cfg.crosshairGap;
        float split = Math.max(0.0f, cfg.crosshairSplitDistance);
        float thick = Math.max(0.1f, cfg.crosshairThickness);
        float outT  = Math.max(0.1f, cfg.crosshairOutlineThickness);

        float halfThick = thick * 0.5f;

        if (cfg.crosshairOutline) {
            switch (cfg.crosshairStyle) {
                case 1 -> fillRectSnapped(ctx, cx - halfThick - outT, cy - halfThick - outT, thick + 2.0f * outT, thick + 2.0f * outT, outline);
                case 2 -> {
                    fillRectSnapped(ctx, cx - gap - split - size - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectSnapped(ctx, cx + gap + split - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectSnapped(ctx, cx - halfThick - outT, cy + gap + split - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                }
                case 3 -> {
                    float start = gap + split;
                    float end = gap + split + size;
                    float tOuter = thick + 2.0f * outT;
                    for (float i = start; i < end; i++) {
                        fillRectSnapped(ctx, cx + i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx - i - outT, cy + i - outT, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx + i - outT, cy - i - outT, tOuter, tOuter, outline);
                        fillRectSnapped(ctx, cx - i - outT, cy - i - outT, tOuter, tOuter, outline);
                    }
                }
                default -> {
                    fillRectSnapped(ctx, cx - gap - split - size - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectSnapped(ctx, cx + gap + split - outT, cy - halfThick - outT, size + 2.0f * outT, thick + 2.0f * outT, outline);
                    fillRectSnapped(ctx, cx - halfThick - outT, cy - gap - split - size - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                    fillRectSnapped(ctx, cx - halfThick - outT, cy + gap + split - outT, thick + 2.0f * outT, size + 2.0f * outT, outline);
                }
            }
            if (cfg.crosshairDot) {
                float dotOuter = thick + 2.0f * outT;
                fillRectSnapped(ctx, cx - halfThick - outT, cy - halfThick - outT, dotOuter, dotOuter, outline);
            }
        }

        switch (cfg.crosshairStyle) {
            case 1 -> fillRectSnapped(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
            case 2 -> {
                fillRectSnapped(ctx, cx - gap - split - size, cy - halfThick, size, thick, color);
                fillRectSnapped(ctx, cx + gap + split,        cy - halfThick, size, thick, color);
                fillRectSnapped(ctx, cx - halfThick,          cy + gap + split, thick, size, color);
            }
            case 3 -> {
                float start = gap + split;
                float end = gap + split + size;
                for (float i = start; i < end; i++) {
                    fillRectSnapped(ctx, cx + i, cy + i, thick, thick, color);
                    fillRectSnapped(ctx, cx - i, cy + i, thick, thick, color);
                    fillRectSnapped(ctx, cx + i, cy - i, thick, thick, color);
                    fillRectSnapped(ctx, cx - i, cy - i, thick, thick, color);
                }
            }
            default -> {
                fillRectSnapped(ctx, cx - gap - split - size, cy - halfThick, size, thick, color);
                fillRectSnapped(ctx, cx + gap + split,        cy - halfThick, size, thick, color);
                fillRectSnapped(ctx, cx - halfThick,          cy - gap - split - size, thick, size, color);
                fillRectSnapped(ctx, cx - halfThick,          cy + gap + split, thick, size, color);
            }
        }

        if (cfg.crosshairDot) {
            fillRectSnapped(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
        }
    }

    /** Preview use — same as drawNative; caller must set up matrix stack. */
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
