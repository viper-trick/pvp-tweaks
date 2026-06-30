package com.pvptweaks.mixin;

import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;

// TODO 26.x: SoundManager no longer has loadStatic/loadStreamed methods.
// The entire sound loading system was overhauled. WAV support needs to be
// reimplemented using the new 26.x sound API.
@Mixin(SoundManager.class)
public class SoundLoaderMixin {
}
