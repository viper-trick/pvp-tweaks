package com.pvptweaks;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.DurabilityHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PvpTweaksClient implements ClientModInitializer {

    private static String lastFirePreset = "";
    private static double savedGamma = -1.0;
    
    public static KeyMapping openMenuKeyBinding;
    public static KeyMapping zoomKeyBinding;

    @Override
    public void onInitializeClient() {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Client initialised.");

        openMenuKeyBinding = new KeyMapping(
                "key.pvptweaks.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KeyMapping.Category.MISC
        );

        zoomKeyBinding = new KeyMapping(
                "key.pvptweaks.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC
        );

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("pvptweaks", "durability_hud"),
            DurabilityHudRenderer::extractRenderState
        );
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("pvptweaks", "cps_hud"),
            com.pvptweaks.gui.CpsHudRenderer::extractRenderState
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) return;
            
            while (openMenuKeyBinding.consumeClick()) {
                if (client.gui.screen() == null) {
                    if (PvpTweaksConfig.get().useLegacyMenu) {
                        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("cloth-config")) {
                            client.setScreenAndShow(new com.pvptweaks.gui.ClothConfigRequiredScreen(null));
                        } else {
                            client.setScreenAndShow(com.pvptweaks.integration.ClothConfigScreenHelper.buildScreen(null));
                        }
                    } else {
                        client.setScreenAndShow(new com.pvptweaks.gui.PvpTweaksHubScreen(null));
                    }
                }
            }

            com.pvptweaks.zoom.ZoomManager.updateToggleState();

            PvpTweaksConfig cfg = PvpTweaksConfig.get();

            // ── Fire preset reload ────────────────────────────────────────────
            String current = cfg.firePreset;
            if (current == null) current = "vanilla";
            if (!current.equals(lastFirePreset)) {
                lastFirePreset = current;
                if (client.levelExtractor != null) client.levelExtractor.allChanged();
            }

            // ── Fullbright ────────────────────────────────────────────────────
            boolean gammaUtilsMode = "gammautils".equals(cfg.fullbrightManagementMode)
                && net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("gammautils");
            var gammaOpt = client.options.gamma();
            if (!gammaUtilsMode) {
                if (cfg.fullbright) {
                    if (savedGamma < 0.0) savedGamma = gammaOpt.get();
                    gammaOpt.set((double) cfg.fullbrightGamma);
                } else if (savedGamma >= 0.0) {
                    gammaOpt.set(savedGamma);
                    savedGamma = -1.0;
                }
            } else if (savedGamma >= 0.0) {
                gammaOpt.set(savedGamma);
                savedGamma = -1.0;
            }
        });
    }
}
