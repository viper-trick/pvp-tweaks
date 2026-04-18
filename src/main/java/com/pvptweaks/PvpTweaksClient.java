package com.pvptweaks;
import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.gui.DurabilityHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class PvpTweaksClient implements ClientModInitializer {

    private static String lastFirePreset = "";

    @Override
    public void onInitializeClient() {
        PvpTweaksMod.LOGGER.info("[PVP Tweaks] Client initialised.");

        HudRenderCallback.EVENT.register(DurabilityHudRenderer::render);
        HudRenderCallback.EVENT.register(com.pvptweaks.gui.CpsHudRenderer::render);

        // Register Mouse events
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            // Track mouse clicks correctly using GLFW
            long handle = client.getWindow().getHandle();
            boolean leftDown = org.lwjgl.glfw.GLFW.glfwGetMouseButton(handle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean rightDown = org.lwjgl.glfw.GLFW.glfwGetMouseButton(handle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            com.pvptweaks.gui.CpsTracker.update(leftDown, rightDown);
            
            String current = PvpTweaksConfig.get().firePreset;
            if (current == null) current = "vanilla";
            if (!current.equals(lastFirePreset)) {
                lastFirePreset = current;
                if (client.worldRenderer != null) client.worldRenderer.reload();
            }
        });
    }
}
