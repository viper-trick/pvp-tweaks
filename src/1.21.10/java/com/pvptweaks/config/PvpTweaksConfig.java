package com.pvptweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PvpTweaksConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("pvptweaks.json");
    public static final Path SOUNDS_DIR   = FabricLoader.getInstance()
            .getConfigDir().resolve("pvptweaks/sounds");
    public static final Path TEXTURES_DIR = FabricLoader.getInstance()
            .getConfigDir().resolve("pvptweaks/textures");

    private static PvpTweaksConfig NEW_INSTANCE = new PvpTweaksConfig();
    private static PvpTweaksConfig LEGACY_INSTANCE = new PvpTweaksConfig();

    public static boolean useLegacyMenu = false;
    public int swordScalePct       = 70;
    public int axeScalePct         = 100;
    public int shieldScalePct      = 70;
    public int shieldOffsetX     = -55;  // x100 → actual = val/100f
    public int shieldOffsetY     = -1;
    public int shieldOffsetZ     = 10;
    public int shieldRotX        = 0;  // degrees
    public int shieldRotY        = 0;
    public int shieldRotZ        = 0;
    public SoundProfile soundShieldBreak = new SoundProfile();
    public int totemScalePct       = 100;
    public int goldenAppleScalePct = 100;
    public int anchorScalePct      = 100;
    public int bowScalePct         = 100;
    public int crossbowScalePct    = 100;
    public int tridentScalePct     = 100;
    public int maceScalePct        = 100;
    public int armorScalePct       = 100;
    public int otherItemScalePct   = 100;
    public int itemScaleGlobalPct  = 100;
    public String itemScaleGlobalMode = "off";
    public java.util.Set<String> itemScaleGlobalLinked = new java.util.HashSet<>();

    public java.util.Map<String, Integer> customItemScales = new java.util.HashMap<>();

    public int totemPopScalePct    = 50;
    public int totemPopVolumePct   = 5;
    public int totemPopAnimScalePct = 30;  // screen animation size

    public int fireOverlayScalePct = 20;
    public int     fireEntityScalePct  = 30;
    public boolean hideFireOnGround    = false;
    public String  firePreset           = "flat"; // vanilla | full | mid | low | flat | none
    public int     endCrystalScalePct  = 100;

    // CPS HUD
    public boolean cpsEnabled      = false;
    public float   cpsX            = 5f;
    public float   cpsY            = 15f;
    public int     cpsColor        = 0xFFFFFFFF; // White
    public boolean cpsRainbow      = false;
    public boolean cpsShadow       = true;
    public float   cpsScale        = 1.0f;
    public boolean cpsShowLabel    = true;
    public boolean durabilityHudEnabled   = false;
    public boolean durabilityHudBackground = true;
    public boolean durabilityHudShowExact = true;
    public boolean durabilityHudLowAlert  = true;
    public float   durabilityHudX         = 5f;
    public float   durabilityHudY         = 5f;
    public int     durabilityHudScalePct  = 100;
    public String  durabilityHudAlign     = "vertical"; // vertical | horizontal
    public boolean durabilityHudShowArmor = true;
    public boolean durabilityHudShowMainHand = true;
    public boolean durabilityHudShowOffHand  = true;
    public SoundProfile soundDurabilityLow = new SoundProfile();

    // Competitive / Client-Side
    public boolean crystalOptimizer = false;
    public boolean anchorOptimizer  = false;

    public int explosionVolumePct  = 0;
    public int hitVolumePct        = 100;
    public int crystalPopVolumePct = 0;

    public int     explosionParticlePct = 0;
    public int     crystalParticlePct         = 0;  // ambient crystal particles
    public int     enderExplosionParticlePct  = 5;  // crystal pop
    public int     respawnAnchorExplosionPct  = 0;
    public int     anchorExplosionParticlePct = 0;  // anchor explosion volume  // crystal explosion particles
    public boolean showHitParticles     = true;

    // Other Explosions — per-type volume + particles
    public int tntExplosionVolumePct       = 100;
    public int tntExplosionParticlePct     = 100;
    public int creeperExplosionVolumePct   = 100;
    public int creeperExplosionParticlePct = 100;
    public int bedExplosionVolumePct       = 100;
    public int bedExplosionParticlePct     = 100;
    public int ghastExplosionVolumePct     = 100;
    public int ghastExplosionParticlePct   = 100;
    public int windChargeVolumePct         = 100;
    public int windChargeParticlePct       = 100;

    // Vision
    public boolean disablePumpkinBlur  = false;
    public boolean fullbright          = false;
    public float   fullbrightGamma     = 5.0f;  // 1.0–5.0; 5.0 = maximum visible
    public String fullbrightManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("gammautils") ? "gammautils" : "pvp-tweaks";

    // Item Backgrounds
    public boolean totemBackgroundEnabled = true;
    public int totemBackgroundColor = 0x80FF0000; // Semi-transparent Red
    public boolean crystalBackgroundEnabled = true;
    public int crystalBackgroundColor = 0x80A020F0; // Semi-transparent Purple
    public String itemBackgroundMode = "both"; // inventory | outside | both | off
    public java.util.Map<String, Integer> customItemBackgrounds = new java.util.HashMap<>();

    public String hotbarSlotLabelMode = "off"; // keybinds | numbers | off
    // Custom Crosshair
    public boolean customCrosshairEnabled   = false;
    public int     crosshairStyle           = 0;    // 0=cross, 1=dot, 2=T, 3=X
    public int     crosshairRed             = 0;
    public int     crosshairGreen           = 255;
    public int     crosshairBlue            = 0;
    public int     crosshairAlpha           = 255;
    public float   crosshairSize            = 3.0f;
    public float   crosshairThickness       = 1.0f;
    public float   crosshairGap             = -2.0f;
    public boolean crosshairDot             = false;
    public boolean crosshairOutline         = false;
    public float   crosshairOutlineThickness = 1.0f;
    public float   crosshairSplitDistance    = 0.0f;
    public boolean crosshairFollowRecoil     = false;
    public boolean crosshairFixedGap         = false;
    public boolean crosshairGapUseWeapon     = false;
    public float   crosshairSplitSizeRatio   = 0.0f;

    // Zoom
    public boolean zoomEnabled = true;
    public double zoomLevel = 4.0;
    public boolean zoomToggle = false;
    public boolean zoomSmoothCamera = true;
    public String zoomManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify") ? "zoomify" : "pvp-tweaks";

    // Plants Control (ported from GRM — client-side only)
    public boolean plantsControlEnabled = false;
    public java.util.Set<String> hiddenPlants  = new java.util.HashSet<>();
    public java.util.Set<String> outlinePlants = new java.util.HashSet<>();

    // Optimizers

    // Durability alert
    public boolean durabilityAlertSoundOnce = false;

    public float getEndCrystalScale()      { return endCrystalScalePct  / 100.0f; }
    public float getFireOverlayScale()     { return fireOverlayScalePct  / 100.0f; }
    public float getFireEntityScale()      { return (hideFireOnGround || "none".equals(firePreset)) ? 0.0f : fireEntityScalePct / 100.0f; }
    public float getExplosionMultiplier()  { return explosionVolumePct   / 100.0f; }
    public float getHitMultiplier()        { return hitVolumePct         / 100.0f; }
    public float getCrystalPopMultiplier() { return crystalPopVolumePct  / 100.0f; }
    public float getExplosionRatio()       { return explosionParticlePct / 100.0f; }
    public float getCrystalRatio()              { return crystalParticlePct         / 100.0f; }
    public float getEnderExplosionRatio()      { return enderExplosionParticlePct  / 100.0f; }
    public float getRespawnAnchorMultiplier()   { return respawnAnchorExplosionPct    / 100.0f; }
    public float getAnchorExplosionRatio()     { return anchorExplosionParticlePct   / 100.0f; }
    public float getTotemPopMultiplier()   { return totemPopVolumePct    / 100.0f; }
    public float getTotemPopScale()        { return totemPopScalePct     / 100.0f; }
    public float getTotemPopAnimScale()    { return totemPopAnimScalePct / 100.0f; }
    public float getArmorScale()           { return armorScalePct / 100.0f; }

    // Per-type explosion multipliers — volume
    public float getTntExplosionMultiplier()     { return tntExplosionVolumePct     / 100.0f; }
    public float getCreeperExplosionMultiplier() { return creeperExplosionVolumePct / 100.0f; }
    public float getBedExplosionMultiplier()     { return bedExplosionVolumePct     / 100.0f; }
    public float getGhastExplosionMultiplier()   { return ghastExplosionVolumePct   / 100.0f; }
    public float getWindChargeMultiplier()       { return windChargeVolumePct       / 100.0f; }

    // Per-type explosion multipliers — particles
    public float getTntExplosionParticleRatio()     { return tntExplosionParticlePct     / 100.0f; }
    public float getCreeperExplosionParticleRatio() { return creeperExplosionParticlePct / 100.0f; }
    public float getBedExplosionParticleRatio()     { return bedExplosionParticlePct     / 100.0f; }
    public float getGhastExplosionParticleRatio()   { return ghastExplosionParticlePct   / 100.0f; }
    public float getWindChargeParticleRatio()       { return windChargeParticlePct       / 100.0f; }

    public float getItemScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return otherItemScalePct / 100.0f;
        Item item = stack.getItem();
        String itemPath = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString();

        if (customItemScales.containsKey(itemPath)) {
            return customItemScales.get(itemPath) / 100.0f;
        }

        float global = itemScaleGlobalPct / 100.0f;

        // Swords
        if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD
         || item == Items.IRON_SWORD   || item == Items.GOLDEN_SWORD
         || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD)
            return resolveGlobal("sword", swordScalePct / 100.0f, global);
        // Axes
        if (item == Items.WOODEN_AXE  || item == Items.STONE_AXE
         || item == Items.IRON_AXE    || item == Items.GOLDEN_AXE
         || item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE)
            return resolveGlobal("axe", axeScalePct / 100.0f, global);
        // Shield
        if (item == Items.SHIELD)
            return resolveGlobal("shield", shieldScalePct / 100.0f, global);
        // Totem
        if (item == Items.TOTEM_OF_UNDYING)
            return resolveGlobal("totem", totemScalePct / 100.0f, global);
        // Golden apples
        if (item == Items.GOLDEN_APPLE
         || item == Items.ENCHANTED_GOLDEN_APPLE)
            return resolveGlobal("goldenApple", goldenAppleScalePct / 100.0f, global);
        // Anchor
        if (item == Items.RESPAWN_ANCHOR)
            return resolveGlobal("anchor", anchorScalePct / 100.0f, global);
        // Bow / Crossbow
        if (item == Items.BOW)
            return resolveGlobal("bow", bowScalePct / 100.0f, global);
        if (item == Items.CROSSBOW)
            return resolveGlobal("crossbow", crossbowScalePct / 100.0f, global);
        // Trident
        if (item == Items.TRIDENT)
            return resolveGlobal("trident", tridentScalePct / 100.0f, global);
        // Mace
        if (item == Items.MACE)
            return resolveGlobal("mace", maceScalePct / 100.0f, global);
        // Armor
        {
            if (itemPath.endsWith("_helmet") || itemPath.endsWith("_chestplate")
             || itemPath.endsWith("_leggings") || itemPath.endsWith("_boots")
             || item == Items.ELYTRA || itemPath.equals("turtle_helmet")) {
                return resolveGlobal("armor", armorScalePct / 100.0f, global);
            }
        }
        return resolveGlobal("otherItems", otherItemScalePct / 100.0f, global);
    }

    private float resolveGlobal(String key, float categoryVal, float global) {
        if ("off".equals(itemScaleGlobalMode)) return categoryVal;
        if ("listed".equals(itemScaleGlobalMode)) return global;
        if ("unlisted".equals(itemScaleGlobalMode)) return "otherItems".equals(key) ? global : categoryVal;
        if ("custom".equals(itemScaleGlobalMode) && itemScaleGlobalLinked.contains(key)) return global;
        return categoryVal;
    }

    public String soundEventTarget = "entity.generic.explode";
    public String textureTarget      = "totem_of_undying";

    public String lastSettingsProfile = "None";
    public String lastKeybindsProfile = "None";


    // Sound overrides per PVP event
    public SoundProfile soundTotem      = new SoundProfile();
    public SoundProfile soundCrystal    = new SoundProfile();
    public SoundProfile soundExplosion  = new SoundProfile();
    public SoundProfile soundHit        = new SoundProfile();
    public SoundProfile soundAnchor     = new SoundProfile();

    // Per-type Other Explosion sound overrides
    public SoundProfile soundTnt        = new SoundProfile();
    public SoundProfile soundCreeper    = new SoundProfile();
    public SoundProfile soundBed        = new SoundProfile();
    public SoundProfile soundGhast      = new SoundProfile();
    public SoundProfile soundWindCharge = new SoundProfile();

    // User-added extra sound overrides (sound event ID -> profile)
    public java.util.Map<String, SoundProfile> extraSounds =
        new java.util.LinkedHashMap<>();

    // Texture overrides (item name -> absolute file path)
    public java.util.Map<String, String> textureOverrides =
        new java.util.HashMap<>();

    public static PvpTweaksConfig get() {
        return useLegacyMenu ? LEGACY_INSTANCE : NEW_INSTANCE;
    }

    public static void set(PvpTweaksConfig newConfig) {
        if (useLegacyMenu) {
            LEGACY_INSTANCE = newConfig;
        } else {
            NEW_INSTANCE = newConfig;
        }
    }

    private static class Preferences {
        public boolean useLegacyMenu = false;
    }

    public static void load() {
        // 1. Load preferences
        Path prefsFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/preferences.json");
        if (Files.exists(prefsFile)) {
            try (Reader r = Files.newBufferedReader(prefsFile)) {
                Preferences prefs = GSON.fromJson(r, Preferences.class);
                if (prefs != null) {
                    useLegacyMenu = prefs.useLegacyMenu;
                }
            } catch (Exception ignored) {}
        }

        // 2. Load configs
        Path newFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/new_menu_config.json");
        Path legacyFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/legacy_menu_config.json");
        Path oldSharedFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks.json");

        // Load new menu config
        if (Files.exists(newFile)) {
            try (Reader r = Files.newBufferedReader(newFile)) {
                PvpTweaksConfig loaded = GSON.fromJson(r, PvpTweaksConfig.class);
                if (loaded != null) NEW_INSTANCE = loaded;
            } catch (Exception ignored) {}
        } else {
            // First time load: Initialize NEW_INSTANCE to Vanilla defaults so it doesn't affect gameplay by default!
            NEW_INSTANCE = new PvpTweaksConfig();
            applyVanillaToConfig(NEW_INSTANCE);
            // Save it so we persist the clean default state
            try {
                Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("pvptweaks"));
                try (Writer w = Files.newBufferedWriter(newFile)) {
                    GSON.toJson(NEW_INSTANCE, w);
                }
            } catch (Exception ignored) {}
        }

        // Migration: set defaults for new fields that may be null in existing configs
        if (NEW_INSTANCE.fullbrightManagementMode == null) {
            NEW_INSTANCE.fullbrightManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("gammautils") ? "gammautils" : "pvp-tweaks";
        }
        if (NEW_INSTANCE.zoomManagementMode == null) {
            NEW_INSTANCE.zoomManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify") ? "zoomify" : "pvp-tweaks";
        }

        // Load legacy menu config
        if (Files.exists(legacyFile)) {
            try (Reader r = Files.newBufferedReader(legacyFile)) {
                PvpTweaksConfig loaded = GSON.fromJson(r, PvpTweaksConfig.class);
                if (loaded != null) LEGACY_INSTANCE = loaded;
            } catch (Exception ignored) {}
        } else if (Files.exists(oldSharedFile)) {
            try (Reader r = Files.newBufferedReader(oldSharedFile)) {
                PvpTweaksConfig loaded = GSON.fromJson(r, PvpTweaksConfig.class);
                if (loaded != null) LEGACY_INSTANCE = loaded;
            } catch (Exception ignored) {}
        } else {
            // First time load: Initialize LEGACY_INSTANCE to the Mod's default competitive settings
            LEGACY_INSTANCE = new PvpTweaksConfig();
            try {
                Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("pvptweaks"));
                try (Writer w = Files.newBufferedWriter(legacyFile)) {
                    GSON.toJson(LEGACY_INSTANCE, w);
                }
            } catch (Exception ignored) {}
        }

        // Migration: set defaults for new fields that may be null in existing configs
        if (LEGACY_INSTANCE.fullbrightManagementMode == null) {
            LEGACY_INSTANCE.fullbrightManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("gammautils") ? "gammautils" : "pvp-tweaks";
        }
        if (LEGACY_INSTANCE.zoomManagementMode == null) {
            LEGACY_INSTANCE.zoomManagementMode = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify") ? "zoomify" : "pvp-tweaks";
        }
    }

    public static void save() {
        try {
            Files.createDirectories(SOUNDS_DIR);
            Files.createDirectories(TEXTURES_DIR);
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("pvptweaks"));

            // 1. Save preferences
            Path prefsFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/preferences.json");
            Preferences prefs = new Preferences();
            prefs.useLegacyMenu = useLegacyMenu;
            try (Writer w = Files.newBufferedWriter(prefsFile)) {
                GSON.toJson(prefs, w);
            }

            // 2. Save configs
            Path newFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/new_menu_config.json");
            Path legacyFile = FabricLoader.getInstance().getConfigDir().resolve("pvptweaks/legacy_menu_config.json");

            try (Writer w = Files.newBufferedWriter(newFile)) {
                GSON.toJson(NEW_INSTANCE, w);
            }
            try (Writer w = Files.newBufferedWriter(legacyFile)) {
                GSON.toJson(LEGACY_INSTANCE, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyVanillaToConfig(PvpTweaksConfig cfg) {
        cfg.swordScalePct = 100;
        cfg.axeScalePct = 100;
        cfg.shieldScalePct = 100;
        cfg.shieldOffsetX = 0;
        cfg.shieldOffsetY = 0;
        cfg.shieldOffsetZ = 0;
        cfg.shieldRotX = 0;
        cfg.shieldRotY = 0;
        cfg.shieldRotZ = 0;
        cfg.totemScalePct = 100;
        cfg.goldenAppleScalePct = 100;
        cfg.anchorScalePct = 100;
        cfg.bowScalePct = 100;
        cfg.crossbowScalePct = 100;
        cfg.tridentScalePct = 100;
        cfg.maceScalePct = 100;
        cfg.armorScalePct = 100;
        cfg.otherItemScalePct = 100;
        cfg.itemScaleGlobalPct = 100;
        cfg.itemScaleGlobalMode = "off";
        cfg.itemScaleGlobalLinked.clear();
        cfg.customItemScales.clear();

        cfg.totemPopScalePct = 100;
        cfg.totemPopVolumePct = 100;
        cfg.totemPopAnimScalePct = 100;

        cfg.fireOverlayScalePct = 100;
        cfg.firePreset = "vanilla";
        cfg.hideFireOnGround = false;
        cfg.endCrystalScalePct = 100;

        cfg.cpsEnabled = false;
        cfg.durabilityHudEnabled = false;
        cfg.crystalOptimizer = false;
        cfg.anchorOptimizer = false;

        cfg.explosionVolumePct = 100;
        cfg.hitVolumePct = 100;
        cfg.crystalPopVolumePct = 100;

        cfg.explosionParticlePct = 100;
        cfg.crystalParticlePct = 100;
        cfg.enderExplosionParticlePct = 100;
        cfg.anchorExplosionParticlePct = 100;
        cfg.showHitParticles = true;

        cfg.tntExplosionVolumePct = 100;
        cfg.tntExplosionParticlePct = 100;
        cfg.creeperExplosionVolumePct = 100;
        cfg.creeperExplosionParticlePct = 100;
        cfg.bedExplosionVolumePct = 100;
        cfg.bedExplosionParticlePct = 100;
        cfg.ghastExplosionVolumePct = 100;
        cfg.ghastExplosionParticlePct = 100;
        cfg.windChargeVolumePct = 100;
        cfg.windChargeParticlePct = 100;

        cfg.disablePumpkinBlur = false;
        cfg.fullbright = false;
        cfg.fullbrightGamma = 1.0f;


    }
}
