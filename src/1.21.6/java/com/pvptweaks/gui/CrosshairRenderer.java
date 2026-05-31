package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;

/** Stateless helper — draws the custom crosshair at (cx,cy). */
public final class CrosshairRenderer {

    private CrosshairRenderer() {}

    public static void draw(DrawContext ctx, int cx, int cy, PvpTweaksConfig cfg) {
        int color = argb(cfg.crosshairAlpha, cfg.crosshairRed, cfg.crosshairGreen, cfg.crosshairBlue);
        int outline = argb(cfg.crosshairAlpha, 0, 0, 0);

        int size  = Math.round(cfg.crosshairSize);
        int gap   = Math.round(cfg.crosshairGap);
        int thick = Math.round(cfg.crosshairThickness);
        int outT  = Math.round(cfg.crosshairOutlineThickness);

        switch (cfg.crosshairStyle) {
            case 1 -> { // Dot only
                if (cfg.crosshairOutline) fillRect(ctx, cx - thick - outT, cy - thick - outT, (thick + outT) * 2 + 1, (thick + outT) * 2 + 1, outline);
                fillRect(ctx, cx - thick, cy - thick, thick * 2 + 1, thick * 2 + 1, color);
            }
            case 2 -> { // T-shape (no top arm)
                if (cfg.crosshairOutline) {
                    // horizontal outline
                    fillRect(ctx, cx - size - gap - outT, cy - thick / 2 - outT, size - gap + outT, thick + outT * 2, outline);
                    fillRect(ctx, cx + gap,               cy - thick / 2 - outT, size - gap + outT, thick + outT * 2, outline);
                    // bottom arm outline
                    fillRect(ctx, cx - thick / 2 - outT, cy + gap, thick + outT * 2, size - gap + outT, outline);
                }
                fillRect(ctx, cx - size - gap, cy - thick / 2, size - gap, thick, color);
                fillRect(ctx, cx + gap + 1,    cy - thick / 2, size - gap, thick, color);
                fillRect(ctx, cx - thick / 2,  cy + gap + 1,  thick, size - gap, color);
            }
            case 3 -> { // X-cross (diagonal)
                for (int i = gap; i < size; i++) {
                    if (cfg.crosshairOutline) {
                        for (int oi = -outT; oi <= outT; oi++) {
                            for (int oj = -outT; oj <= outT; oj++) {
                                fillRect(ctx, cx + i + oi,  cy + i + oj,  thick, thick, outline);
                                fillRect(ctx, cx - i + oi,  cy + i + oj,  thick, thick, outline);
                                fillRect(ctx, cx + i + oi,  cy - i + oj,  thick, thick, outline);
                                fillRect(ctx, cx - i + oi,  cy - i + oj,  thick, thick, outline);
                            }
                        }
                    }
                    fillRect(ctx, cx + i, cy + i, thick, thick, color);
                    fillRect(ctx, cx - i, cy + i, thick, thick, color);
                    fillRect(ctx, cx + i, cy - i, thick, thick, color);
                    fillRect(ctx, cx - i, cy - i, thick, thick, color);
                }
            }
            default -> { // 0 = Classic cross
                if (cfg.crosshairOutline) {
                    // horizontal outline
                    fillRect(ctx, cx - size - gap - outT, cy - thick / 2 - outT, (size - gap) + outT, thick + outT * 2, outline);
                    fillRect(ctx, cx + gap,               cy - thick / 2 - outT, (size - gap) + outT, thick + outT * 2, outline);
                    // vertical outline
                    fillRect(ctx, cx - thick / 2 - outT, cy - size - gap - outT, thick + outT * 2, (size - gap) + outT, outline);
                    fillRect(ctx, cx - thick / 2 - outT, cy + gap,               thick + outT * 2, (size - gap) + outT, outline);
                }
                // horizontal arms
                fillRect(ctx, cx - size - gap, cy - thick / 2, size - gap, thick, color);
                fillRect(ctx, cx + gap + 1,    cy - thick / 2, size - gap, thick, color);
                // vertical arms
                fillRect(ctx, cx - thick / 2, cy - size - gap, thick, size - gap, color);
                fillRect(ctx, cx - thick / 2, cy + gap + 1,    thick, size - gap, color);
            }
        }

        // Centre dot
        if (cfg.crosshairDot) {
            if (cfg.crosshairOutline) fillRect(ctx, cx - 1 - outT, cy - 1 - outT, (1 + outT) * 2 + 1, (1 + outT) * 2 + 1, outline);
            fillRect(ctx, cx - 1, cy - 1, 3, 3, color);
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
