package com.pvptweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("registry")
    Map<Identifier, WeighedSoundEvents> getSounds();

    @Accessor("soundCache")
    Map<Identifier, Resource> getSoundResources();
}
