package com.pvptweaks.mixin;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SoundManager.class)
public class SoundManagerGetRedirect {

    @Shadow @Final private Map<Identifier, WeightedSoundSet> sounds;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$redirectGet(Identifier id, CallbackInfoReturnable<WeightedSoundSet> cir) {
        Identifier target = com.pvptweaks.sound.SoundRedirects.get(id);
        if (target == null) return;
        WeightedSoundSet replacement = this.sounds.get(target);
        if (replacement != null) {
            cir.setReturnValue(replacement);
        }
    }
}
