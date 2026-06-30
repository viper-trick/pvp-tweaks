package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ported from GRM (your own code — no license concerns).
 *
 * Cancels the selection outline for blocks listed in cfg.outlinePlants.
 * Handler intentionally takes only CallbackInfo — Mixin allows this
 * even if the target method has many params.
 */
@Mixin(LevelRenderer.class)
public class BlockOutlineMixin {

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$hideOutline(CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.plantsControlEnabled) return;
        if (cfg.outlinePlants == null || cfg.outlinePlants.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.hitResult == null) return;
        if (!(mc.hitResult instanceof BlockHitResult bhr)) return;

        BlockState state = mc.level.getBlockState(bhr.getBlockPos());
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        if (cfg.outlinePlants.contains(id)) {
            ci.cancel();
        }
    }
}
