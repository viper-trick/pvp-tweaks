package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final Consumer<Integer> onSave;
    private final int initialColor;
    private TextFieldWidget hexField;
    private String errorMsg = "";

    public ColorPickerScreen(Screen parent, int initialColor, Consumer<Integer> onSave) {
        super(Text.literal("Color Picker"));
        this.parent = parent;
        this.initialColor = initialColor;
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        
        // Colors & Opacity grids starting at height / 2 - 80
        int startX = cx - 125;
        int startY = height / 2 - 80;
        int buttonSize = 22;
        int gap = 3;
        
        // Add Color presets
        int[] presetColors = {
            0xFFFE3B30, // Red
            0xFF34C759, // Green
            0xFF007AFF, // Blue
            0xFFFFCC00, // Yellow
            0xFFAF52DE, // Purple
            0xFF5AC8FA, // Cyan
            0xFFFFA500, // Orange
            0xFFFF2D55, // Pink
            0xFFFFFFFF, // White
            0xFF1C1C1E  // Dark Grey
        };
        
        for (int i = 0; i < presetColors.length; i++) {
            final int colorVal = presetColors[i];
            int px = startX + (i % 5) * (buttonSize + gap);
            int py = startY + (i / 5) * (buttonSize + gap);
            
            addDrawableChild(new ModernButtonWidget(px, py, buttonSize, buttonSize, Text.literal(""), () -> {
                int currentAlpha = 0x80;
                try {
                    String hex = hexField.getText();
                    if (hex.length() == 8) {
                        currentAlpha = Integer.parseInt(hex.substring(0, 2), 16);
                    }
                } catch (Exception ignored) {}
                
                int newColor = (colorVal & 0x00FFFFFF) | (currentAlpha << 24);
                String newHex = Integer.toHexString(newColor).toUpperCase();
                while (newHex.length() < 8) newHex = "0" + newHex;
                hexField.setText(newHex);
            }) {
                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 2, colorVal);
                    if (this.isHovered()) {
                        RenderUtils.drawOutline(context, this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, 1, 0xFFFFFFFF);
                    }
                }
            });
        }
        
        // Add Opacity presets
        int opX = startX + 5 * (buttonSize + gap) + 15;
        int[] opVals = {0x40, 0x80, 0xC0, 0xFF};
        String[] opLabels = {"25%", "50%", "75%", "100%"};
        for (int i = 0; i < 4; i++) {
            final int alpha = opVals[i];
            int py = startY + i * 20;
            addDrawableChild(new ModernButtonWidget(opX, py, 35, 18, Text.literal(opLabels[i]), () -> {
                int currentColor = 0xFFFFFFFF;
                try {
                    String hex = hexField.getText();
                    if (hex.length() == 6) hex = "FF" + hex;
                    if (hex.length() == 8) currentColor = (int) Long.parseLong(hex, 16);
                } catch (Exception ignored) {}
                
                int newColor = (currentColor & 0x00FFFFFF) | (alpha << 24);
                String newHex = Integer.toHexString(newColor).toUpperCase();
                while (newHex.length() < 8) newHex = "0" + newHex;
                hexField.setText(newHex);
            }));
        }
        
        // hexField position
        int yField = startY + 55 + 20;
        hexField = new TextFieldWidget(textRenderer, cx - 50, yField, 100, 20, Text.literal("Hex Code"));
        String initHex = Integer.toHexString(initialColor).toUpperCase();
        while (initHex.length() < 8) initHex = "0" + initHex;
        hexField.setText(initHex);
        hexField.setMaxLength(8);
        addSelectableChild(hexField);
        
        // Cancel & Save Buttons
        int yButtons = yField + 30;
        addDrawableChild(new ModernButtonWidget(cx - 50, yButtons, 45, 20, Text.literal("Cancel"), () -> {
            client.setScreen(parent);
        }));

        addDrawableChild(new ModernButtonWidget(cx + 5, yButtons, 45, 20, Text.literal("\u00a7aSave"), () -> {
            try {
                String hex = hexField.getText();
                if (hex.length() == 6) hex = "FF" + hex;
                int color = (int) Long.parseLong(hex, 16);
                onSave.accept(color);
            } catch (Exception e) {
                errorMsg = "Invalid hex code!";
            }
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        int cx = width / 2;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lColor Picker"), cx, height / 2 - 100, 0xFFFFFF);
        
        // Preview box aligned with hex field
        int yField = height / 2 - 80 + 55 + 20;
        try {
            String hex = hexField.getText();
            if (hex.length() == 6) hex = "FF" + hex;
            int color = (int) Long.parseLong(hex, 16);
            RenderUtils.drawRoundedRect(context, cx + 60, yField, 20, 20, 2, color);
            RenderUtils.drawOutline(context, cx + 60, yField, 20, 20, 1, 0xFFFFFFFF);
            errorMsg = "";
        } catch (Exception ignored) {}

        if (!errorMsg.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7c" + errorMsg), cx, yField + 60, 0xFFFFFF);
        }

        hexField.render(context, mouseX, mouseY, delta);
    }
}
