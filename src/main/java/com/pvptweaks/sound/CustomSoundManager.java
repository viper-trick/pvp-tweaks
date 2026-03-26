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
 *
 * Supported input formats (requires ffmpeg on PATH):
 *   .ogg  — native Minecraft format, copied as-is
 *   .mp3  — converted to mono OGG via ffmpeg
 *   .wav  — converted to mono OGG via ffmpeg
 *   .flac — converted to mono OGG via ffmpeg
 *   .aac / .m4a — converted to mono OGG via ffmpeg
 *
 * Result: a .ogg file in SOUNDS_DIR, served by PvpTweaksDynamicPack.
 */
public class CustomSoundManager {

    private static final Map<String, Identifier> cache = new HashMap<>();

    public static Identifier registerCustomSound(String sourcePath) {
        if (cache.containsKey(sourcePath)) return cache.get(sourcePath);

        try {
            Path src = Paths.get(sourcePath);
            if (!Files.exists(src)) {
                PvpTweaksMod.LOGGER.error("[PVP Tweaks] file not found: {}", sourcePath);
                return null;
            }

            String filename = src.getFileName().toString();
            // Safe lowercase ID: strip extension, replace non-alphanumeric with '_'
            String baseName = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf("."))
                : filename;
            String safeId = baseName.toLowerCase().replaceAll("[^a-z0-9_]", "_");

            Path dest = PvpTweaksConfig.SOUNDS_DIR.resolve(safeId + ".ogg");
            Files.createDirectories(PvpTweaksConfig.SOUNDS_DIR);

            if (Mp3Converter.needsConversion(filename)) {
                // Convert MP3/WAV/FLAC/etc. → mono OGG via ffmpeg
                boolean ok = Mp3Converter.convertToOgg(src, dest);
                if (!ok) {
                    PvpTweaksMod.LOGGER.error(
                        "[PVP Tweaks] Could not convert {} to OGG. "
                        + "Install ffmpeg:  sudo apt install ffmpeg", filename);
                    return null;
                }
            } else {
                // Already .ogg — copy directly
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            }

            Identifier id = Identifier.of("pvptweaks", "custom/" + safeId);
            cache.put(sourcePath, id);
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] registered {} -> {}", filename, id);

            // Trigger resource reload so SoundManager picks up the new sounds.json entry
            net.minecraft.client.MinecraftClient mc =
                net.minecraft.client.MinecraftClient.getInstance();
            if (mc != null) mc.execute(mc::reloadResources);

            return id;
        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] registerCustomSound failed: {}", e.getMessage());
            return null;
        }
    }

    public static java.util.List<String> getAllGameSounds() {
        return net.minecraft.registry.Registries.SOUND_EVENT.getIds().stream()
            .map(id -> id.getNamespace() + ":" + id.getPath())
            .sorted().collect(java.util.stream.Collectors.toList());
    }
}
