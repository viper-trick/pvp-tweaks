package com.pvptweaks.gui;

import com.pvptweaks.PvpTweaksClient;
import com.pvptweaks.PvpTweaksMod;
import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ZoomScreen extends Screen {
    private final Screen parent;
    private boolean listeningForKeybind = false;

    public ZoomScreen(Screen parent) {
        super(Text.literal("Zoom Settings"));
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

        addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20, Text.literal(modeLabel), () -> {
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
                    Text.literal("\u00a78Zoomify (Zoom) (not installed)"), () -> {});
            btn.active = false;
            addDrawableChild(btn);
            y += 28;
        }

        // ── ZOOMIFY MODE: show Zoomify controls only ─────────────────────
        if (zoomifyMode) {
            addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("\u00a7bOpen Zoomify Settings..."), () -> openZoomifySettings()));
            y += 28;

            // pvp tweaks zoom is completely disabled - show a grayed button
            ModernButtonWidget disabledZoom = new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("\u00a78PvP Tweaks Zoom: DISABLED"), () -> {});
            disabledZoom.active = false;
            addDrawableChild(disabledZoom);
            y += 35;

        } else {
            // ── PVP TWEAKS MODE: show full pvp-tweaks zoom controls ──────
            addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("Zoom Enabled: " + (cfg.zoomEnabled ? "ON" : "OFF")), () -> {
                cfg.zoomEnabled = !cfg.zoomEnabled;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 28;

            String keyName = PvpTweaksClient.zoomKeyBinding.getBoundKeyLocalizedText().getString();
            Text btnText = listeningForKeybind
                    ? Text.literal("> \u00a7e???\u00a7r <")
                    : Text.literal("Zoom Key: " + keyName);
            addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20, btnText, () -> {
                listeningForKeybind = true;
                refresh();
            }));
            y += 28;

            addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("Zoom Mode: " + (cfg.zoomToggle ? "TOGGLE" : "HOLD")), () -> {
                cfg.zoomToggle = !cfg.zoomToggle;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 28;

            addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("Smooth Camera: " + (cfg.zoomSmoothCamera ? "ON" : "OFF")), () -> {
                cfg.zoomSmoothCamera = !cfg.zoomSmoothCamera;
                PvpTweaksConfig.save();
                refresh();
            }));
            y += 35;

            // If Zoomify is also installed in pvp-tweaks mode, offer its settings
            if (zoomifyInstalled) {
                addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                        Text.literal("\u00a77Open Zoomify Settings..."), () -> openZoomifySettings()));
                y += 28;
            }
        }

        // ── Back button ──────────────────────────────────────────────────
        addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20, Text.literal("Back"), () -> {
            client.setScreen(parent);
        }));
    }
    private void openZoomifySettings() {
        // Close the GUI and send /zoomify chat command to open Zoomify's settings
        client.setScreen(null);
        if (client.player != null) {
            client.player.networkHandler.sendChatCommand("zoomify");
        }
    }

    private void refresh() {
        this.clearChildren();
        this.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForKeybind) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                PvpTweaksClient.zoomKeyBinding.setBoundKey(InputUtil.UNKNOWN_KEY);
            } else {
                PvpTweaksClient.zoomKeyBinding.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
            }
            net.minecraft.client.option.KeyBinding.updateKeysByCode();
            client.options.write();
            listeningForKeybind = false;
            refresh();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        boolean zoomifyInstalled = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("zoomify");
        boolean zoomifyMode = "zoomify".equals(PvpTweaksConfig.get().zoomManagementMode) && zoomifyInstalled;

        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a7lZoom Settings"), width / 2, 20, 0xFFFFFF);

        if (zoomifyMode) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("\u00a77Zoomify is managing zoom. PvP Tweaks zoom is OFF."),
                    width / 2, 38, 0xAAAAAA);
        } else {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("\u00a77Scroll wheel while zooming changes zoom level."),
                    width / 2, 38, 0xAAAAAA);
            if (zoomifyInstalled) {
                context.drawCenteredTextWithShadow(textRenderer,
                        Text.literal("\u00a76Tip: Disable zoom in Zoomify settings to avoid FOV conflicts."),
                        width / 2, 50, 0xAAAAAA);
            }
        }
    }
}
