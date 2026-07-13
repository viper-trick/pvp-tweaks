package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public class PumpkinBlurMixin {

    @Inject(
        method = "getViewBlockingState",
        at = @At("RETURN"),
        cancellable = true,
        require = 0
    )
    private static void pvptweaks$onGetViewBlockingState(Player player, CallbackInfoReturnable<BlockState> cir) {
        if (PvpTweaksConfig.get().disablePumpkinBlur) {
            BlockState state = cir.getReturnValue();
            if (state != null && state.is(Blocks.CARVED_PUMPKIN)) {
                cir.setReturnValue(null);
            }
        }
    }
}
