package com.pvptweaks.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class ClothConfigRequiredScreen extends Screen {
    private final Screen parent;

    public ClothConfigRequiredScreen(Screen parent) {
        super(Component.literal("Cloth Config Required"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 200;
        addRenderableWidget(new ModernButtonWidget(width / 2 - w / 2, height / 2 + 10, w, 20, Component.literal("\u00a7bDownload Cloth Config API"), () -> {
            Util.getPlatform().openUri("https://modrinth.com/mod/cloth-config");
        }));
        
        addRenderableWidget(new ModernButtonWidget(width / 2 - w / 2, height / 2 + 35, w, 20, Component.literal("Go Back"), () -> {
            minecraft.setScreen(parent);
        }));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredString(font, Component.literal("\u00a7cCloth Config API is Missing!"), width / 2, height / 2 - 40, 0xFFFFFF);
        context.drawCenteredString(font, Component.literal("\u00a77The Legacy Menu requires Cloth Config API to function."), width / 2, height / 2 - 20, 0xFFFFFF);
    }
}
