package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public class FireRandomTickMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$blockFireParticles(
            BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.fireEntityScalePct == 0) { ci.cancel(); return; }
        float density = cfg.getFireEntityScale();
        if (density <= 0.0f) { ci.cancel(); return; }
        if (density < 1.0f && random.nextFloat() > density) { ci.cancel(); }
    }
}
