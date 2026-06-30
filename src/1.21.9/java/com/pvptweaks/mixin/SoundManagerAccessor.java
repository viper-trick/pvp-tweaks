package com.pvptweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("registry")
    Map<ResourceLocation, WeighedSoundEvents> getSounds();

    @Accessor("soundCache")
    Map<ResourceLocation, Resource> getSoundResources();
}
