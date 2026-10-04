import glob

content = """package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "submitArmWithItem", at = @At("HEAD"))
    private void pvptweaks$scalePerItem(
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

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (cfg == null) return;
    }
}
"""

for path in glob.glob("src/build-26.3/**/HeldItemRendererMixin.java", recursive=True):
    with open(path, "w") as f:
        f.write(content)
    print(f"Successfully updated {path}")

