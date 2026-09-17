package com.pvptweaks.mixin;

import com.pvptweaks.gui.CpsHudRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class CpsHudMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void pvptweaks$cpsHud(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        CpsHudRenderer.extractRenderState(context, tickCounter);
    }
}
