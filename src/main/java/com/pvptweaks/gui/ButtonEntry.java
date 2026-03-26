package com.pvptweaks.gui;

import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.Collections;
import java.util.List;

public class ButtonEntry extends TextListEntry {

    private final ButtonWidget button;

    public ButtonEntry(Text label, Runnable onClick) {
        super(Text.empty(), Text.empty());
        this.button = ButtonWidget.builder(label, btn -> onClick.run())
            .dimensions(0, 0, 200, 20).build();
    }

    @Override
    public void render(DrawContext ctx, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        button.setX(x + entryWidth / 2 - 100);
        button.setY(y + 2);
        button.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public int getItemHeight() { return 26; }

    @Override
    public List<? extends net.minecraft.client.gui.Element> children() {
        return Collections.singletonList(button);
    }

    @Override
    public List<? extends net.minecraft.client.gui.Selectable> narratables() {
        return Collections.singletonList(button);
    }
}
