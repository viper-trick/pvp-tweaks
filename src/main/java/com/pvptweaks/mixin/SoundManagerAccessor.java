package com.pvptweaks.mixin;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("sounds")
    Map<Identifier, WeightedSoundSet> getSounds();

    @Accessor("soundResources")
    Map<Identifier, Resource> getSoundResources();
}
