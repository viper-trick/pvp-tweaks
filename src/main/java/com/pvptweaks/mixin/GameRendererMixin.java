package com.pvptweaks.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

// Disabled – held item scaling moved to HeldItemRendererMixin via method_3233
@Mixin(GameRenderer.class)
public class GameRendererMixin {
}
