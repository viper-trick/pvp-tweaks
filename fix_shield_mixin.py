import glob

content = """package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.util.ShieldSampleStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class ShieldRendererMixin {

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void pvptweaks$sampleShieldPreRender(
            float partialTick,
            PoseStack matrices,
            SubmitNodeCollector collector,
            PlayerRenderState playerRenderState,
            FirstPersonHandsAndItemsRenderState renderState,
            CallbackInfo ci
    ) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg.shieldSampleShield && PvpTweaksConfig.adjusterOpen) {
            if (renderState.offHandItem == null || renderState.offHandItem.isEmpty()) {
                renderState.offHandItem = ShieldSampleStack.INSTANCE;
            }
        }
    }

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void pvptweaks$shieldOffset(
            PlayerRenderState playerState,
            FirstPersonHandsAndItemsRenderState renderState,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack itemStack,
            float equipProgress,
            PoseStack matrices,
            SubmitNodeCollector collector,
            int light,
            CallbackInfo ci
    ) {
        if (matrices == null || itemStack == null || itemStack.isEmpty()) return;
        if (itemStack.getItem() != Items.SHIELD) return;

        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        float ox = cfg.shieldOffsetX / 100f;
        float oy = cfg.shieldOffsetY / 100f;
        float oz = cfg.shieldOffsetZ / 100f;
        if (ox != 0 || oy != 0 || oz != 0) {
            matrices.translate(ox, oy, oz);
        }
        if (cfg.shieldRotX != 0) {
            matrices.mulPose(new org.joml.Matrix4f().rotation(com.mojang.math.Axis.XP.rotationDegrees(cfg.shieldRotX)));
        }
        if (cfg.shieldRotY != 0) {
            matrices.mulPose(new org.joml.Matrix4f().rotation(com.mojang.math.Axis.YP.rotationDegrees(cfg.shieldRotY)));
        }
        if (cfg.shieldRotZ != 0) {
            matrices.mulPose(new org.joml.Matrix4f().rotation(com.mojang.math.Axis.ZP.rotationDegrees(cfg.shieldRotZ)));
        }
    }
}
"""

for path in glob.glob("src/build-26.3/**/ShieldRendererMixin.java", recursive=True):
    with open(path, "w") as f:
        f.write(content)
    print(f"Successfully updated {path}")

