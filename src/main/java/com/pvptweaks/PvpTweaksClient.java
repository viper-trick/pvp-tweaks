package com.pvptweaks;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.DurabilityHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PvpTweaksClient implements ClientModInitializer {

    private static String lastFirePreset = "";
    /** Gamma saved before fullbright was enabled — restored when disabled. */
    private static double savedGamma = -1.0;
    
    public static KeyBinding openMenuKeyBinding;
    public static KeyBinding zoomKeyBinding;

    @Override
    public void onInitializeClient() {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Client initialised.");

        openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvptweaks.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                net.minecraft.client.option.KeyBinding.Category.MISC
        ));

        zoomKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvptweaks.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                net.minecraft.client.option.KeyBinding.Category.MISC
        ));

        HudRenderCallback.EVENT.register(DurabilityHudRenderer::render);
        HudRenderCallback.EVENT.register(com.pvptweaks.gui.CpsHudRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            
            while (openMenuKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
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

            // Track mouse clicks via GLFW
            long handle = client.getWindow().getHandle();
            boolean leftDown  = org.lwjgl.glfw.GLFW.glfwGetMouseButton(handle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean rightDown = org.lwjgl.glfw.GLFW.glfwGetMouseButton(handle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            com.pvptweaks.gui.CpsTracker.update(leftDown, rightDown);

            com.pvptweaks.zoom.ZoomManager.updateToggleState();

            PvpTweaksConfig cfg = PvpTweaksConfig.get();

            // ── Fire preset reload ────────────────────────────────────────────
            String current = cfg.firePreset;
            if (current == null) current = "vanilla";
            if (!current.equals(lastFirePreset)) {
                lastFirePreset = current;
                if (client.worldRenderer != null) client.worldRenderer.reload();
            }

            // ── Fullbright ────────────────────────────────────────────────────
            net.minecraft.client.option.SimpleOption<Double> gammaOpt = client.options.getGamma();
            if (cfg.fullbright) {
                if (savedGamma < 0.0) savedGamma = gammaOpt.getValue();
                gammaOpt.setValue((double) cfg.fullbrightGamma);
            } else if (savedGamma >= 0.0) {
                gammaOpt.setValue(savedGamma);
                savedGamma = -1.0;
            }
        });
    }
}
