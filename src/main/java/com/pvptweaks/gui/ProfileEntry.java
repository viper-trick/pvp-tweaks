package com.pvptweaks.gui;

import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Cloth Config entry that shows a small Delete button and a larger Load button on the same line.
 */
public class ProfileEntry extends TextListEntry {

    private final ButtonWidget deleteButton;
    private final ButtonWidget loadButton;

    public ProfileEntry(String profileName, Runnable onDelete, Runnable onLoad) {
        super(Text.literal(profileName), Text.empty());
        
        this.deleteButton = ButtonWidget.builder(Text.literal("\u00a7c[-]"), b -> onDelete.run())
            .dimensions(0, 0, 30, 20)
            .tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("Delete Profile")))
            .build();
            
        this.loadButton = ButtonWidget.builder(Text.literal("\u00a7b\u25b6 " + profileName + "  \u00a77[Load]"), b -> onLoad.run())
            .dimensions(0, 0, 160, 20)
            .build();
    }

    @Override
    public void render(DrawContext ctx, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        int mid = x + entryWidth / 2;
        
        // Position buttons side-by-side
        deleteButton.setX(mid - 100);
        deleteButton.setY(y + 2);
        
        loadButton.setX(mid - 65);
        loadButton.setY(y + 2);
        
        deleteButton.render(ctx, mouseX, mouseY, delta);
        loadButton.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public int getItemHeight() { return 26; }

    @Override
    public List<? extends net.minecraft.client.gui.Element> children() {
        return Arrays.asList(deleteButton, loadButton);
    }

    @Override
    public List<? extends net.minecraft.client.gui.Selectable> narratables() {
        return Arrays.asList(deleteButton, loadButton);
    }
}
