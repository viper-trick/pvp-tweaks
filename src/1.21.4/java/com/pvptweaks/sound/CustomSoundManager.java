package com.pvptweaks.sound;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.mixin.SoundManagerAccessor;
import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages custom sound files and performs immediate injection into the sound engine.
 */
public class CustomSoundManager {

    private static final Map<String, Identifier> cache = new HashMap<>();

    public static Identifier registerCustomSound(String sourcePath) {
        Identifier id = cache.get(sourcePath);
        if (id == null) {
            try {
                Path src = Paths.get(sourcePath);
                if (!Files.exists(src)) {
                    PvpTweaksMod.LOGGER.error("[PVP Tweaks] file not found: {}", sourcePath);
                    return null;
                }

                String filename = src.getFileName().toString();
                String baseName = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
                String safeId = baseName.toLowerCase().replaceAll("[^a-z0-9_]", "_");

                Path dest = PvpTweaksConfig.SOUNDS_DIR.resolve(safeId + ".ogg");
                Files.createDirectories(PvpTweaksConfig.SOUNDS_DIR);

                // Convert/Process file
                boolean ok = Mp3Converter.convertToOgg(src, dest);
                if (!ok) {
                    PvpTweaksMod.LOGGER.error("[PVP Tweaks] Could not process/convert {}", filename);
                    return null;
                }

                // If the source was in our sounds directory, and its path is not the same as dest,
                // we should delete the original file to prevent duplicates in the list!
                if (!src.equals(dest) && src.getParent() != null && src.getParent().toAbsolutePath().equals(PvpTweaksConfig.SOUNDS_DIR.toAbsolutePath())) {
                    try {
                        Files.deleteIfExists(src);
                    } catch (IOException e) {
                        PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to delete original file after conversion: {}", e.getMessage());
                    }
                }

                id = Identifier.of("pvptweaks", "custom/" + safeId);
                cache.put(sourcePath, id);
                PvpTweaksMod.LOGGER.info("[PVP Tweaks] Registered {} -> {}", filename, id);
            } catch (IOException e) {
                PvpTweaksMod.LOGGER.error("[PVP Tweaks] registerCustomSound failed: {}", e.getMessage());
                return null;
            }
        }

        // ALWAYS ensure it is injected into the current manager
        injectIfMissing(id);

        return id;
    }

    public static void injectIfMissing(Identifier eventId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        
        SoundManager manager = mc.getSoundManager();
        if (manager.get(eventId) != null) return; // Already there

        if (!(manager instanceof SoundManagerAccessor accessor)) {
            PvpTweaksMod.LOGGER.warn("[PVP Tweaks] SoundManager is not accessor-compatible");
            return;
        }

        try {
            String safeId = eventId.getPath().substring(eventId.getPath().lastIndexOf('/') + 1);
            Path soundFile = PvpTweaksConfig.SOUNDS_DIR.resolve(safeId + ".ogg");
            if (!Files.exists(soundFile)) {
                PvpTweaksMod.LOGGER.error("[PVP Tweaks] Injection failed: sound file missing at {}", soundFile);
                return;
            }

            // 1. Create the Sound object
            Sound sound = new Sound(
                eventId,
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

            // 3. Inject into maps
            accessor.getSounds().put(eventId, soundSet);
            
            Identifier resourceId = sound.getLocation();
            InputSupplier<InputStream> supplier = () -> Files.newInputStream(soundFile);
            Resource resource = new Resource(PvpTweaksDynamicPack.INSTANCE, supplier);
            
            accessor.getSoundResources().put(resourceId, resource);
            
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Injected sound into active registry: {}", eventId);
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Injection failed for " + eventId, e);
        }
    }

    public static java.util.List<String> getAllGameSounds() {
        return net.minecraft.registry.Registries.SOUND_EVENT.getIds().stream()
            .map(id -> id.getNamespace() + ":" + id.getPath())
            .sorted().collect(java.util.stream.Collectors.toList());
    }
}
