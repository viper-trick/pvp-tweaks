package com.pvptweaks.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ButtonEntry extends AbstractConfigListEntry<Object> {

    private final ButtonWidget button;

    public ButtonEntry(Text buttonLabel, Runnable onClick) {
        super(Text.empty(), false);
        this.button = ButtonWidget.builder(buttonLabel, btn -> onClick.run())
            .dimensions(0, 0, 150, 20).build();
    }

    @Override
    public Object getValue() { return null; }

    @Override
    public Optional<Object> getDefaultValue() { return Optional.empty(); }

    @Override
    public boolean isEdited() { return false; }

    @Override
    public void save() {}

    @Override
    public List<? extends net.minecraft.client.gui.Element> children() {
        return List.of(button);
    }

    @Override
    public List<? extends net.minecraft.client.gui.Selectable> selectableChildren() {
        return List.of(button);
    }

    @Override
    public void render(DrawContext ctx, int index, int y, int x, int entryWidth, int entryHeight,
                       int mouseX, int mouseY, boolean hovered, float delta) {
        button.setX(x + entryWidth / 2 - 75);
        button.setY(y);
        button.setWidth(150);
        button.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public int getItemHeight() { return 24; }
}
