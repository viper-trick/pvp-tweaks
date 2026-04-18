package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class InteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void pvptweaks$onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (PvpTweaksConfig.get().crystalOptimizer && target instanceof EndCrystalEntity) {
            target.discard();
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"))
    private void pvptweaks$onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (PvpTweaksConfig.get().anchorOptimizer) {
            BlockPos pos = hitResult.getBlockPos();
            World world = MinecraftClient.getInstance().world;
            if (world != null && world.getBlockState(pos).isOf(Blocks.RESPAWN_ANCHOR)) {
                if (!player.getStackInHand(hand).isOf(net.minecraft.item.Items.GLOWSTONE)) {
                    // Check if we are in Nether or End (where anchors explode)
                    if (world.getRegistryKey() == World.NETHER || world.getRegistryKey() == World.END) {
                        // Replace with a 'fake' block that doesn't block placement (like a Fern)
                        world.setBlockState(pos, Blocks.FERN.getDefaultState());
                    }
                }
            }
        }
    }
}
