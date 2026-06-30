package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class InteractionManagerMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void pvptweaks$onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        if (PvpTweaksConfig.get().crystalOptimizer && target instanceof EndCrystal) {
            target.discard();
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void pvptweaks$onInteractBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (PvpTweaksConfig.get().anchorOptimizer) {
            BlockPos pos = hitResult.getBlockPos();
            Level world = Minecraft.getInstance().level;
            if (world != null && world.getBlockState(pos).is(Blocks.RESPAWN_ANCHOR)) {
                if (!player.getItemInHand(hand).is(net.minecraft.world.item.Items.GLOWSTONE)) {
                    // Check if we are in Nether or End (where anchors explode)
                    if (world.dimension() == Level.NETHER || world.dimension() == Level.END) {
                        // Replace with a 'fake' block that doesn't block placement (like a Fern)
                        world.setBlockAndUpdate(pos, Blocks.FERN.defaultBlockState());
                    }
                }
            }
        }
    }
}
