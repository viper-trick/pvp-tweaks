package com.pvptweaks.gui;

import org.lwjgl.glfw.GLFW;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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

        addRenderableWidget(new ModernButtonWidget(midX - 102, this.height / 2 + 16, 100, 20, (Component)Component.literal("Save"), () -> confirm()));
        addRenderableWidget(new ModernButtonWidget(midX + 2, this.height / 2 + 16, 100, 20, (Component)Component.literal("Cancel"), () -> minecraft.setScreen(parent)));
    }

    private void confirm() {
        String name = nameField.getValue().trim();
        if (!name.isBlank()) onConfirm.accept(name);
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) { confirm(); return true; }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { minecraft.setScreen(parent); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, 0xAA101010, 0xCC101010);
        RenderUtils.drawOutline(ctx, width / 2 - 120, height / 2 - 50, 240, 100, 1, UiPalette.BORDER);
        ctx.drawCenteredString(font, Component.literal("\u00a7lSAVE PROFILE"), this.width / 2, this.height / 2 - 40, UiPalette.ACCENT_BLUE);
        super.render(ctx, mx, my, delta);
    }
}
