package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;

public class RenderUtils {

    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Simple rounded rect using 3 fills
        // Center
        context.fill(x + radius, y, x + width - radius, y + height, color);
        // Left
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        // Right
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        
        // Corners (simplified for now to avoid complex GL code)
        context.fill(x, y, x + radius, y + radius, color);
        context.fill(x + width - radius, y, x + width, y + radius, color);
        context.fill(x, y + height - radius, x + radius, y + height, color);
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color);
    }

    public static void drawGradientRect(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }
    
    public static void drawOutline(DrawContext context, int x, int y, int width, int height, int thickness, int color) {
        context.fill(x, y, x + width, y + thickness, color); // Top
        context.fill(x, y + height - thickness, x + width, y + height, color); // Bottom
        context.fill(x, y, x + thickness, y + height, color); // Left
        context.fill(x + width - thickness, y, x + width, y + height, color); // Right
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
