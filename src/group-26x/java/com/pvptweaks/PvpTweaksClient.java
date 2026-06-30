package com.pvptweaks;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.DurabilityHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
// TODO HudElement: import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
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

        // TODO HudElement: HudRenderCallback.EVENT.register(DurabilityHudRenderer::render);
        // TODO HudElement: HudRenderCallback.EVENT.register(com.pvptweaks.gui.CpsHudRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) return;
            
            while (openMenuKeyBinding.consumeClick()) {
                if (client.screen == null) {
                    if (PvpTweaksConfig.get().useLegacyMenu) {
                        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("cloth-config")) {
                            client.setScreen(new com.pvptweaks.gui.ClothConfigRequiredScreen(null));
                        } else {
                            client.setScreen(com.pvptweaks.integration.ClothConfigScreenHelper.buildScreen(null));
                        }
                    } else {
                        client.setScreen(new com.pvptweaks.gui.PvpTweaksHubScreen(null));
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
                if (client.levelRenderer != null) client.levelRenderer.allChanged();
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
