package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix3x2fStack;

public class CpsHudRenderer {
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.cpsEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        String text;
        if (cfg.cpsShowLabel) {
            text = "L: " + CpsTracker.getLeftCps() + "  R: " + CpsTracker.getRightCps();
        } else {
            text = CpsTracker.getLeftCps() + " | " + CpsTracker.getRightCps();
        }
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int x = (int) (width * (cfg.cpsX / 100.0f));
        int y = (int) (height * (cfg.cpsY / 100.0f));

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scale(cfg.cpsScale, cfg.cpsScale);
        
        float scaledX = x / cfg.cpsScale;
        float scaledY = y / cfg.cpsScale;

        int color = cfg.cpsRainbow ? getRainbowColor() : cfg.cpsColor;

        if (cfg.cpsShadow) {
            context.drawTextWithShadow(client.textRenderer, text, (int)scaledX, (int)scaledY, color);
        } else {
            context.drawText(client.textRenderer, text, (int)scaledX, (int)scaledY, color, false);
        }
        matrices.popMatrix();
    }

    public static int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 4000) / 4000f;
        return hsbToRgb(hue, 0.8f, 1.0f);
    }

    public static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
