package com.pvptweaks.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class ClothConfigRequiredScreen extends Screen {
    private final Screen parent;

    public ClothConfigRequiredScreen(Screen parent) {
        super(Text.literal("Cloth Config Required"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 200;
        addDrawableChild(new ModernButtonWidget(width / 2 - w / 2, height / 2 + 10, w, 20, Text.literal("\u00a7bDownload Cloth Config API"), () -> {
            Util.getOperatingSystem().open("https://modrinth.com/mod/cloth-config");
        }));
        
        addDrawableChild(new ModernButtonWidget(width / 2 - w / 2, height / 2 + 35, w, 20, Text.literal("Go Back"), () -> {
            client.setScreen(parent);
        }));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7cCloth Config API is Missing!"), width / 2, height / 2 - 40, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a77The Legacy Menu requires Cloth Config API to function."), width / 2, height / 2 - 20, 0xFFFFFF);
    }
}
