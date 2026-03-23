package com.pvptweaks.config;

/**
 * Defines a sound override for a PVP event.
 * mode = "default" | "preset" | "custom"
 * presetId  = Minecraft sound event ID (e.g. "entity.firework_rocket.blast")
 * customPath = absolute path to .ogg/.mp3/.wav file
 */
public class SoundProfile {
    public String mode       = "default";
    public String presetId   = "";
    public String customPath = "";

    public boolean isDefault() { return "default".equals(mode); }
    public boolean isPreset()  { return "preset".equals(mode);  }
    public boolean isCustom()  { return "custom".equals(mode);  }
}
