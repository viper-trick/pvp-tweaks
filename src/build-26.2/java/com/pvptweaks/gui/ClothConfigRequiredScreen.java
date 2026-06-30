package com.pvptweaks.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


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
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://modrinth.com/mod/cloth-config"));
            } catch (Exception ignored) {}
        }));
        
        addRenderableWidget(new ModernButtonWidget(width / 2 - w / 2, height / 2 + 35, w, 20, Component.literal("Go Back"), () -> {
            this.minecraft.setScreenAndShow(parent);
        }));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        
        context.text(font, Component.literal("\u00a7cCloth Config API is Missing!"), width / 2, height / 2 - 40, 0xFFFFFF);
        context.text(font, Component.literal("\u00a77The Legacy Menu requires Cloth Config API to function."), width / 2, height / 2 - 20, 0xFFFFFF);
    }
}
