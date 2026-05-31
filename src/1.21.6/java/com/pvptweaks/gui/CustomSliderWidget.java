package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class CustomSliderWidget extends SliderWidget {
    private final String prefix;
    private final double min;
    private final double max;
    private final Consumer<Double> setter;
    private final boolean isInt;
    public boolean forced = false;

    public CustomSliderWidget(int x, int y, int width, int height, String prefix, double value, double min, double max, boolean isInt, Consumer<Double> setter) {
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
        if (forced) {
            String valStr = isInt ? String.valueOf((int) val) : String.format("%.1f", val);
            this.setMessage(Text.literal("§7" + prefix + ": " + valStr + "% (Forced)"));
            return;
        }
        String valStr = formatValueWithColor(val);
        this.setMessage(Text.literal(prefix + ": " + valStr));
    }

    private String formatValueWithColor(double val) {
        String lowerPrefix = prefix.toLowerCase();
        boolean isCrosshair = lowerPrefix.contains("size") || lowerPrefix.contains("gap") || 
                              lowerPrefix.contains("thickness") || lowerPrefix.contains("outline") ||
                              lowerPrefix.contains("red") || lowerPrefix.contains("green") ||
                              lowerPrefix.contains("blue") || lowerPrefix.contains("alpha");
        
        if (isCrosshair) {
            String valStr = isInt ? String.valueOf((int) val) : String.format("%.1f", val);
            return "§e" + valStr;
        }

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
        if (max <= 5.0) {
            checkVal = val * 100.0;
        }

        if (val <= min + 0.001) {
            return "§c" + valStr + "§r";
        } else if (val >= max - 0.001) {
            return "§d" + valStr + "§r";
        } else if (checkVal <= 25.001) {
            return "§6" + valStr + "§r";
        } else if (checkVal <= 50.001) {
            return "§e" + valStr + "§r";
        } else if (checkVal <= 75.001) {
            return "§b" + valStr + "§r";
        } else if (checkVal <= 100.001) {
            return "§a" + valStr + "§r";
        } else {
            return "§d" + valStr + "§r";
        }
    }

    @Override
    protected void applyValue() {
        double val = min + (value * (max - min));
        setter.accept(val);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        net.minecraft.client.font.TextRenderer tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        
        if (forced) {
            RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 4, 0x30000000);
            RenderUtils.drawOutline(context, this.getX(), this.getY(), this.width, this.height, 1, 0x20FFFFFF);
            int fillWidth = (int) (this.value * this.width);
            if (fillWidth > 0) {
                RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), fillWidth, this.height, 4, 0x40808080);
            }
            int textX = this.getX() + (this.width - tr.getWidth(this.getMessage())) / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.drawTextWithShadow(tr, this.getMessage(), textX, textY, 0xFFAAAAAA);
            return;
        }

        // Background container
        RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 4, 0x50000000);
        RenderUtils.drawOutline(context, this.getX(), this.getY(), this.width, this.height, 1, 0x30FFFFFF);
        
        // Active fill
        int fillWidth = (int) (this.value * this.width);
        if (fillWidth > 0) {
            RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), fillWidth, this.height, 4, UiPalette.ACCENT_BLUE & 0x80FFFFFF);
        }
        
        // Draw outline when hovered
        if (this.isHovered()) {
            RenderUtils.drawOutline(context, this.getX(), this.getY(), this.width, this.height, 1, UiPalette.ACCENT_BLUE);
        }
        
        // Center text message
        int textX = this.getX() + (this.width - tr.getWidth(this.getMessage())) / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        context.drawTextWithShadow(tr, this.getMessage(), textX, textY, 0xFFFFFFFF);
    }
}
