package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSearchScreen extends Screen {

    private final Screen originalParent;
    private EditBox searchField;
    private List<String> allItems;
    private List<String> filtered = new ArrayList<>();
    
    private int scrollOffset = 0;
    private static final int ROW_H  = 20;
    private static final int LIST_Y = 75;

    private final boolean forBackgrounds;

    public ItemSearchScreen(Screen parent, boolean forBackgrounds) {
        super(Component.literal(forBackgrounds ? "Add Custom Background Item" : "Add Custom Item"));
        this.originalParent = parent;
        this.forBackgrounds = forBackgrounds;
    }

    @Override
    protected void init() {
        allItems = BuiltInRegistries.ITEM.stream()
            .map(b -> BuiltInRegistries.ITEM.getKey(b).toString())
            .sorted()
            .collect(Collectors.toList());
        filtered = new ArrayList<>(allItems);

        searchField = new EditBox(font, width / 2 - 150, 45, 300, 16, Component.empty());
        searchField.setHint(Component.literal("Search items (e.g. netherite_sword)..."));
        searchField.setResponder(q -> {
            String low = q.toLowerCase();
            filtered = allItems.stream()
                .filter(id -> id.contains(low))
                .collect(Collectors.toList());
            scrollOffset = 0;
        });
        addRenderableWidget(searchField);

        addRenderableWidget(new ModernButtonWidget(width / 2 - 50, height - 30, 100, 20, Component.literal("Done"), () -> minecraft.setScreen(originalParent)));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int maxRows = (height - LIST_Y - 40) / ROW_H;
        scrollOffset = (int) Mth.clamp(scrollOffset - (int)vy, 0, Math.max(0, filtered.size() - maxRows));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        int maxRows = (height - LIST_Y - 40) / ROW_H;
        if (click.x() >= width / 2 + 100 && click.x() <= width / 2 + 145 && click.y() >= LIST_Y && click.y() < LIST_Y + maxRows * ROW_H) {
            int i = (int)((click.y() - LIST_Y) / ROW_H) + scrollOffset;
            if (i >= 0 && i < filtered.size()) {
                String id = filtered.get(i);
                PvpTweaksConfig cfg = PvpTweaksConfig.get();
                if (forBackgrounds) {
                    if (!cfg.customItemBackgrounds.containsKey(id)) {
                        cfg.customItemBackgrounds.put(id, 0x80FF0000);
                        PvpTweaksConfig.save();
                    }
                } else {
                    if (!cfg.customItemScales.containsKey(id)) {
                        cfg.customItemScales.put(id, 100);
                        PvpTweaksConfig.save();
                    }
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);
        
        ctx.centeredText(font, Component.literal("\u00a7lADD CUSTOM ITEM"), width / 2, 15, UiPalette.ACCENT_BLUE);
        ctx.centeredText(font, Component.literal("MouseButtonEvent the + button to add an item to " + (forBackgrounds ? "Item Backgrounds" : "Item Sizes")), width / 2, 30, UiPalette.TEXT_SECONDARY);

        int maxRows = (height - LIST_Y - 40) / ROW_H;
        RenderUtils.drawRoundedRect(ctx, width / 2 - 150, LIST_Y, 300, maxRows * ROW_H, 4, 0x50000000);
        RenderUtils.drawOutline(ctx, width / 2 - 150, LIST_Y, 300, maxRows * ROW_H, 1, 0x30FFFFFF);

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        for (int i = 0; i < maxRows; i++) {
            int idx = i + scrollOffset;
            if (idx >= filtered.size()) break;
            String id = filtered.get(idx);
            int y = LIST_Y + i * ROW_H;
            
            boolean rowHovered = mx >= width / 2 - 150 && mx <= width / 2 + 150 && my >= y && my < y + ROW_H;
            boolean plusHovered = mx >= width / 2 + 100 && mx <= width / 2 + 145 && my >= y && my < y + ROW_H;
            
            if (rowHovered) {
                RenderUtils.drawRoundedRect(ctx, width / 2 - 150, y, 300, ROW_H, 2, 0x20FFFFFF);
            }
            
            boolean added = forBackgrounds ? cfg.customItemBackgrounds.containsKey(id) : cfg.customItemScales.containsKey(id);
            ctx.text(font, Component.literal(id), width / 2 - 145, y + (ROW_H - 8) / 2, added ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY);
            
            if (added) {
                ctx.text(font, Component.literal("\u2714"), width / 2 + 120, y + (ROW_H - 8) / 2, 0xFF55FF55);
            } else {
                int btnColor = plusHovered ? UiPalette.ACCENT_BLUE : 0xFFFFFFFF;
                RenderUtils.drawRoundedRect(ctx, width / 2 + 115, y + 2, 25, ROW_H - 4, 3, plusHovered ? 0x4000A3FF : 0x20FFFFFF);
                ctx.centeredText(font, Component.literal("+"), width / 2 + 127, y + (ROW_H - 8) / 2, btnColor);
            }
        }

        ctx.text(font, Component.literal("\u00a78" + filtered.size() + " items found"), width / 2 - 150, height - 45, UiPalette.TEXT_SECONDARY);
        super.extractRenderState(ctx, mx, my, delta);
    }
}
