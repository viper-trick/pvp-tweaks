package com.pvptweaks.mixin;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.client.sound.*;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {

    @Shadow @Final private Map<Identifier, WeightedSoundSet> sounds;
    @Shadow @Final private Map<Identifier, Resource> soundResources;

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;reloadSounds()V"))
    private void pvptweaks$injectCustomSounds(@Coerce Object soundList, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Injecting custom sounds directly into SoundManager registry...");
        
        Path dir = PvpTweaksConfig.SOUNDS_DIR;
        if (!Files.exists(dir)) return;

        try {
            Files.list(dir)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".ogg"))
                .forEach(p -> {
                    try {
                        String fn = p.getFileName().toString().toLowerCase();
                        String base = fn.substring(0, fn.length() - 4);
                        String safeId = base.replaceAll("[^a-z0-9_]", "_");
                        
                        Identifier eventId = Identifier.of("pvptweaks", "custom/" + safeId);
                        
                        // 1. Create the Sound object
                        Sound sound = new Sound(
                            Identifier.of("pvptweaks", "custom/" + safeId),
                            ConstantFloatProvider.create(1.0f),
                            ConstantFloatProvider.create(1.0f),
                            1,
                            Sound.RegistrationType.FILE,
                            false, // stream
                            false, // preload
                            16    // attenuation
                        );
                        
                        // 2. Create WeightedSoundSet
                        WeightedSoundSet soundSet = new WeightedSoundSet(eventId, null);
                        soundSet.add(sound);
                        
                        // 3. Inject into SoundManager
                        this.sounds.put(eventId, soundSet);
                        
                        // 4. Inject the resource file directly
                        Identifier resourceId = sound.getLocation();
                        InputSupplier<InputStream> supplier = () -> Files.newInputStream(p);
                        Resource resource = new Resource(PvpTweaksDynamicPack.INSTANCE, supplier);
                        
                        this.soundResources.put(resourceId, resource);
                        
                        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Successfully injected sound: {} -> {}", eventId, resourceId);
                    } catch (Exception e) {
                        PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to inject custom sound from path: " + p, e);
                    }
                });
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to list custom sounds directory", e);
        }
    }
}
