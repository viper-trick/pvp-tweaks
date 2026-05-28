package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.isxander.zoomify.Zoomify", remap = false)
public class ZoomifyMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$onTick(net.minecraft.client.MinecraftClient client, CallbackInfo ci) {
        if (!"zoomify".equals(PvpTweaksConfig.get().zoomManagementMode)) {
            try {
                Class<?> clazz = Class.forName("dev.isxander.zoomify.Zoomify");
                java.lang.reflect.Field f1 = clazz.getDeclaredField("zooming");
                f1.setAccessible(true);
                f1.setBoolean(null, false);

                java.lang.reflect.Field f2 = clazz.getDeclaredField("secondaryZooming");
                f2.setAccessible(true);
                f2.setBoolean(null, false);
            } catch (Exception ignored) {}
            ci.cancel();
        }
    }

    @Inject(method = "getZoomDivisor", at = @At("HEAD"), cancellable = true)
    private static void pvptweaks$onGetZoomDivisor(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (!"zoomify".equals(PvpTweaksConfig.get().zoomManagementMode)) {
            cir.setReturnValue(1.0f);
        }
    }
}
