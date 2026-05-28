package com.pvptweaks.zoom;

import com.pvptweaks.PvpTweaksClient;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class ZoomManager {
    public static float currentZoomProgress = 1.0f;
    private static boolean isZoomToggled = false;

    public static boolean isZooming() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.zoomEnabled) return false;
        if ("zoomify".equals(cfg.zoomManagementMode) && net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify")) {
            return false;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return false;

        if (cfg.zoomToggle) {
            return isZoomToggled;
        } else {
            return PvpTweaksClient.zoomKeyBinding.isPressed();
        }
    }

    public static void updateToggleState() {
        if (PvpTweaksConfig.get().zoomToggle) {
            while (PvpTweaksClient.zoomKeyBinding.wasPressed()) {
                isZoomToggled = !isZoomToggled;
            }
        } else {
            isZoomToggled = false;
        }
    }

    public static float getSmoothZoom(float tickDelta) {
        float target = isZooming() ? (float) PvpTweaksConfig.get().zoomLevel : 1.0f;
        
        // Smoothly interpolate currentZoomProgress towards target
        currentZoomProgress += (target - currentZoomProgress) * (0.25f * tickDelta);
        if (Math.abs(currentZoomProgress - target) < 0.01f) {
            currentZoomProgress = target;
        }
        return currentZoomProgress;
    }

    public static void onMouseScroll(double amount) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        // Increase/decrease zoomLevel
        double step = cfg.zoomLevel < 5.0 ? 0.5 : 1.0;
        cfg.zoomLevel = Math.max(1.5, Math.min(40.0, cfg.zoomLevel + amount * step));
    }
}
