package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundSearchScreen extends Screen {

    private final Screen originalParent;
    private TextFieldWidget searchField;
    private List<String> allSounds;
    private List<String> filtered = new ArrayList<>();

    private int scrollOffset = 0;
    private static final int ROW_H  = 20;
    private static final int LIST_Y = 75;

    public SoundSearchScreen(Screen parent) {
        super(Text.literal("Add Custom Sound"));
        this.originalParent = parent;
    }

    @Override
    protected void init() {
        allSounds = Registries.SOUND_EVENT.getIds().stream()
            .map(id -> id.toString())
            .sorted()
            .collect(Collectors.toList());
        filtered = new ArrayList<>(allSounds);

        searchField = new TextFieldWidget(textRenderer, width / 2 - 150, 45, 300, 16, Text.empty());
        searchField.setPlaceholder(Text.literal("Search sounds (e.g. entity.arrow.shoot)..."));
        searchField.setChangedListener(q -> {
            String low = q.toLowerCase();
            filtered = allSounds.stream()
                .filter(id -> id.contains(low))
                .collect(Collectors.toList());
            scrollOffset = 0;
        });
        addDrawableChild(searchField);

        addDrawableChild(new ModernButtonWidget(width / 2 - 50, height - 30, 100, 20, Text.literal("Done"), () -> client.setScreen(originalParent)));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int maxRows = (height - LIST_Y - 40) / ROW_H;
        scrollOffset = (int) MathHelper.clamp(scrollOffset - (int)vy, 0, Math.max(0, filtered.size() - maxRows));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int maxRows = (height - LIST_Y - 40) / ROW_H;
        if (mouseX >= width / 2 + 100 && mouseX <= width / 2 + 145 && mouseY >= LIST_Y && mouseY < LIST_Y + maxRows * ROW_H) {
            int i = (int)((mouseY - LIST_Y) / ROW_H) + scrollOffset;
            if (i >= 0 && i < filtered.size()) {
                String id = filtered.get(i);
                PvpTweaksConfig cfg = PvpTweaksConfig.get();
                if (!cfg.extraSounds.containsKey(id)) {
                    com.pvptweaks.config.SoundProfile profile = new com.pvptweaks.config.SoundProfile();
                    profile.mode = "preset";
                    profile.presetId = id;
                    cfg.extraSounds.put(id, profile);
                    PvpTweaksConfig.save();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        RenderUtils.drawGradientRect(ctx, 0, 0, width, height, UiPalette.GRADIENT_START, UiPalette.GRADIENT_END);
        RenderUtils.drawOutline(ctx, 20, 20, width - 40, height - 40, 1, UiPalette.BORDER);

        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lADD CUSTOM SOUND"), width / 2, 15, UiPalette.ACCENT_BLUE);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Click the + button to add a sound for customization"), width / 2, 30, UiPalette.TEXT_SECONDARY);

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

            boolean added = cfg.extraSounds.containsKey(id);
            ctx.drawTextWithShadow(textRenderer, Text.literal(id), width / 2 - 145, y + (ROW_H - 8) / 2, added ? UiPalette.ACCENT_BLUE : UiPalette.TEXT_PRIMARY);

            if (added) {
                ctx.drawTextWithShadow(textRenderer, Text.literal("\u2714"), width / 2 + 120, y + (ROW_H - 8) / 2, 0xFF55FF55);
            } else {
                int btnColor = plusHovered ? UiPalette.ACCENT_BLUE : 0xFFFFFFFF;
                RenderUtils.drawRoundedRect(ctx, width / 2 + 115, y + 2, 25, ROW_H - 4, 3, plusHovered ? 0x4000A3FF : 0x20FFFFFF);
                ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("+"), width / 2 + 127, y + (ROW_H - 8) / 2, btnColor);
            }
        }

        ctx.drawTextWithShadow(textRenderer, Text.literal("\u00a78" + filtered.size() + " sounds found"), width / 2 - 150, height - 45, UiPalette.TEXT_SECONDARY);
        super.render(ctx, mx, my, delta);
    }
}
