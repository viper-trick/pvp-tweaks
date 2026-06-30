package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables the carved-pumpkin overlay ("pumpkin blur") when the option is enabled.
 *
 * In 1.21.11, the pumpkin blur is rendered via Gui.renderOverlay.
 * We inject at the head of this method and cancel it if the texture being
 * rendered is the pumpkin blur texture and the config option is enabled.
 */
@Mixin(Gui.class)
public class PumpkinBlurMixin {

    @Inject(
        method = "renderOverlay",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void pvptweaks$onRenderOverlay(GuiGraphicsExtractor context, Identifier texture, float opacity, CallbackInfo ci) {
        if (PvpTweaksConfig.get().disablePumpkinBlur && texture != null) {
            String path = texture.getPath();
            // Typically "textures/misc/pumpkinblur.png"
            if (path.contains("pumpkinblur")) {
                ci.cancel();
            }
        }
    }
}
