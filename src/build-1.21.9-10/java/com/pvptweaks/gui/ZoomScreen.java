package com.pvptweaks.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.pvptweaks.PvpTweaksClient;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ZoomScreen extends Screen {
    private final Screen parent;
    private boolean listeningForKeybind = false;

    public ZoomScreen(Screen parent) {
        super(Component.literal("Zoom Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int cx = width / 2;
        int y = height / 2 - 90;

        boolean zoomifyInstalled = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify");
        boolean zoomifyMode = "zoomify".equals(cfg.zoomManagementMode) && zoomifyInstalled;

        // ── Mode toggle ──────────────────────────────────────────────────
        String modeLabel;
        if (!zoomifyInstalled) {
            modeLabel = "Zoom Manager: pvp tweaks (default)";
        } else {
            modeLabel = "Zoom Manager: " + (zoomifyMode ? "Zoomify (Zoom)" : "pvp tweaks (default)");
        }

        addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20, Component.literal(modeLabel), () -> {
            if (zoomifyInstalled) {
                cfg.zoomManagementMode = zoomifyMode ? "pvp-tweaks" : "zoomify";
                // When switching TO pvp-tweaks mode, ensure zoom is enabled
                if ("pvp-tweaks".equals(cfg.zoomManagementMode)) {
                    cfg.zoomEnabled = true;
                }
                PvpTweaksConfig.save();
                refresh();
            }
        }));
        y += 28;

        // ── Zoomify not installed indicator ──────────────────────────────
        if (!zoomifyInstalled) {
            ModernButtonWidget btn = new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("\u00a78Zoomify (Zoom) (not installed)"), () -> {});
            btn.active = false;
            addRenderableWidget(btn);
            y += 28;
        }

        // ── ZOOMIFY MODE: show Zoomify controls only ─────────────────────
        if (zoomifyMode) {
            addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("\u00a7bOpen Zoomify Settings..."), () -> openZoomifySettings()));
            y += 28;

            // pvp tweaks zoom is completely disabled - show a grayed button
            ModernButtonWidget disabledZoom = new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("\u00a78PvP Tweaks Zoom: DISABLED"), () -> {});
            disabledZoom.active = false;
            addRenderableWidget(disabledZoom);
            y += 35;

        } else {
            // ── PVP TWEAKS MODE: show full pvp-tweaks zoom controls ──────
            addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("Zoom Enabled: " + (cfg.zoomEnabled ? "ON" : "OFF")), () -> {
                cfg.zoomEnabled = !cfg.zoomEnabled;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 28;

            String keyName = PvpTweaksClient.zoomKeyBinding.getTranslatedKeyMessage().getString();
            Component btnText = listeningForKeybind
                    ? Component.literal("> \u00a7e???\u00a7r <")
                    : Component.literal("Zoom Key: " + keyName);
            addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20, btnText, () -> {
                listeningForKeybind = true;
                refresh();
            }));
            y += 28;

            addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("Zoom Mode: " + (cfg.zoomToggle ? "TOGGLE" : "HOLD")), () -> {
                cfg.zoomToggle = !cfg.zoomToggle;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 28;

            addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Component.literal("Smooth Camera: " + (cfg.zoomSmoothCamera ? "ON" : "OFF")), () -> {
                cfg.zoomSmoothCamera = !cfg.zoomSmoothCamera;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 35;

            // If Zoomify is also installed in pvp-tweaks mode, offer its settings
            if (zoomifyInstalled) {
                addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20,
                        Component.literal("\u00a77Open Zoomify Settings..."), () -> openZoomifySettings()));
                y += 28;
            }
        }

        // ── Back button ──────────────────────────────────────────────────
        addRenderableWidget(new ModernButtonWidget(cx - 100, y, 200, 20, Component.literal("Back"), () -> {
            minecraft.setScreen(parent);
        }));
    }
    private void openZoomifySettings() {
        // Close the GUI and send /zoomify chat command to open Zoomify's settings
        minecraft.setScreen(null);
        if (minecraft.player != null) {
            minecraft.player.connection.sendCommand("zoomify");
        }
    }

    private void refresh() {
        this.clearWidgets();
        this.init();
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (listeningForKeybind) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
                PvpTweaksClient.zoomKeyBinding.setKey(InputConstants.UNKNOWN);
            } else {
                PvpTweaksClient.zoomKeyBinding.setKey(InputConstants.getKey(input));
            }
            net.minecraft.client.KeyMapping.resetMapping();
            minecraft.options.save();
            listeningForKeybind = false;
            refresh();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        boolean zoomifyInstalled = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify");
        boolean zoomifyMode = "zoomify".equals(PvpTweaksConfig.get().zoomManagementMode) && zoomifyInstalled;

        context.drawCenteredString(font,
                Component.literal("\u00a7lZoom Settings"), width / 2, 20, 0xFFFFFF);

        if (zoomifyMode) {
            context.drawCenteredString(font,
                    Component.literal("\u00a77Zoomify is managing zoom. PvP Tweaks zoom is OFF."),
                    width / 2, 38, 0xAAAAAA);
        } else {
            context.drawCenteredString(font,
                    Component.literal("\u00a77Scroll wheel while zooming changes zoom level."),
                    width / 2, 38, 0xAAAAAA);
            if (zoomifyInstalled) {
                context.drawCenteredString(font,
                        Component.literal("\u00a76Tip: Disable zoom in Zoomify settings to avoid FOV conflicts."),
                        width / 2, 50, 0xAAAAAA);
            }
        }
    }
}
