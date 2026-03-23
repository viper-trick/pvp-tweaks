package com.pvptweaks.integration;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.PvpTweaksConfigScreen;
import com.pvptweaks.gui.SoundPickerScreen;
import com.pvptweaks.sound.CustomSoundManager;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> PvpTweaksConfigScreen.build(parent);
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> map = new LinkedHashMap<>();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        // Sound Pickers — אחד לכל קטגוריה
        map.put("pvptweaks:sound_totem",
            parent -> new SoundPickerScreen(parent, cfg.soundTotem,    "Totem Pop",       PvpTweaksConfig::save));
        map.put("pvptweaks:sound_crystal",
            parent -> new SoundPickerScreen(parent, cfg.soundCrystal,  "End Crystal",     PvpTweaksConfig::save));
        map.put("pvptweaks:sound_anchor",
            parent -> new SoundPickerScreen(parent, cfg.soundAnchor,   "Respawn Anchor",  PvpTweaksConfig::save));
        map.put("pvptweaks:sound_explosion",
            parent -> new SoundPickerScreen(parent, cfg.soundExplosion,"Other Explosions", PvpTweaksConfig::save));
        map.put("pvptweaks:sound_hit",
            parent -> new SoundPickerScreen(parent, cfg.soundHit,      "Hit Sound",       PvpTweaksConfig::save));
        map.put("pvptweaks:sound_shield",
            parent -> new SoundPickerScreen(parent, cfg.soundShieldBreak,"Shield Break",  PvpTweaksConfig::save));
        return map;
    }
}
