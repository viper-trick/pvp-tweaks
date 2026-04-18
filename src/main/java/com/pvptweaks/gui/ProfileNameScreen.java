package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.function.Consumer;

/** Minimal screen for entering a profile name before saving. */
public class ProfileNameScreen extends Screen {

    private final Screen parent;
    private final String prefill;
    private final Consumer<String> onConfirm;
    private TextFieldWidget nameField;

    public ProfileNameScreen(Screen parent, String prefill, Consumer<String> onConfirm) {
        super(Text.literal("Save Profile"));
        this.parent    = parent;
        this.prefill   = prefill;
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

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"),
            b -> confirm()).dimensions(midX - 52, this.height / 2 + 16, 50, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"),
            b -> client.setScreen(parent)).dimensions(midX + 2, this.height / 2 + 16, 50, 20).build());
    }

    private void confirm() {
        String name = nameField.getText().trim();
        if (!name.isBlank()) {
            onConfirm.accept(name);
        }
        client.setScreen(parent);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) { confirm(); return true; }
        if (input.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) { client.setScreen(parent); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("\u00a76Enter profile name:"), this.width / 2, this.height / 2 - 28, 0xFFFFFFFF);
    }
}
