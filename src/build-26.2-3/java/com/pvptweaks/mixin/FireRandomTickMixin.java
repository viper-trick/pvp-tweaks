package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public class FireRandomTickMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$blockFireParticles(
            BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.fireEntityScalePct == 0) { ci.cancel(); return; }
        float density = cfg.getFireEntityScale();
        if (density <= 0.0f) { ci.cancel(); return; }
        if (density < 1.0f && random.nextFloat() > density) { ci.cancel(); }
    }
}
