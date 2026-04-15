package com.pvptweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pvptweaks.PvpTweaksMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages named profiles — snapshots of the full PvpTweaksConfig.
 * Each profile is stored as a separate JSON file in:
 *   config/pvptweaks/profiles/<name>.json
 */
public class PvpTweaksProfiles {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path PROFILES_DIR = FabricLoader.getInstance()
            .getConfigDir().resolve("pvptweaks/profiles");

    /** Save the current config as a named profile. */
    public static boolean save(String name) {
        if (name == null || name.isBlank()) return false;
        String safe = safeName(name);
        try {
            Files.createDirectories(PROFILES_DIR);
            Path file = PROFILES_DIR.resolve(safe + ".json");
            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(PvpTweaksConfig.get(), w);
            }
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Profile saved: {}", safe);
            return true;
        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Profile save failed: {}", e.getMessage());
            return false;
        }
    }

    /** Load a profile by name, replacing the current config. */
    public static boolean load(String name) {
        if (name == null || name.isBlank()) return false;
        Path file = PROFILES_DIR.resolve(safeName(name) + ".json");
        if (!Files.exists(file)) return false;
        try (Reader r = Files.newBufferedReader(file)) {
            PvpTweaksConfig loaded = GSON.fromJson(r, PvpTweaksConfig.class);
            if (loaded == null) return false;
            // Replace singleton instance by copying fields via JSON round-trip
            String json = GSON.toJson(loaded);
            PvpTweaksConfig updated = GSON.fromJson(json, PvpTweaksConfig.class);
            // Overwrite current config fields by serializing and reloading
            java.nio.file.Path configPath = FabricLoader.getInstance()
                    .getConfigDir().resolve("pvptweaks.json");
            try (Writer w = Files.newBufferedWriter(configPath)) {
                GSON.toJson(updated, w);
            }
            PvpTweaksConfig.load();
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Profile loaded: {}", name);
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Profile load failed: {}", e.getMessage());
            return false;
        }
    }

    /** Delete a profile by name. */
    public static boolean delete(String name) {
        Path file = PROFILES_DIR.resolve(safeName(name) + ".json");
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }

    /** List all saved profile names (without extension). */
    public static List<String> list() {
        List<String> names = new ArrayList<>();
        if (!Files.exists(PROFILES_DIR)) return names;
        try {
            Files.list(PROFILES_DIR)
                .filter(p -> p.getFileName().toString().endsWith(".json"))
                .forEach(p -> {
                    String fn = p.getFileName().toString();
                    names.add(fn.substring(0, fn.length() - 5)); // strip .json
                });
        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Profile list error: {}", e.getMessage());
        }
        names.sort(String::compareTo);
        return names;
    }

    /** Export current config as a JSON string (for clipboard/share). */
    public static String exportJson() {
        // Create a copy to strip sensitive/local sound settings before exporting
        PvpTweaksConfig current = PvpTweaksConfig.get();
        PvpTweaksConfig copy = GSON.fromJson(GSON.toJson(current), PvpTweaksConfig.class);

        // Strip sounds to keep export focused on visuals/scales
        copy.soundShieldBreak = new SoundProfile();
        copy.soundTotem       = new SoundProfile();
        copy.soundCrystal     = new SoundProfile();
        copy.soundExplosion   = new SoundProfile();
        copy.soundHit         = new SoundProfile();
        copy.soundAnchor      = new SoundProfile();

        return GSON.toJson(copy);
    }

    /** Import config from a JSON string, saving and reloading. */
    public static boolean importJson(String json) {
        if (json == null || json.isBlank()) return false;
        try {
            PvpTweaksConfig parsed = GSON.fromJson(json, PvpTweaksConfig.class);
            if (parsed == null) return false;

            // Preserve local sound settings during import
            PvpTweaksConfig current = PvpTweaksConfig.get();
            parsed.soundShieldBreak = current.soundShieldBreak;
            parsed.soundTotem       = current.soundTotem;
            parsed.soundCrystal     = current.soundCrystal;
            parsed.soundExplosion   = current.soundExplosion;
            parsed.soundHit         = current.soundHit;
            parsed.soundAnchor      = current.soundAnchor;

            java.nio.file.Path configPath = FabricLoader.getInstance()
                    .getConfigDir().resolve("pvptweaks.json");
            try (Writer w = Files.newBufferedWriter(configPath)) {
                GSON.toJson(parsed, w);
            }
            PvpTweaksConfig.load();
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Config imported (sounds preserved)");
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Import failed: {}", e.getMessage());
            return false;
        }
    }

    /** Apply the built-in Competitive preset. */
    public static void applyCompetitive() {
        PvpTweaksConfig c = PvpTweaksConfig.get();

        // Item Scales
        c.swordScalePct       = 70;
        c.axeScalePct         = 100;
        c.shieldScalePct      = 70;
        c.shieldOffsetX       = -55;
        c.shieldOffsetY       = -1;
        c.shieldOffsetZ       = 10;
        c.shieldRotX          = 0;
        c.shieldRotY          = 0;
        c.shieldRotZ          = 0;
        c.totemScalePct       = 100;
        c.goldenAppleScalePct = 100;
        c.anchorScalePct      = 100;
        c.bowScalePct         = 100;
        c.crossbowScalePct    = 100;
        c.tridentScalePct     = 100;
        c.maceScalePct        = 100;
        c.otherItemScalePct   = 100;

        // Totem pop animation (Customized)
        c.totemPopScalePct     = 50;
        c.totemPopVolumePct    = 5;   // Totem sound 5
        c.totemPopAnimScalePct = 30;  // Totem animation 30

        // Fire & Visuals
        c.fireOverlayScalePct = 20;
        c.fireEntityScalePct  = 30;
        c.hideFireOnGround    = false;
        c.firePreset          = "flat";
        c.endCrystalScalePct  = 100;

        // Sounds — Silent Explosions
        c.explosionVolumePct       = 0;
        c.hitVolumePct             = 100;
        c.crystalPopVolumePct      = 0;
        c.respawnAnchorExplosionPct = 0;

        // Particles — Minimal
        c.explosionParticlePct      = 0;
        c.crystalParticlePct        = 0;
        c.enderExplosionParticlePct = 5;  // Totem particles 5
        c.anchorExplosionParticlePct = 0;
        c.showHitParticles          = true; // Show hit crit yes

        // Targets
        c.soundEventTarget = "entity.generic.explode";
        c.textureTarget    = "totem_of_undying";

        // Reset Sound Profiles
        c.soundShieldBreak = new SoundProfile();
        c.soundTotem       = new SoundProfile();
        c.soundCrystal     = new SoundProfile();
        c.soundExplosion   = new SoundProfile();
        c.soundHit         = new SoundProfile();
        c.soundAnchor      = new SoundProfile();

        // Texture overrides
        c.textureOverrides = new java.util.HashMap<>();

        PvpTweaksConfig.save();
    }

    /** Reset everything to default values. */
    public static void applyDefault() {
        // Recreate a fresh instance and save it
        String defaultJson;
        try {
            defaultJson = GSON.toJson(new PvpTweaksConfig());
        } catch (Exception e) {
            return;
        }
        importJson(defaultJson);
    }

    private static String safeName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
    }
}
