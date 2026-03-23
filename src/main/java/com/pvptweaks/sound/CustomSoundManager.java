package com.pvptweaks.sound;

import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages custom sound files.
 * Copies user-provided audio files into the pvptweaks sounds directory
 * and maps them to pvptweaks: Identifiers.
 *
 * Supported formats: .ogg (native), .mp3/.wav/.flac (copy as-is, user must
 * ensure .ogg format – Minecraft only plays .ogg natively).
 */
public class CustomSoundManager {

    private static final Map<String, Identifier> cache = new HashMap<>();

    public static Identifier registerCustomSound(String sourcePath) {
        if (cache.containsKey(sourcePath)) return cache.get(sourcePath);

        try {
            Path src  = Paths.get(sourcePath);
            if (!Files.exists(src)) return null;

            String filename = src.getFileName().toString();
            // Normalize to .ogg extension for the ID
            String baseName = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
            String safeId   = baseName.toLowerCase().replaceAll("[^a-z0-9_]", "_");

            Path dest = PvpTweaksConfig.SOUNDS_DIR.resolve(safeId + ".ogg");
            Files.createDirectories(PvpTweaksConfig.SOUNDS_DIR);
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

            Identifier id = Identifier.of("pvptweaks", "custom/" + safeId);
            cache.put(sourcePath, id);

            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Registered custom sound: {} -> {}", sourcePath, id);
            return id;

        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Failed to register custom sound: {}", e.getMessage());
            return null;
        }
    }

    /** Returns all sound events registered in the game registry as strings. */
    public static java.util.List<String> getAllGameSounds() {
        return net.minecraft.registry.Registries.SOUND_EVENT.getIds().stream()
            .map(id -> id.getNamespace() + ":" + id.getPath())
            .sorted()
            .collect(java.util.stream.Collectors.toList());
    }
}
