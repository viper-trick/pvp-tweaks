package com.pvptweaks.mixin;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SoundManager.class)
public class SoundManagerGetRedirect {

    @Shadow @Final private Map<Identifier, Weighted> sounds;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$redirectGet(Identifier id, CallbackInfoReturnable<Weighted> cir) {
        Identifier target = com.pvptweaks.sound.SoundRedirects.get(id);
        if (target == null) return;
        Weighted replacement = this.sounds.get(target);
        if (replacement != null) {
            cir.setReturnValue(replacement);
        }
    }
}
