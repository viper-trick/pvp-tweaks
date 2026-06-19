package com.pvptweaks.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Shadow @Final private SimpleOption<Integer> fov;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void pvptweaks$widenFovRange(CallbackInfo ci) {
        try {
            java.lang.reflect.Field callbacksField = SimpleOption.class.getDeclaredField("callbacks");
            callbacksField.setAccessible(true);
            callbacksField.set(this.fov, new SimpleOption.ValidatingIntSliderCallbacks(1, 180));

            java.lang.reflect.Field codecField = SimpleOption.class.getDeclaredField("codec");
            codecField.setAccessible(true);
            codecField.set(this.fov, Codec.intRange(1, 180));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
