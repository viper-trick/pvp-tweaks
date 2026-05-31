package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;

/** Stateless helper — draws the custom crosshair at (cx,cy). */
public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    public static void draw(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        int size  = Math.max(1, Math.round(cfg.crosshairSize));
        int gap   = Math.max(0, Math.round(cfg.crosshairGap));
        int thick = Math.max(1, Math.round(cfg.crosshairThickness));
        int outT  = Math.max(1, Math.round(cfg.crosshairOutlineThickness));

        int halfThick = thick / 2;

        if (cfg.crosshairOutline) {
            switch (cfg.crosshairStyle) {
                case 1 -> { // Dot only outline
                    fillRect(ctx, cx - halfThick - outT, cy - halfThick - outT, thick + 2 * outT, thick + 2 * outT, outline);
                }
                case 2 -> { // T-shape outline (no top arm)
                    fillRect(ctx, cx - gap - size - outT, cy - halfThick - outT, size + 2 * outT, thick + 2 * outT, outline);
                    fillRect(ctx, cx + gap + 1 - outT, cy - halfThick - outT, size + 2 * outT, thick + 2 * outT, outline);
                    fillRect(ctx, cx - halfThick - outT, cy + gap + 1 - outT, thick + 2 * outT, size + 2 * outT, outline);
                }
                case 3 -> { // X-cross outline
                    int start = gap;
                    int end = gap + size;
                    for (int i = start; i < end; i++) {
                        fillRect(ctx, cx + i - outT, cy + i - outT, thick + 2 * outT, thick + 2 * outT, outline);
                        fillRect(ctx, cx - i - outT, cy + i - outT, thick + 2 * outT, thick + 2 * outT, outline);
                        fillRect(ctx, cx + i - outT, cy - i - outT, thick + 2 * outT, thick + 2 * outT, outline);
                        fillRect(ctx, cx - i - outT, cy - i - outT, thick + 2 * outT, thick + 2 * outT, outline);
                    }
                }
                default -> { // Classic cross outline
                    fillRect(ctx, cx - gap - size - outT, cy - halfThick - outT, size + 2 * outT, thick + 2 * outT, outline);
                    fillRect(ctx, cx + gap + 1 - outT, cy - halfThick - outT, size + 2 * outT, thick + 2 * outT, outline);
                    fillRect(ctx, cx - halfThick - outT, cy - gap - size - outT, thick + 2 * outT, size + 2 * outT, outline);
                    fillRect(ctx, cx - halfThick - outT, cy + gap + 1 - outT, thick + 2 * outT, size + 2 * outT, outline);
                }
            }
            if (cfg.crosshairDot) {
                fillRect(ctx, cx - halfThick - outT, cy - halfThick - outT, thick + 2 * outT, thick + 2 * outT, outline);
            }
        }

        // Draw main crosshair
        switch (cfg.crosshairStyle) {
            case 1 -> { // Dot only
                fillRect(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
            }
            case 2 -> { // T-shape
                fillRect(ctx, cx - gap - size, cy - halfThick, size, thick, color);
                fillRect(ctx, cx + gap + 1,    cy - halfThick, size, thick, color);
                fillRect(ctx, cx - halfThick,  cy + gap + 1,  thick, size, color);
            }
            case 3 -> { // X-cross
                int start = gap;
                int end = gap + size;
                for (int i = start; i < end; i++) {
                    fillRect(ctx, cx + i, cy + i, thick, thick, color);
                    fillRect(ctx, cx - i, cy + i, thick, thick, color);
                    fillRect(ctx, cx + i, cy - i, thick, thick, color);
                    fillRect(ctx, cx - i, cy - i, thick, thick, color);
                }
            }
            default -> { // Classic cross
                fillRect(ctx, cx - gap - size, cy - halfThick, size, thick, color);
                fillRect(ctx, cx + gap + 1,    cy - halfThick, size, thick, color);
                fillRect(ctx, cx - halfThick,  cy - gap - size, thick, size, color);
                fillRect(ctx, cx - halfThick,  cy + gap + 1,    thick, size, color);
            }
        }

        if (cfg.crosshairDot) {
            fillRect(ctx, cx - halfThick, cy - halfThick, thick, thick, color);
        }
    }

    private static void fillRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        ctx.fill(x, y, x + w, y + h, color);
    }

    private static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
