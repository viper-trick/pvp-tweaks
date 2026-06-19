package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class FullbrightScreen extends Screen {
    private final Screen parent;
    private final java.util.IdentityHashMap<ClickableWidget, String> tooltips = new java.util.IdentityHashMap<>();

    public FullbrightScreen(Screen parent) {
        super(Text.literal("Fullbright Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int cx = width / 2;
        int y = height / 2 - 90;

        boolean gammaUtilsInstalled = FabricLoader.getInstance().isModLoaded("gammautils");
        boolean gammaUtilsMode = "gammautils".equals(cfg.fullbrightManagementMode) && gammaUtilsInstalled;

        String modeLabel;
        if (!gammaUtilsInstalled) {
            modeLabel = "Fullbright Manager: pvp tweaks (default)";
        } else {
            modeLabel = "Fullbright Manager: " + (gammaUtilsMode ? "\u00a7bGamma Utils (Fullbright)" : "pvp tweaks (default)");
        }

        addTooltipped(cx - 100, y, 200, 20, modeLabel,
            "Switch between Gamma Utils and built-in fullbright", () -> {
            if (gammaUtilsInstalled) {
                cfg.fullbrightManagementMode = gammaUtilsMode ? "pvp-tweaks" : "gammautils";
                PvpTweaksConfig.save();
                refresh();
            }
        });
        y += 28;

        if (!gammaUtilsInstalled) {
            var btn = addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("\u00a78Gamma Utils (Fullbright) (not installed)"), () -> {}));
            btn.active = false;
            tooltips.put(btn, "Gamma Utils mod is not installed");
            y += 28;
        }

        if (gammaUtilsMode) {
            addTooltipped(cx - 100, y, 200, 20, "\u00a7bOpen Gamma Utils Settings...",
                "Open Gamma Utils configuration screen", () -> openGammaUtilsSettings());
            y += 28;

            var disabledFB = addDrawableChild(new ModernButtonWidget(cx - 100, y, 200, 20,
                    Text.literal("\u00a78PvP Tweaks Fullbright: DISABLED"), () -> {}));
            disabledFB.active = false;
            tooltips.put(disabledFB, "Gamma Utils is managing fullbright");
            y += 35;

        } else {
            addTooltipped(cx - 100, y, 200, 20,
                "Fullbright: " + (cfg.fullbright ? "\u00a7aON" : "\u00a77OFF"),
                "Toggle night vision fullbright mode", () -> {
                cfg.fullbright = !cfg.fullbright;
                PvpTweaksConfig.save();
                refresh();
            });
            y += 28;

            addSlider(cx - 100, y, 200, "Gamma",
                (int)(cfg.fullbrightGamma * 100), 100, 1500, 100,
                v -> { cfg.fullbrightGamma = v.floatValue() / 100f; PvpTweaksConfig.save(); },
                "Brightness level when fullbright is on (100-1500%)");
            y += 35;

            if (gammaUtilsInstalled) {
                addTooltipped(cx - 100, y, 200, 20, "\u00a77Open Gamma Utils Settings...",
                    "Open Gamma Utils configuration (currently disabled by PvP Tweaks)", () -> openGammaUtilsSettings());
                y += 28;
            }
        }

        addTooltipped(cx - 100, y, 200, 20, "Back",
            "Return to PvP Tweaks Hub", () -> client.setScreen(parent));
    }

    private void addTooltipped(int x, int y, int w, int h, String label, String tip, Runnable action) {
        var btn = addDrawableChild(new ModernButtonWidget(x, y, w, h, Text.literal(label), action));
        tooltips.put(btn, tip);
    }

    private void addSlider(int x, int y, int w, String label, double val, double min, double max, double defVal,
                           java.util.function.Consumer<Double> setter, String tip) {
        var slider = addDrawableChild(new CustomSliderWidget(x, y, w, 20, label, val, min, max, true, setter));
        tooltips.put(slider, tip);
    }

    private void openGammaUtilsSettings() {
        try {
            Class<?> autoConfig = Class.forName("me.shedaniel.autoconfig.AutoConfig");
            Class<?> modConfig = Class.forName("io.github.sjouwer.gammautils.config.ModConfig");
            java.lang.reflect.Method getScreen = autoConfig.getMethod("getConfigScreen", Class.class, Screen.class);
            Object result = getScreen.invoke(null, modConfig, this);
            if (result instanceof Screen) {
                client.setScreen((Screen) result);
                return;
            }
            if (result instanceof java.util.function.Supplier) {
                Object supplied = ((java.util.function.Supplier<?>) result).get();
                if (supplied instanceof Screen) {
                    client.setScreen((Screen) supplied);
                    return;
                }
            }
            if (result instanceof java.util.Optional) {
                var ref = new Object() { Screen screen = null; };
                ((java.util.Optional<?>) result).ifPresent(s -> {
                    if (s instanceof Screen) ref.screen = (Screen) s;
                });
                if (ref.screen != null) {
                    client.setScreen(ref.screen);
                    return;
                }
            }
            if (client.player != null)
                client.player.sendMessage(Text.literal("§c[PvP Tweaks] AutoConfig.getConfigScreen returned unexpected type: " + result.getClass().getName()), false);
        } catch (Exception e) {
            if (client.player != null)
                client.player.sendMessage(Text.literal("§c[PvP Tweaks] Error opening Gamma Utils settings: " + e.getClass().getSimpleName() + ": " + e.getMessage()), false);
            try {
                Class<?> apiImpl = Class.forName("io.github.sjouwer.gammautils.config.ModMenuApiImpl");
                Object instance = apiImpl.getDeclaredConstructor().newInstance();
                java.lang.reflect.Method getFactory = apiImpl.getMethod("getModConfigScreenFactory");
                Object factory = getFactory.invoke(instance);
                java.lang.reflect.Method create = factory.getClass().getMethod("create", Screen.class);
                client.setScreen((Screen) create.invoke(factory, this));
            } catch (Exception e2) {
                if (client.player != null)
                    client.player.sendMessage(Text.literal("§c[PvP Tweaks] Fallback also failed: " + e2.getClass().getSimpleName() + ": " + e2.getMessage()), false);
            }
        }
    }

    private void refresh() {
        this.clearChildren();
        this.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        boolean gammaUtilsInstalled = FabricLoader.getInstance().isModLoaded("gammautils");
        boolean gammaUtilsMode = "gammautils".equals(PvpTweaksConfig.get().fullbrightManagementMode) && gammaUtilsInstalled;

        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("\u00a7lFullbright Settings"), width / 2, 20, 0xFFFFFF);

        if (gammaUtilsMode) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("\u00a77Gamma Utils is managing fullbright. PvP Tweaks fullbright is OFF."),
                    width / 2, 38, 0xAAAAAA);
        } else {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("\u00a77Adjust brightness for night vision. Values above 100% boost gamma."),
                    width / 2, 38, 0xAAAAAA);
            if (gammaUtilsInstalled) {
                context.drawCenteredTextWithShadow(textRenderer,
                        Text.literal("\u00a76Tip: Disable fullbright in Gamma Utils to avoid conflicts."),
                        width / 2, 50, 0xAAAAAA);
            }
        }

        ClickableWidget hovered = null;
        for (ClickableWidget cw : tooltips.keySet()) {
            if (cw.isHovered()) {
                hovered = cw;
                break;
            }
        }
        if (hovered != null) {
            renderTooltip(context, tooltips.get(hovered), mouseX, mouseY);
        }
    }

    private void renderTooltip(DrawContext ctx, String text, int mx, int my) {
        int tw = textRenderer.getWidth(text);
        int tx = Math.max(3, Math.min(mx - tw / 2, width - tw - 3));
        int ty = Math.max(3, my - 22);
        ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + textRenderer.fontHeight + 2, 0xC0202020);
        ctx.drawTextWithShadow(textRenderer, Text.literal(text), tx, ty, 0xFFFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
