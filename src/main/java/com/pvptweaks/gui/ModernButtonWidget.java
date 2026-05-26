package com.pvptweaks.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ModernButtonWidget extends ClickableWidget {
    private float hoverProgress = 0f;
    private final Runnable onPress;

    public ModernButtonWidget(int x, int y, int width, int height, Text message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.active && this.visible && click.x() >= this.getX() && click.x() <= this.getX() + this.width && click.y() >= this.getY() && click.y() <= this.getY() + this.height) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            if (onPress != null) onPress.run();
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        boolean hovered = this.isSelected() || this.isHovered();
        hoverProgress = MathHelper.lerp(delta * 0.2f, hoverProgress, hovered ? 1.0f : 0.0f);

        int bgColor = RenderUtils.lerpColor(UiPalette.BUTTON_IDLE, UiPalette.BUTTON_HOVER, hoverProgress);
        int borderColor = RenderUtils.lerpColor(UiPalette.BORDER, UiPalette.ACCENT_BLUE, hoverProgress);

        RenderUtils.drawRoundedRect(context, this.getX(), this.getY(), this.width, this.height, 4, bgColor);
        RenderUtils.drawOutline(context, this.getX(), this.getY(), this.width, this.height, 1, borderColor);

        int textColor = hovered ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY;
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), 
                this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, textColor);
    }

    @Override
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
