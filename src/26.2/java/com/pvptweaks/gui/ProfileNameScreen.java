package com.pvptweaks.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import java.util.function.Consumer;

public class ProfileNameScreen extends Screen {
    private final Screen parent;
    private final String prefill;
    private final Consumer<String> onConfirm;
    private EditBox nameField;

    public ProfileNameScreen(Screen parent, String prefill, Consumer<String> onConfirm) {
        super(Component.literal("Save Profile"));
        this.parent = parent;
        this.prefill = prefill;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        nameField = new EditBox(font, midX - 100, this.height / 2 - 10, 200, 20, Component.empty());
        nameField.setHint(Component.literal("Profile name..."));
        if (prefill != null) nameField.setValue(prefill);
        nameField.setMaxLength(32);
        addRenderableWidget(nameField);
        setInitialFocus(nameField);

        addRenderableWidget(new ModernButtonWidget(midX - 102, this.height / 2 + 16, 100, 20, Component.literal("Save"), () -> confirm()));
        addRenderableWidget(new ModernButtonWidget(midX + 2, this.height / 2 + 16, 100, 20, Component.literal("Cancel"), () -> minecraft.setScreenAndShow(parent)));
    }

    private void confirm() {
        String name = nameField.getValue().trim();
        if (!name.isBlank()) onConfirm.accept(name);
        minecraft.setScreenAndShow(parent);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER) { confirm(); return true; }
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) { minecraft.setScreenAndShow(parent); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, 0xAA101010, 0xCC101010);
        RenderUtils.drawOutline(ctx, width / 2 - 120, height / 2 - 50, 240, 100, 1, UiPalette.BORDER);
        ctx.centeredText(font, Component.literal("\u00a7lSAVE PROFILE"), this.width / 2, this.height / 2 - 40, UiPalette.ACCENT_BLUE);
        super.extractRenderState(ctx, mx, my, delta);
    }
}
