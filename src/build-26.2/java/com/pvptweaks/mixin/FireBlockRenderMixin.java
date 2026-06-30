package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(ModelBlockRenderer.class)
public class FireBlockRenderMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$filterGroundFire(
            BlockState state, BlockPos pos, BlockGetter world,
            PoseStack matrices, VertexConsumer consumer,
            boolean cull, List<BakedQuad> parts, CallbackInfo ci) {

        if (!(state.getBlock() instanceof BaseFireBlock)) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.getFireEntityScale() <= 0.0f) ci.cancel();
    }
}
