package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemBackgroundScreen extends Screen {
    private final Screen parent;
    private int contentScroll = 0;

    public ItemBackgroundScreen(Screen parent) {
        super(Text.literal("Item Background Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        refreshWidgets();
    }

    private void refreshWidgets() {
        this.clearChildren();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int cx = width / 2;
        int y = 50 - contentScroll;
        int spacing = 30;

        addDrawableChild(new ModernButtonWidget(cx - 75, y, 150, 20, Text.literal("Totem Background: " + (cfg.totemBackgroundEnabled ? "ON" : "OFF")), () -> {
            cfg.totemBackgroundEnabled = !cfg.totemBackgroundEnabled;
            refreshWidgets();
        }));
        y += spacing;

        addDrawableChild(new ModernButtonWidget(cx - 75, y, 150, 20, Text.literal("\ud83c\udfa8 Totem Color"), () -> {
            client.setScreen(new ColorPickerScreen(this, cfg.totemBackgroundColor, color -> {
                cfg.totemBackgroundColor = color;
                PvpTweaksConfig.save();
                client.execute(() -> client.setScreen(new ItemBackgroundScreen(parent)));
            }));
        }));
        y += spacing;

        addDrawableChild(new ModernButtonWidget(cx - 75, y, 150, 20, Text.literal("Crystal Background: " + (cfg.crystalBackgroundEnabled ? "ON" : "OFF")), () -> {
            cfg.crystalBackgroundEnabled = !cfg.crystalBackgroundEnabled;
            refreshWidgets();
        }));
        y += spacing;

        addDrawableChild(new ModernButtonWidget(cx - 75, y, 150, 20, Text.literal("\ud83c\udfa8 Crystal Color"), () -> {
            client.setScreen(new ColorPickerScreen(this, cfg.crystalBackgroundColor, color -> {
                cfg.crystalBackgroundColor = color;
                PvpTweaksConfig.save();
                client.execute(() -> client.setScreen(new ItemBackgroundScreen(parent)));
            }));
        }));
        y += spacing * 1.5;

        addDrawableChild(new ModernButtonWidget(cx - 75, y, 150, 20, Text.literal("\u00a7a+ Add Custom Item"), () -> {
            client.setScreen(new ItemSearchScreen(this, true));
        }));
        y += spacing;

        List<String> customIds = new ArrayList<>(cfg.customItemBackgrounds.keySet());
        for (String id : customIds) {
            if (y > height - 40) break;
            
            String label = id.contains(":") ? id.substring(id.lastIndexOf(':') + 1) : id;
            if (label.length() > 15) label = label.substring(0, 15) + "...";
            
            addDrawableChild(new ModernButtonWidget(cx - 75, y, 100, 20, Text.literal("\ud83c\udfa8 " + label), () -> {
                client.setScreen(new ColorPickerScreen(this, cfg.customItemBackgrounds.get(id), color -> {
                    cfg.customItemBackgrounds.put(id, color);
                    PvpTweaksConfig.save();
                    client.execute(() -> client.setScreen(new ItemBackgroundScreen(parent)));
                }));
            }));
            
            addDrawableChild(new ModernButtonWidget(cx + 30, y, 45, 20, Text.literal("\u00a7cDel"), () -> {
                cfg.customItemBackgrounds.remove(id);
                refreshWidgets();
            }));
            
            y += spacing;
        }

        addDrawableChild(new ModernButtonWidget(cx - 75, height - 30, 150, 20, Text.literal("Back"), () -> {
            PvpTweaksConfig.save();
            client.setScreen(parent);
        }));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        contentScroll = Math.max(0, contentScroll - (int)(vy * 15));
        refreshWidgets();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lItem Backgrounds"), width / 2, 20, 0xFFFFFF);
    }
}
