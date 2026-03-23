package com.pvptweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;

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

    private static PvpTweaksConfig INSTANCE = new PvpTweaksConfig();

    public int swordScalePct       = 100;
    public int axeScalePct         = 100;
    public int shieldScalePct      = 100;
    public int shieldOffsetX     = 0;  // x100 → actual = val/100f
    public int shieldOffsetY     = 0;
    public int shieldOffsetZ     = 0;
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
    public int otherItemScalePct   = 100;

    public int totemPopScalePct    = 100;
    public int totemPopVolumePct   = 100;
    public int totemPopAnimScalePct = 100;  // screen animation size

    public int fireOverlayScalePct = 100;
    public int     fireEntityScalePct  = 100;
    public boolean hideFireOnGround    = false;
    public String  firePreset           = "vanilla"; // vanilla | full | mid | low | flat | none
    public int endCrystalScalePct  = 100;

    public int explosionVolumePct  = 100;
    public int hitVolumePct        = 100;
    public int crystalPopVolumePct = 100;

    public int     explosionParticlePct = 100;
    public int     crystalParticlePct         = 100;  // ambient crystal particles
    public int     enderExplosionParticlePct  = 100;  // crystal pop
    public int     respawnAnchorExplosionPct  = 100;
    public int     anchorExplosionParticlePct = 100;  // anchor explosion volume  // crystal explosion particles
    public boolean showHitParticles     = true;

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

    public float getItemScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return otherItemScalePct / 100.0f;
        Item item = stack.getItem();
        // Swords
        if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD
         || item == Items.IRON_SWORD   || item == Items.GOLDEN_SWORD
         || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD)
            return swordScalePct / 100.0f;
        // Axes
        if (item == Items.WOODEN_AXE  || item == Items.STONE_AXE
         || item == Items.IRON_AXE    || item == Items.GOLDEN_AXE
         || item == Items.DIAMOND_AXE || item == Items.NETHERITE_AXE)
            return axeScalePct / 100.0f;
        // Shield
        if (item == Items.SHIELD)             return shieldScalePct      / 100.0f;
        // Totem
        if (item == Items.TOTEM_OF_UNDYING)   return totemScalePct       / 100.0f;
        // Golden apples
        if (item == Items.GOLDEN_APPLE
         || item == Items.ENCHANTED_GOLDEN_APPLE) return goldenAppleScalePct / 100.0f;
        // Anchor
        if (item == Items.RESPAWN_ANCHOR)     return anchorScalePct      / 100.0f;
        // Bow / Crossbow
        if (item == Items.BOW)                return bowScalePct         / 100.0f;
        if (item == Items.CROSSBOW)           return crossbowScalePct    / 100.0f;
        // Trident
        if (item == Items.TRIDENT)            return tridentScalePct     / 100.0f;
        // Mace
        if (item == Items.MACE)               return maceScalePct        / 100.0f;
        return otherItemScalePct / 100.0f;
    }

    public String soundEventTarget = "entity.generic.explode";
    public String textureTarget      = "totem_of_undying";


    // Sound overrides per PVP event
    public SoundProfile soundTotem      = new SoundProfile();
    public SoundProfile soundCrystal    = new SoundProfile();
    public SoundProfile soundExplosion  = new SoundProfile();
    public SoundProfile soundHit        = new SoundProfile();
    public SoundProfile soundAnchor     = new SoundProfile();

    // Texture overrides (item name -> absolute file path)
    public java.util.Map<String, String> textureOverrides =
        new java.util.HashMap<>();

    public static PvpTweaksConfig get() { return INSTANCE; }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) { save(); return; }
        try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
            PvpTweaksConfig loaded = GSON.fromJson(r, PvpTweaksConfig.class);
            if (loaded != null) INSTANCE = loaded;
        } catch (IOException e) { INSTANCE = new PvpTweaksConfig(); }
    }

    public static void save() {
        try {
            Files.createDirectories(SOUNDS_DIR);
            Files.createDirectories(TEXTURES_DIR);
            try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, w);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
