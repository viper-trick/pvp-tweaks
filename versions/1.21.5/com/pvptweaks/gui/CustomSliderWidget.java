package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class CustomSliderWidget extends SliderWidget {
    private final String prefix;
    private final double min;
    private final double max;
    private final Consumer<Double> setter;
    private final boolean isInt;

    public CustomSliderWidget(int x, int y, int width, int height, String prefix, double value, double min, double max, boolean isInt, Consumer<Double> setter) {
        // We set height to a fixed value for the bar, but the overall clickable area is larger
        super(x, y, width, height, Text.literal(""), (value - min) / (max - min));
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.setter = setter;
        this.isInt = isInt;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        double val = min + (value * (max - min));
        String valStr = formatValueWithColor(val);
        this.setMessage(Text.literal(prefix + ": " + valStr));
    }

    private String formatValueWithColor(double val) {
        String valStr;

        if (val <= min + 0.001) {
            if (min == 0) {
                String pLower = prefix.toLowerCase();
                if (pLower.contains("vol")) {
                    valStr = "OFF";
                } else if (pLower.contains("part")) {
                    valStr = "none";
                } else {
                    valStr = "OFF";
                }
            } else {
                valStr = isInt ? String.valueOf((int) val) : String.format("%.1f", val);
            }
        } else if (val >= max - 0.001) {
            valStr = "MAX";
        } else {
            if (isInt) {
                valStr = String.valueOf((int) val);
            } else {
                valStr = String.format("%.1f", val);
            }
        }

        double checkVal = val;
        // Map speed/scale/gamma (typically 0.1 to 5.0) to a percentage-like scale
        if (max <= 5.0) {
            checkVal = val * 100.0;
        }

        if (val <= min + 0.001) {
            return "\u00a7c" + valStr + "\u00a7r"; // Red for OFF/min
        } else if (val >= max - 0.001) {
            return "\u00a7d" + valStr + "\u00a7r"; // Purple for MAX
        } else if (checkVal <= 25.001) {
            return "\u00a76" + valStr + "\u00a7r"; // Orange
        } else if (checkVal <= 50.001) {
            return "\u00a7e" + valStr + "\u00a7r"; // Yellow
        } else if (checkVal <= 75.001) {
            return "\u00a7b" + valStr + "\u00a7r"; // Light Blue/Aqua
        } else if (checkVal <= 100.001) {
            return "\u00a7a" + valStr + "\u00a7r"; // Green
        } else {
            return "\u00a7d" + valStr + "\u00a7r"; // Purple
        }
    }

    @Override
    protected void applyValue() {
        double val = min + (value * (max - min));
        setter.accept(val);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw label ABOVE the slider
        context.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, this.getMessage(), 
                this.getX(), this.getY() - 10, UiPalette.TEXT_SECONDARY);

        // Draw slider bar
        int barY = this.getY() + this.height / 2;
        RenderUtils.drawRoundedRect(context, this.getX(), barY - 2, this.width, 4, 2, 0x40FFFFFF);
        int fillWidth = (int) (this.value * this.width);
        RenderUtils.drawRoundedRect(context, this.getX(), barY - 2, fillWidth, 4, 2, UiPalette.ACCENT_BLUE);
        
        // Draw knob
        int knobX = this.getX() + fillWidth - 4;
        RenderUtils.drawRoundedRect(context, knobX, barY - 6, 8, 12, 4, UiPalette.TEXT_PRIMARY);
        if (this.isHovered()) {
            RenderUtils.drawOutline(context, knobX - 1, barY - 7, 10, 14, 1, UiPalette.ACCENT_BLUE);
        }
    }
}
