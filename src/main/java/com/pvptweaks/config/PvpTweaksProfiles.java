package com.pvptweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pvptweaks.PvpTweaksMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages named profiles — snapshots of the full PvpTweaksConfig and Minecraft Keybinds.
 * Each profile is stored as a separate JSON file in:
 *   config/pvptweaks/profiles/<name>.json
 */
public class PvpTweaksProfiles {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path PROFILES_DIR = FabricLoader.getInstance()
            .getConfigDir().resolve("pvptweaks/profiles");

    /** Data structure for sharing and saving profiles. */
    public static class ExportPackage {
        public String name;
        public PvpTweaksConfig config;
        public Map<String, String> keybinds;

        public ExportPackage() {}
        public ExportPackage(String name, PvpTweaksConfig config, Map<String, String> keybinds) {
            this.name = name;
            this.config = config;
            this.keybinds = keybinds;
        }
    }

    /** Capture all current game keybinds. */
    public static Map<String, String> getCurrentKeybinds() {
        Map<String, String> keys = new HashMap<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return keys;

        for (KeyBinding kb : mc.options.allKeys) {
            keys.put(kb.getId(), kb.getBoundKeyTranslationKey());
        }
        return keys;
    }

    /** Apply keybinds to Minecraft options. */
    public static void applyKeybinds(Map<String, String> keybinds) {
        if (keybinds == null || keybinds.isEmpty()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        for (KeyBinding kb : mc.options.allKeys) {
            String bound = keybinds.get(kb.getId());
            if (bound != null) {
                kb.setBoundKey(InputUtil.fromTranslationKey(bound));
            }
        }
        KeyBinding.updateKeysByCode();
        mc.options.write();
    }

    /** Save the current config and keybinds as a named profile. */
    public static boolean save(String name) {
        if (name == null || name.isBlank()) return false;
        String safe = safeName(name);
        try {
            Files.createDirectories(PROFILES_DIR);
            Path file = PROFILES_DIR.resolve(safe + ".json");
            
            ExportPackage pkg = new ExportPackage(name, PvpTweaksConfig.get(), getCurrentKeybinds());
            
            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(pkg, w);
            }
            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Profile saved: {}", safe);
            return true;
        } catch (IOException e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Profile save failed: {}", e.getMessage());
            return false;
        }
    }

    /** Load a profile by name, replacing the current config and keybinds. */
    public static boolean load(String name) {
        if (name == null || name.isBlank()) return false;
        Path file = PROFILES_DIR.resolve(safeName(name) + ".json");
        if (!Files.exists(file)) return false;
        try (Reader r = Files.newBufferedReader(file)) {
            // Try loading as ExportPackage first
            String content = Files.readString(file);
            ExportPackage pkg;
            try {
                pkg = GSON.fromJson(content, ExportPackage.class);
                // Heuristic: if config is null, it's probably the old format
                if (pkg == null || pkg.config == null) {
                    throw new Exception("Old format");
                }
            } catch (Exception e) {
                // Backward compatibility: load as PvpTweaksConfig
                PvpTweaksConfig old = GSON.fromJson(content, PvpTweaksConfig.class);
                pkg = new ExportPackage(name, old, null);
            }

            if (pkg.config == null) return false;

            // Apply mod config
            String json = GSON.toJson(pkg.config);
            PvpTweaksConfig updated = GSON.fromJson(json, PvpTweaksConfig.class);
            updated.lastSettingsProfile = name; // Update current name in the loaded config
            
            java.nio.file.Path configPath = FabricLoader.getInstance()
                    .getConfigDir().resolve("pvptweaks.json");
            try (Writer w = Files.newBufferedWriter(configPath)) {
                GSON.toJson(updated, w);
            }
            PvpTweaksConfig.load();

            // Apply keybinds if present
            if (pkg.keybinds != null) {
                applyKeybinds(pkg.keybinds);
                PvpTweaksConfig.get().lastKeybindsProfile = name;
                PvpTweaksConfig.save();
            }

            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Profile loaded: {}", name);
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Profile load failed: {}", e.getMessage());
            return false;
        }
    }

    /** Export current data as a JSON string. */
    public static String exportJson(String name, boolean includeConfig, boolean includeKeys) {
        PvpTweaksConfig config = null;
        if (includeConfig) {
            PvpTweaksConfig current = PvpTweaksConfig.get();
            config = GSON.fromJson(GSON.toJson(current), PvpTweaksConfig.class);
            // Strip sounds
            config.soundShieldBreak = new SoundProfile();
            config.soundTotem       = new SoundProfile();
            config.soundCrystal     = new SoundProfile();
            config.soundExplosion   = new SoundProfile();
            config.soundHit         = new SoundProfile();
            config.soundAnchor      = new SoundProfile();
        }

        Map<String, String> keys = includeKeys ? getCurrentKeybinds() : null;
        ExportPackage pkg = new ExportPackage(name, config, keys);
        return GSON.toJson(pkg);
    }

    /** Import from JSON, apply components, and save as an "Imported" profile. */
    public static boolean importPackage(String json, boolean applyConfig, boolean applyKeys) {
        if (json == null || json.isBlank()) return false;
        try {
            ExportPackage pkg = GSON.fromJson(json, ExportPackage.class);
            if (pkg == null) return false;

            // Save as a new profile with "(Imported)" suffix
            String importedName = (pkg.name != null ? pkg.name : "Unknown") + " (Imported)";
            
            if (applyConfig && pkg.config != null) {
                // Preserve local sound settings during import
                PvpTweaksConfig current = PvpTweaksConfig.get();
                pkg.config.soundShieldBreak = current.soundShieldBreak;
                pkg.config.soundTotem       = current.soundTotem;
                pkg.config.soundCrystal     = current.soundCrystal;
                pkg.config.soundExplosion   = current.soundExplosion;
                pkg.config.soundHit         = current.soundHit;
                pkg.config.soundAnchor      = current.soundAnchor;
                pkg.config.lastSettingsProfile = importedName;

                java.nio.file.Path configPath = FabricLoader.getInstance()
                        .getConfigDir().resolve("pvptweaks.json");
                try (Writer w = Files.newBufferedWriter(configPath)) {
                    GSON.toJson(pkg.config, w);
                }
                PvpTweaksConfig.load();
            }

            if (applyKeys && pkg.keybinds != null) {
                applyKeybinds(pkg.keybinds);
                PvpTweaksConfig.get().lastKeybindsProfile = importedName;
                PvpTweaksConfig.save();
            }

            save(importedName);

            PvpTweaksMod.LOGGER.info("[PVP Tweaks] Package imported and saved as profile: {}", importedName);
            return true;
        } catch (Exception e) {
            PvpTweaksMod.LOGGER.error("[PVP Tweaks] Import failed: {}", e.getMessage());
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

    /** Import config from a JSON string, saving and reloading. */
    public static boolean importJson(String json) {
        return importPackage(json, true, false);
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
        
        // Durability HUD
        c.durabilityHudEnabled = true;

        // Optimizers
        c.crystalOptimizer = true;
        c.anchorOptimizer  = true;

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
