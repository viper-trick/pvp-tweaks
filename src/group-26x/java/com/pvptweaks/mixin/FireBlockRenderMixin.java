package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.FireBlockModels;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateModelSet.class)
public class FireBlockRenderMixin {

    @Inject(method = "get", at = @At("RETURN"), cancellable = true, require = 0)
    private void pvptweaks$scaleFireBlock(BlockState state, CallbackInfoReturnable<BlockStateModel> cir) {
        if (!(state.getBlock() instanceof BaseFireBlock)) return;
        float scale = PvpTweaksConfig.get().getFireEntityScale();
        BlockStateModel original = cir.getReturnValue();
        if (original == null) return;
        if (scale <= 0f) {
            cir.setReturnValue(FireBlockModels.emptyModel(original.particleMaterial()));
        } else if (scale < 1f) {
            cir.setReturnValue(FireBlockModels.scaledModel(original, scale));
        }
    }
}
