package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemBackgroundScreen extends Screen {
    private final Screen parent;
    private int contentScroll = 0;

    public ItemBackgroundScreen(Screen parent) {
        super(Component.literal("Item Background Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        refreshWidgets();
    }

    private void refreshWidgets() {
        this.clearWidgets();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int cx = width / 2;
        int y = 50 - contentScroll;
        int spacing = 30;

        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20, Component.literal("Totem Background: " + (cfg.totemBackgroundEnabled ? "ON" : "OFF")), () -> {
            cfg.totemBackgroundEnabled = !cfg.totemBackgroundEnabled;
            refreshWidgets();
        }));
        y += spacing;

        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20, Component.literal("\ud83c\udfa8 Totem Color"), () -> {
            minecraft.setScreen(new ColorPickerScreen(this, cfg.totemBackgroundColor, color -> {
                cfg.totemBackgroundColor = color;
                PvpTweaksConfig.save();
                minecraft.execute(() -> minecraft.setScreen(new ItemBackgroundScreen(parent)));
            }));
        }));
        y += spacing;

        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20, Component.literal("Crystal Background: " + (cfg.crystalBackgroundEnabled ? "ON" : "OFF")), () -> {
            cfg.crystalBackgroundEnabled = !cfg.crystalBackgroundEnabled;
            refreshWidgets();
        }));
        y += spacing;

        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20, Component.literal("\ud83c\udfa8 Crystal Color"), () -> {
            minecraft.setScreen(new ColorPickerScreen(this, cfg.crystalBackgroundColor, color -> {
                cfg.crystalBackgroundColor = color;
                PvpTweaksConfig.save();
                minecraft.execute(() -> minecraft.setScreen(new ItemBackgroundScreen(parent)));
            }));
        }));
        y += spacing;

        String[] modes = {"inventory", "outside", "both", "off"};
        String[] modeLabels = {"\u00a7eInventory", "\u00a7eOutside", "\u00a7aBoth", "\u00a77Off"};
        int mIdx = java.util.Arrays.asList(modes).indexOf(cfg.itemBackgroundMode);
        if (mIdx < 0) mIdx = 2;
        final int fMIdx = mIdx;
        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20,
            Component.literal("Show: " + modeLabels[fMIdx]), () -> {
            int next = (fMIdx + 1) % 4;
            cfg.itemBackgroundMode = modes[next];
            refreshWidgets();
        }));
        y += spacing * 1.5;

        addRenderableWidget(new ModernButtonWidget(cx - 75, y, 150, 20, Component.literal("\u00a7a+ Add Custom Item"), () -> {
            minecraft.setScreen(new ItemSearchScreen(this, true));
        }));
        y += spacing;

        List<String> customIds = new ArrayList<>(cfg.customItemBackgrounds.keySet());
        for (String id : customIds) {
            if (y > height - 40) break;
            
            String label = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1) : id;
            if (label.length() > 15) label = label.substring(0, 15) + "...";
            
            addRenderableWidget(new ModernButtonWidget(cx - 75, y, 100, 20, Component.literal("\ud83c\udfa8 " + label), () -> {
                minecraft.setScreen(new ColorPickerScreen(this, cfg.customItemBackgrounds.get(id), color -> {
                    cfg.customItemBackgrounds.put(id, color);
                    PvpTweaksConfig.save();
                    minecraft.execute(() -> minecraft.setScreen(new ItemBackgroundScreen(parent)));
                }));
            }));
            
            addRenderableWidget(new ModernButtonWidget(cx + 30, y, 45, 20, Component.literal("\u00a7cDel"), () -> {
                cfg.customItemBackgrounds.remove(id);
                refreshWidgets();
            }));
            
            y += spacing;
        }

        addRenderableWidget(new ModernButtonWidget(cx - 75, height - 30, 150, 20, Component.literal("Back"), () -> {
            PvpTweaksConfig.save();
            minecraft.setScreen(parent);
        }));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        contentScroll = Math.max(0, contentScroll - (int)(vy * 15));
        refreshWidgets();
        return true;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(font, Component.literal("\u00a7lItem Backgrounds"), width / 2, 20, 0xFFFFFF);
    }
}
