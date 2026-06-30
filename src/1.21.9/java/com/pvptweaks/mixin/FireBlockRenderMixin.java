package com.pvptweaks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pvptweaks.config.PvpTweaksConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockRenderDispatcher.class)
public class FireBlockRenderMixin {

    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$filterGroundFire(
            BlockState state, BlockPos pos, BlockAndTintGetter world,
            PoseStack matrices, VertexConsumer consumer,
            boolean cull, List<BlockModelPart> parts, CallbackInfo ci) {

        if (!(state.getBlock() instanceof BaseFireBlock)) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.getFireEntityScale() <= 0.0f) ci.cancel();
    }
}
