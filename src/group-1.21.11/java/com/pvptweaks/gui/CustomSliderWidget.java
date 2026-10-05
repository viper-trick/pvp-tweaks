package com.pvptweaks.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class CustomSliderWidget extends AbstractSliderButton {
    private final String prefix;
    private final double min;
    private final double max;
    public double vanillaMin = Double.NaN;
    public double vanillaMax = Double.NaN;
    private final Consumer<Double> setter;
    private final boolean isInt;
    public boolean forced = false;

    public CustomSliderWidget(int x, int y, int width, int height, String prefix, double value, double min, double max, boolean isInt, Consumer<Double> setter) {
        super(x, y, width, height, Component.literal(""), (value - min) / (max - min));
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
            this.setMessage(Component.literal("§7" + prefix + ": " + valStr + "% (Forced)"));
            return;
        }
        String valStr = formatValueWithColor(val);
        this.setMessage(Component.literal(prefix + ": " + valStr));
    }

    private String formatValueWithColor(double val) {
        // If vanilla range is set and value is outside it, show red
        if (!Double.isNaN(vanillaMin) && val < vanillaMin) {
            String vs = isInt ? String.valueOf((int) val) : String.format("%.1f", val);
            return "§c" + vs + "§r";
        }
        if (!Double.isNaN(vanillaMax) && val > vanillaMax) {
            String vs = isInt ? String.valueOf((int) val) : String.format("%.1f", val);
            return "§c" + vs + "§r";
        }

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
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        net.minecraft.client.gui.Font tr = net.minecraft.client.Minecraft.getInstance().font;

        if (!this.active) {
            RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, 0x30000000);
            RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, 0x20FFFFFF);
            int textX = this.getX() + (this.width - tr.width(this.getMessage())) / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.drawString(tr, this.getMessage(), textX, textY, 0xFF888888);
            return;
        }

        if (forced) {
            RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, 0x30000000);
            RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, 0x20FFFFFF);
            int fillWidth = (int) (this.value * this.width);
            if (fillWidth > 0) {
                RenderUtils.drawRoundedFill(context, this.getX(), this.getY(), this.width, this.height, 8, fillWidth, 0x40808080);
            }
            int textX = this.getX() + (this.width - tr.width(this.getMessage())) / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.drawString(tr, this.getMessage(), textX, textY, 0xFFAAAAAA);
            return;
        }

        RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, 0x50000000);
        RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, 0x30FFFFFF);
        
        int fillWidth = (int) (this.value * this.width);
        if (fillWidth > 0) {
            RenderUtils.drawRoundedFill(context, this.getX(), this.getY(), this.width, this.height, 8, fillWidth, UiPalette.ACCENT_BLUE & 0x80FFFFFF);
        }
        
        // Draw outline when hovered
        if (this.isHovered()) {
            RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, UiPalette.ACCENT_BLUE);
        }
        
        // Center text message
        int textX = this.getX() + (this.width - tr.width(this.getMessage())) / 2;
        int textY = this.getY() + (this.height - 8) / 2;
        context.drawString(tr, this.getMessage(), textX, textY, 0xFFFFFFFF);
    }
}
