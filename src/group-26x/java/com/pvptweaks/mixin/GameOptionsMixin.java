package com.pvptweaks.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class GameOptionsMixin {
    @Shadow @Final private OptionInstance<Integer> fov;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void pvptweaks$widenFovRange(CallbackInfo ci) {
        try {
            java.lang.reflect.Field valuesField = OptionInstance.class.getDeclaredField("values");
            valuesField.setAccessible(true);
            valuesField.set(this.fov, new OptionInstance.IntRange(1, 180));

            java.lang.reflect.Field codecField = OptionInstance.class.getDeclaredField("codec");
            codecField.setAccessible(true);
            codecField.set(this.fov, new OptionInstance.IntRange(1, 180).codec());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
