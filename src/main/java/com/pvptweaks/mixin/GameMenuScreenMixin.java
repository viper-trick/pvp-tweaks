package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected GameMenuScreenMixin() { super(Text.empty()); }

    @Inject(method = "init", at = @At("TAIL"))
    private void pvptweaks$addTopButton(CallbackInfo ci) {
        if (((GameMenuScreen)(Object)this).shouldShowMenu()) {
            // Place button above the "Game Menu" text (which is at y=40)
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("\u00a7d\u2694 PVP Tweaks"),
                b -> MinecraftClient.getInstance().setScreen(
                    PvpTweaksConfigScreen.build(this)
                )
            ).dimensions(this.width / 2 - 102, 12, 204, 20).build());
        }
    }
}
