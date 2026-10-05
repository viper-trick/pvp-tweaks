package com.pvptweaks;
import com.mojang.blaze3d.platform.InputConstants;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.DurabilityHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PvpTweaksClient implements ClientModInitializer {

    private static double savedGamma = -1.0;
    
    public static KeyMapping openMenuKeyBinding;
    public static KeyMapping zoomKeyBinding;

    @Override
    public void onInitializeClient() {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Client initialised.");

        openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.pvptweaks.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                net.minecraft.client.KeyMapping.Category.MISC
        ));

        zoomKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.pvptweaks.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                net.minecraft.client.KeyMapping.Category.MISC
        ));

        HudRenderCallback.EVENT.register(DurabilityHudRenderer::render);
        HudRenderCallback.EVENT.register(com.pvptweaks.gui.CpsHudRenderer::render);

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

            // ── Fullbright ────────────────────────────────────────────────────
            boolean gammaUtilsMode = "gammautils".equals(cfg.fullbrightManagementMode)
                && net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("gammautils");
            net.minecraft.client.OptionInstance<Double> gammaOpt = client.options.gamma();
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
