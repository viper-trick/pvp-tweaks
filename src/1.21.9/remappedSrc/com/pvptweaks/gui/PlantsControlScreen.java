package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

/**
 * Plants Control screen — ported from GRM (user's own code, client-side only).
 *
 * Two tabs: Hide (hides rendering) and Outline (hides selection outline).
 * Features: search, scroll, Select All / Deselect All, per-block toggles.
 */
public class PlantsControlScreen extends Screen {

    private final Screen parent;

    private EditBox searchField;
    private List<String> allBlocks;
    private List<String> filtered = new ArrayList<>();

    private int tab = 0; // 0 = Hide, 1 = Outline
    private int scrollOffset = 0;
    private String statusMsg = "";

    private static final int ROW_H  = 14;
    private static final int LIST_Y = 80;

    // Keywords that suggest a block is a plant (not a full solid block)
    private static final String[] PLANT_KEYWORDS = {
        "fern", "flower", "rose", "dandelion", "poppy", "orchid",
        "allium", "tulip", "daisy", "lily", "vine", "bush", "leaves",
        "seagrass", "kelp", "coral", "mushroom", "moss", "azalea",
        "sapling", "bamboo", "sugarcane", "cactus", "weed", "plant",
        "spore", "warped_roots", "nether_roots", "crimson_roots", "sunflower",
        "lilac", "peony", "dead_bush", "tall_grass", "large_fern",
        "wheat", "carrot", "potato", "beet", "melon_stem", "pumpkin_stem",
        "nether_wart", "sweet_berry", "glow_lichen", "hanging_roots",
        "cave_vines", "twisting_vines", "weeping_vines", "pitcher", "torchflower",
        "short_grass", "wildflowers", "bush"
    };

    // Blocks to EXCLUDE even if they match a keyword (full blocks that are not plants)
    private static final java.util.Set<String> EXCLUDED = java.util.Set.of(
        "minecraft:grass_block",
        "minecraft:mycelium",
        "minecraft:podzol",
        "minecraft:dirt_path",
        "minecraft:coarse_dirt"
    );

    public PlantsControlScreen(Screen parent) {
        super(Component.literal("Plants Control"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        allBlocks = BuiltInRegistries.BLOCK.stream()
            .map(b -> BuiltInRegistries.BLOCK.getKey(b).toString())
            .filter(id -> {
                if (EXCLUDED.contains(id)) return false;
                String low = id.toLowerCase();
                for (String kw : PLANT_KEYWORDS) if (low.contains(kw)) return true;
                return false;
            })
            .sorted()
            .collect(Collectors.toList());
        filtered = new ArrayList<>(allBlocks);

        // Tab buttons
        addRenderableWidget(Button.builder(Component.literal("🌿 Hide Rendering"),
            b -> { tab = 0; scrollOffset = 0; }).bounds(4, 26, 155, 18).build());
        addRenderableWidget(Button.builder(Component.literal("🔲 Hide Outline"),
            b -> { tab = 1; scrollOffset = 0; }).bounds(163, 26, 155, 18).build());

        // Search
        searchField = new EditBox(font, width / 2 - 150, 48, 300, 16, Component.empty());
        searchField.setHint(Component.literal("Search plants..."));
        searchField.setResponder(q -> {
            String low = q.toLowerCase();
            filtered = allBlocks.stream()
                .filter(id -> id.contains(low))
                .collect(Collectors.toList());
            scrollOffset = 0;
        });
        addRenderableWidget(searchField);

        // Select All / Deselect All
        addRenderableWidget(Button.builder(Component.literal("✅ Select All"),
            b -> {
                PvpTweaksConfig cfg = PvpTweaksConfig.get();
                for (String id : filtered) {
                    if (tab == 0) cfg.hiddenPlants.add(id);
                    else cfg.outlinePlants.add(id);
                }
                PvpTweaksConfig.save();
                if (minecraft != null && minecraft.levelRenderer != null) minecraft.levelRenderer.allChanged();
                statusMsg = "§aSelected " + filtered.size() + " plants";
            }).bounds(4, LIST_Y - 20, 120, 16).build());

        addRenderableWidget(Button.builder(Component.literal("❌ Deselect All"),
            b -> {
                PvpTweaksConfig cfg = PvpTweaksConfig.get();
                for (String id : filtered) {
                    if (tab == 0) cfg.hiddenPlants.remove(id);
                    else cfg.outlinePlants.remove(id);
                }
                PvpTweaksConfig.save();
                if (minecraft != null && minecraft.levelRenderer != null) minecraft.levelRenderer.allChanged();
                statusMsg = "§7Deselected " + filtered.size() + " plants";
            }).bounds(130, LIST_Y - 20, 120, 16).build());

        // Enable toggle
        addRenderableWidget(Button.builder(
            Component.literal(PvpTweaksConfig.get().plantsControlEnabled
                ? "✔ Plants Control: ON" : "✘ Plants Control: OFF"),
            b -> {
                PvpTweaksConfig cfg = PvpTweaksConfig.get();
                cfg.plantsControlEnabled = !cfg.plantsControlEnabled;
                PvpTweaksConfig.save();
                b.setMessage(Component.literal(cfg.plantsControlEnabled
                    ? "✔ Plants Control: ON" : "✘ Plants Control: OFF"));
                if (minecraft != null && minecraft.levelRenderer != null) minecraft.levelRenderer.allChanged();
            }
        ).bounds(4, height - 24, 180, 20).build());

        addRenderableWidget(Button.builder(Component.literal("✖ Close"),
            b -> minecraft.setScreen(parent)).bounds(width - 84, height - 24, 80, 20).build());
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hx, double vy) {
        int maxRows = (height - LIST_Y - 30) / ROW_H;
        scrollOffset = (int) Math.max(0, Math.min(Math.max(0, filtered.size() - maxRows), scrollOffset - vy * 3));
        return true;
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent click, boolean doubled) {
        int maxRows = (height - LIST_Y - 30) / ROW_H;
        if (click.y() >= LIST_Y && click.y() < LIST_Y + maxRows * ROW_H) {
            int i = (int)((click.y() - LIST_Y) / ROW_H) + scrollOffset;
            if (i >= 0 && i < filtered.size()) {
                toggleBlock(filtered.get(i));
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private void toggleBlock(String blockId) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        boolean added;
        if (tab == 0) {
            if (cfg.hiddenPlants.contains(blockId)) { cfg.hiddenPlants.remove(blockId); added = false; }
            else { cfg.hiddenPlants.add(blockId); added = true; }
        } else {
            if (cfg.outlinePlants.contains(blockId)) { cfg.outlinePlants.remove(blockId); added = false; }
            else { cfg.outlinePlants.add(blockId); added = true; }
        }
        PvpTweaksConfig.save();
        statusMsg = (added ? "§aAdded: " : "§7Removed: ") + blockId;
        if (minecraft != null && minecraft.levelRenderer != null) minecraft.levelRenderer.allChanged();
    }

    private boolean isActive(String blockId) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        return tab == 0 ? cfg.hiddenPlants.contains(blockId) : cfg.outlinePlants.contains(blockId);
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredString(font,
            Component.literal("§2🌿 §lPlants Control§r §2🌿"), width / 2, 8, 0xFFFFFFFF);

        String tabLabel = tab == 0 ? "§aHide Rendering" : "§bHide Outline";
        ctx.drawString(font,
            Component.literal("§7" + tabLabel + "  §8— Click a plant to toggle"),
            4, LIST_Y - 32, 0xFFAAAAAA);

        if (!statusMsg.isEmpty())
            ctx.drawString(font, Component.literal(statusMsg), 4, LIST_Y - 22, 0xFFFFFFFF);

        int maxRows = (height - LIST_Y - 30) / ROW_H;
        ctx.fill(4, LIST_Y, width - 4, LIST_Y + maxRows * ROW_H, 0x88000000);

        for (int i = 0; i < maxRows; i++) {
            int idx = i + scrollOffset;
            if (idx >= filtered.size()) break;
            String id = filtered.get(idx);
            boolean active = isActive(id);
            int y = LIST_Y + i * ROW_H;
            if (active) ctx.fill(4, y, width - 4, y + ROW_H, 0x882244AA);
            ctx.drawString(font,
                Component.literal((active ? "§a✔ " : "§7  ") + id),
                8, y + 2, 0xFFFFFFFF);
        }

        ctx.drawString(font,
            Component.literal("§8" + filtered.size() + " plants  |  scroll to browse"),
            4, height - 38, 0xFFAAAAAA);
    }

    @Override public void onClose() { minecraft.setScreen(parent); }
}
