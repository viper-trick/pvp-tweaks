package com.pvptweaks;

import com.pvptweaks.config.PvpTweaksConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpTweaksMod implements ModInitializer {
    public static final String MOD_ID = "pvptweaks";
    public static final Logger LOGGER  = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PvpTweaksConfig.load();
        LOGGER.info("[PVP Tweaks] Config loaded.");
        LOGGER.info("[PVP Tweaks] Sounds dir   -> {}", PvpTweaksConfig.SOUNDS_DIR);
        LOGGER.info("[PVP Tweaks] Textures dir -> {}", PvpTweaksConfig.TEXTURES_DIR);
    }
}
