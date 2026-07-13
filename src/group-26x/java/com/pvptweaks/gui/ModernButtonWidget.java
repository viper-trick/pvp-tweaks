package com.pvptweaks.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ModernButtonWidget extends AbstractWidget {
    private float hoverProgress = 0f;
    private final Runnable onPress;

    public ModernButtonWidget(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (this.active && this.visible && click.x() >= this.getX() && click.x() <= this.getX() + this.width && click.y() >= this.getY() && click.y() <= this.getY() + this.height) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            if (onPress != null) onPress.run();
            return true;
        }
        return false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        boolean hovered = this.isFocused() || this.isHovered();
        hoverProgress = Mth.lerp(delta * 0.2f, hoverProgress, hovered ? 1.0f : 0.0f);

        int bgColor = RenderUtils.lerpColor(UiPalette.BUTTON_IDLE, UiPalette.BUTTON_HOVER, hoverProgress);
        int borderColor = RenderUtils.lerpColor(UiPalette.BORDER, UiPalette.ACCENT_BLUE, hoverProgress);

        RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 8, bgColor);
        RenderUtils.drawRoundedOutline(context, this.getX(), this.getY(), this.width, this.height, 8, 1, borderColor);

        int textColor = hovered ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY;
        int textWidth = Minecraft.getInstance().font.width(this.getMessage());
        context.text(Minecraft.getInstance().font, this.getMessage(),
                this.getX() + (this.width - textWidth) / 2, this.getY() + (this.height - 8) / 2, textColor);
    }

    @Override
    protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
