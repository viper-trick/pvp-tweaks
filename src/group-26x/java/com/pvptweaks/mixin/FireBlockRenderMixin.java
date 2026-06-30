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

    @Inject(method = "tesselateBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvptweaks$filterGroundFire(
            net.minecraft.client.renderer.block.BlockQuadOutput output,
            float x, float y, float z,
            net.minecraft.client.renderer.block.BlockAndTintGetter world,
            BlockPos pos, BlockState state,
            net.minecraft.client.renderer.block.dispatch.BlockStateModel model,
            long seed, CallbackInfo ci) {

        if (!(state.getBlock() instanceof BaseFireBlock)) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if ("none".equals(cfg.firePreset) || cfg.getFireEntityScale() <= 0.0f) ci.cancel();
    }
}
