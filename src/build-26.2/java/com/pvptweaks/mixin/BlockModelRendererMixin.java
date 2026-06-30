package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ported from GRM (your own code — no license concerns).
 *
 * Targets BlockBehaviour.BlockStateBase.getRenderType() —
 * called by EVERY renderer (vanilla, Sodium, Iris, etc.) before any geometry is built.
 * Returning INVISIBLE means the block is skipped entirely at the chunk-build stage.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockModelRendererMixin {

    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$hideHiddenPlants(CallbackInfoReturnable<RenderShape> cir) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.plantsControlEnabled) return;
        if (cfg.hiddenPlants == null || cfg.hiddenPlants.isEmpty()) return;

        BlockState state = (BlockState)(Object) this;
        String blockId = Integer.toString(BuiltInRegistries.BLOCK.getId(state.getBlock()));

        if (cfg.hiddenPlants.contains(blockId)) {
            cir.setReturnValue(RenderShape.INVISIBLE);
        }
    }
}
