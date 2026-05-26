package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import java.util.function.Consumer;

public class ProfileNameScreen extends Screen {
    private final Screen parent;
    private final String prefill;
    private final Consumer<String> onConfirm;
    private TextFieldWidget nameField;

    public ProfileNameScreen(Screen parent, String prefill, Consumer<String> onConfirm) {
        super(Text.literal("Save Profile"));
        this.parent = parent;
        this.prefill = prefill;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        nameField = new TextFieldWidget(textRenderer, midX - 100, this.height / 2 - 10, 200, 20, Text.empty());
        nameField.setPlaceholder(Text.literal("Profile name..."));
        if (prefill != null) nameField.setText(prefill);
        nameField.setMaxLength(32);
        addDrawableChild(nameField);
        setInitialFocus(nameField);

        addDrawableChild(new ModernButtonWidget(midX - 102, this.height / 2 + 16, 100, 20, (Text)Text.literal("Save"), () -> confirm()));
        addDrawableChild(new ModernButtonWidget(midX + 2, this.height / 2 + 16, 100, 20, (Text)Text.literal("Cancel"), () -> client.setScreen(parent)));
    }

    private void confirm() {
        String name = nameField.getText().trim();
        if (!name.isBlank()) onConfirm.accept(name);
        client.setScreen(parent);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER) { confirm(); return true; }
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) { client.setScreen(parent); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, 0xAA101010, 0xCC101010);
        RenderUtils.drawOutline(ctx, width / 2 - 120, height / 2 - 50, 240, 100, 1, UiPalette.BORDER);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lSAVE PROFILE"), this.width / 2, this.height / 2 - 40, UiPalette.ACCENT_BLUE);
        super.render(ctx, mx, my, delta);
    }
}
