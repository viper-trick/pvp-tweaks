package com.pvptweaks;

import com.pvptweaks.config.PvpTweaksConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public class PvpTweaksClient implements ClientModInitializer {

    private static String lastFirePreset = "";

    @Override
    public void onInitializeClient() {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Client initialised.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            String current = PvpTweaksConfig.get().firePreset;
            if (current == null) current = "vanilla";
            if (!current.equals(lastFirePreset)) {
                lastFirePreset = current;
                if (client.worldRenderer != null) client.worldRenderer.reload();
            }
        });
    }
}
