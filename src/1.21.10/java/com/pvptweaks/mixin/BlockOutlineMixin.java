package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
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
@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$hideOutline(CallbackInfo ci) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.plantsControlEnabled) return;
        if (cfg.outlinePlants == null || cfg.outlinePlants.isEmpty()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null || mc.crosshairTarget == null) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult bhr)) return;

        BlockState state = mc.world.getBlockState(bhr.getBlockPos());
        String id = Registries.BLOCK.getId(state.getBlock()).toString();
        if (cfg.outlinePlants.contains(id)) {
            ci.cancel();
        }
    }
}
