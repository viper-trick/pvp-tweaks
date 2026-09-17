package com.pvptweaks.mixin;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.profiling.ProfilerFiller;
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

    @Shadow @Final private Map<Identifier, WeighedSoundEvents> registry;
    @Shadow @Final private Map<Identifier, Resource> soundCache;

    @Inject(method = "apply", at = @At("RETURN"))
    private void pvptweaks$injectCustomSounds(
            @Coerce Object preparations,
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfo ci) {
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

                        Identifier eventId = Identifier.fromNamespaceAndPath("pvptweaks", "custom/" + safeId);

                        // 1. Create the Sound object
                        Sound sound = new Sound(
                            Identifier.fromNamespaceAndPath("pvptweaks", "custom/" + safeId),
                            ConstantFloat.of(1.0f),
                            ConstantFloat.of(1.0f),
                            1,
                            Sound.Type.FILE,
                            false,
                            false,
                            16
                        );

                        // 2. Create WeighedSoundEvents
                        WeighedSoundEvents soundSet = new WeighedSoundEvents(eventId, null);
                        soundSet.addSound(sound);

                        // 3. Inject into SoundManager
                        this.registry.put(eventId, soundSet);

                        // 4. Inject the resource file into soundCache
                        Identifier resourceId = sound.getLocation();
                        Resource resource = new Resource(
                            PvpTweaksDynamicPack.INSTANCE != null
                                ? PvpTweaksDynamicPack.INSTANCE
                                : new PvpTweaksDynamicPack(),
                            () -> Files.newInputStream(p)
                        );

                        this.soundCache.put(resourceId, resource);

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
