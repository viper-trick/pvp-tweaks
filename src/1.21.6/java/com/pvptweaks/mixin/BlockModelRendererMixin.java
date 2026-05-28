package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ported from GRM (your own code — no license concerns).
 *
 * Targets AbstractBlock.AbstractBlockState.getRenderType() —
 * called by EVERY renderer (vanilla, Sodium, Iris, etc.) before any geometry is built.
 * Returning INVISIBLE means the block is skipped entirely at the chunk-build stage.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public class BlockModelRendererMixin {

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void pvptweaks$hideHiddenPlants(CallbackInfoReturnable<BlockRenderType> cir) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.plantsControlEnabled) return;
        if (cfg.hiddenPlants == null || cfg.hiddenPlants.isEmpty()) return;

        BlockState state = (BlockState)(Object) this;
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (cfg.hiddenPlants.contains(blockId)) {
            cir.setReturnValue(BlockRenderType.INVISIBLE);
        }
    }
}
