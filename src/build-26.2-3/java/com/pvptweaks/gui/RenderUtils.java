package com.pvptweaks.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderUtils {

    public static void drawRoundedRect(GuiGraphicsExtractor ctx, int x, int y, int width, int height, int radius, int color) {
        if (radius <= 0) {
            ctx.fill(x, y, x + width, y + height, color);
            return;
        }
        radius = Math.min(radius, Math.min(width / 2, height / 2));

        ctx.fill(x + radius, y, x + width - radius, y + height, color);
        ctx.fill(x, y + radius, x + radius, y + height - radius, color);
        ctx.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

        for (int dy = 0; dy < radius; dy++) {
            int dx = (int) Math.sqrt(radius * radius - (radius - dy) * (radius - dy));

            int topY = y + dy;
            int botY = y + height - dy - 1;

            ctx.fill(x + radius - dx, topY, x + radius, topY + 1, color);
            ctx.fill(x + width - radius, topY, x + width - radius + dx, topY + 1, color);
            ctx.fill(x + radius - dx, botY, x + radius, botY + 1, color);
            ctx.fill(x + width - radius, botY, x + width - radius + dx, botY + 1, color);
        }
    }

    public static void drawRoundedFill(GuiGraphicsExtractor ctx, int x, int y, int totalWidth, int height, int radius, int fillWidth, int color) {
        if (fillWidth <= 0 || height <= 0) return;
        fillWidth = Math.min(fillWidth, totalWidth);
        radius = Math.min(radius, height / 2);
        if (radius <= 0) {
            ctx.fill(x, y, x + fillWidth, y + height, color);
            return;
        }
        for (int row = 0; row < height; row++) {
            int leftX = x;
            int rightX = x + fillWidth;
            if (row < radius) {
                int dx = (int) Math.sqrt(radius * radius - (radius - row) * (radius - row));
                leftX = x + radius - dx;
                rightX = Math.min(rightX, x + totalWidth - radius + dx);
            } else if (row >= height - radius) {
                int dyFromBottom = row - (height - radius);
                int dx = (int) Math.sqrt(radius * radius - (dyFromBottom + 1) * (dyFromBottom + 1));
                leftX = x + radius - dx;
                rightX = Math.min(rightX, x + totalWidth - radius + dx);
            } else {
                rightX = Math.min(rightX, x + totalWidth);
            }
            if (leftX < rightX) {
                ctx.fill(leftX, y + row, rightX, y + row + 1, color);
            }
        }
    }

    public static void drawGradientRect(GuiGraphicsExtractor context, int x, int y, int width, int height, int startColor, int endColor) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }
    
    public static void drawOutline(GuiGraphicsExtractor context, int x, int y, int width, int height, int thickness, int color) {
        context.fill(x, y, x + width, y + thickness, color);
        context.fill(x, y + height - thickness, x + width, y + height, color);
        context.fill(x, y, x + thickness, y + height, color);
        context.fill(x + width - thickness, y, x + width, y + height, color);
    }

    public static void drawRoundedOutline(GuiGraphicsExtractor ctx, int x, int y, int width, int height, int radius, int thickness, int color) {
        if (radius <= 0 || radius >= Math.min(width / 2, height / 2)) {
            drawOutline(ctx, x, y, width, height, thickness, color);
            return;
        }
        ctx.fill(x + radius, y, x + width - radius, y + thickness, color);
        ctx.fill(x + radius, y + height - thickness, x + width - radius, y + height, color);
        ctx.fill(x, y + radius, x + thickness, y + height - radius, color);
        ctx.fill(x + width - thickness, y + radius, x + width, y + height - radius, color);
        for (int dy = 0; dy < radius; dy++) {
            int dx = (int) Math.sqrt(radius * radius - (radius - dy) * (radius - dy));
            int topY = y + dy;
            int botY = y + height - dy - 1;
            ctx.fill(x + radius - dx, topY, x + radius - dx + thickness, topY + 1, color);
            ctx.fill(x + width - radius + dx - thickness, topY, x + width - radius + dx, topY + 1, color);
            ctx.fill(x + radius - dx, botY, x + radius - dx + thickness, botY + 1, color);
            ctx.fill(x + width - radius + dx - thickness, botY, x + width - radius + dx, botY + 1, color);
        }
    }

    public static int lerpColor(int color1, int color2, float delta) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * delta);
        int r = (int) (r1 + (r2 - r1) * delta);
        int g = (int) (g1 + (g2 - g1) * delta);
        int b = (int) (b1 + (b2 - b1) * delta);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
