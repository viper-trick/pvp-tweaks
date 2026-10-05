package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends net.minecraft.client.gui.screens.Screen {

    protected GameMenuScreenMixin() { super(Component.empty()); }

    @Inject(method = "init", at = @At("TAIL"))
    private void pvptweaks$addTopButton(CallbackInfo ci) {
        if (((PauseScreen)(Object)this).showsPauseMenu()) {
            // Place button above the "Game Menu" text (which is at y=40)
            this.addRenderableWidget(Button.builder(
                Component.literal("\u00a7d\u2694 PVP Tweaks"),
                b -> {
                    if (com.pvptweaks.config.PvpTweaksConfig.get().useLegacyMenu) {
                        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("cloth-config")) {
                            Minecraft.getInstance().setScreen(new com.pvptweaks.gui.ClothConfigRequiredScreen(this));
                        } else {
                            Minecraft.getInstance().setScreen(
                                com.pvptweaks.integration.ClothConfigScreenHelper.buildScreen(this)
                            );
                        }
                    } else {
                        Minecraft.getInstance().setScreen(
                            new com.pvptweaks.gui.PvpTweaksHubScreen(this)
                        );
                    }
                }
            ).bounds(this.width / 2 - 102, 12, 204, 20).build());
        }
    }
}
