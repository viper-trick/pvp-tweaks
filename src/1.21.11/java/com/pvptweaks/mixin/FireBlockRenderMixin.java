package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(BlockRenderManager.class)
public class FireBlockRenderMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$filterGroundFire(
            BlockState state, BlockPos pos, BlockRenderView world,
            MatrixStack matrices, VertexConsumer consumer,
            boolean cull, List<BlockModelPart> parts, CallbackInfo ci) {

        if (!(state.getBlock() instanceof AbstractFireBlock)) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.getFireEntityScale() <= 0.0f) ci.cancel();
    }
}
