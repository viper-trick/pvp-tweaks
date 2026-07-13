package com.pvptweaks.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ModernButtonWidget extends AbstractWidget {
    private float hoverProgress = 0f;
    private final Runnable onPress;
    private Runnable onDisabledClick = null;

    public ModernButtonWidget(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    public void setOnDisabledClick(Runnable r) { this.onDisabledClick = r; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.visible && mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            if (this.active) {
                if (onPress != null) onPress.run();
            } else if (onDisabledClick != null) {
                onDisabledClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        if (!this.active) {
            RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, 0x30000000);
            RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, 0x20FFFFFF);
            int textWidth = Minecraft.getInstance().font.width(this.getMessage());
            context.drawString(Minecraft.getInstance().font, this.getMessage(),
                this.getX() + (this.width - textWidth) / 2, this.getY() + (this.height - 8) / 2, 0xFF888888);
            return;
        }

        boolean hovered = this.isHoveredOrFocused() || this.isHovered();
        hoverProgress = Mth.lerp(delta * 0.2f, hoverProgress, hovered ? 1.0f : 0.0f);

        int bgColor = RenderUtils.lerpColor(UiPalette.BUTTON_IDLE, UiPalette.BUTTON_HOVER, hoverProgress);
        int borderColor = RenderUtils.lerpColor(UiPalette.BORDER, UiPalette.ACCENT_BLUE, hoverProgress);

        RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, bgColor);
        RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, borderColor);

        int textColor = hovered ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY;
        context.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 
                this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, textColor);
    }

    @Override
    protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
