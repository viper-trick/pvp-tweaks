package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class DurabilityAdjusterScreen extends Screen {

    private final Screen parent;
    private boolean showUi = true;
    private boolean dragging = false;
    private ButtonWidget doneButton;

    // Define instruction texts as constants for clarity and easier management
    private static final Text INSTRUCTION_TEXT_VISIBLE_HUD = Text.literal("\u00a7bDrag to move, Arrows to nudge.");
    private static final Text INSTRUCTION_TEXT_TAB_TOGGLE = Text.literal("\u00a77Press TAB to toggle settings buttons visibility.");

    public DurabilityAdjusterScreen(Screen parent) {
        super(Text.literal("Durability Adjuster"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = 10;
        int y = height - 26;

        // Add the "Done" button. It saves config and returns to parent screen.
        doneButton = addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> {
            PvpTweaksConfig.save(); // Save config when Done is pressed
            client.setScreen(parent);
        }).dimensions(x, y, 60, 18).build());

        x += 65;
        addDrawableChild(ButtonWidget.builder(Text.literal("Align"), b -> {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudAlign = "vertical".equals(cfg.durabilityHudAlign) ? "horizontal" : "vertical";
            PvpTweaksConfig.save();
        }).dimensions(x, y, 60, 18).build());

        x += 65;
        addDrawableChild(ButtonWidget.builder(Text.literal("Frame"), b -> {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudBackground = !cfg.durabilityHudBackground;
            PvpTweaksConfig.save();
        }).dimensions(x, y, 60, 18).build());

        x += 65;
        addDrawableChild(ButtonWidget.builder(Text.literal("Exact"), b -> {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudShowExact = !cfg.durabilityHudShowExact;
            PvpTweaksConfig.save();
        }).dimensions(x, y, 60, 18).build());

        x += 65;
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudX = 5f;
            cfg.durabilityHudY = 5f;
            PvpTweaksConfig.save();
        }).dimensions(x, y, 60, 18).build());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Alignment lines removed as requested.
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        // First check if any button was clicked
        if (showUi && super.mouseClicked(click, doubled)) {
            return true;
        }

        if (click.button() == 0) { // Left mouse button
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            // Calculate the current HUD position based on config percentages
            int hudX = (int) (width * (cfg.durabilityHudX / 100.0f));
            int hudY = (int) (height * (cfg.durabilityHudY / 100.0f));
            
            // Define a bounding box for the HUD element to start dragging.
            if (click.x() >= hudX - 10 && click.x() <= hudX + 100 && click.y() >= hudY - 10 && click.y() <= hudY + 100) {
                dragging = true;
                return true; // Consume the event
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double deltaX, double deltaY) {
        if (dragging && click.button() == 0) {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            // Update config with new position percentages as floats for precision.
            cfg.durabilityHudX = (float) Math.max(0.0, Math.min(100.0, (click.x() / (double)width) * 100.0));
            cfg.durabilityHudY = (float) Math.max(0.0, Math.min(100.0, (click.y() / (double)height) * 100.0));
            return true; // Event handled
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        if (click.button() == 0 && dragging) {
            dragging = false;
            PvpTweaksConfig.save(); // Save the new position to config when dragging stops
            return true; // Event consumed
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        // Toggle UI visibility with TAB key
        if (input.key() == GLFW.GLFW_KEY_TAB) {
            showUi = !showUi;
            if (doneButton != null) doneButton.visible = showUi;
            return true; // Event consumed
        }

        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        boolean changed = false; 

        // Adjust HUD position using arrow keys (0.1% at a time for sub-pixel precision)
        float step = 0.1f;
        if (input.key() == GLFW.GLFW_KEY_UP) {
            cfg.durabilityHudY = Math.max(0f, cfg.durabilityHudY - step);
            changed = true;
        } else if (input.key() == GLFW.GLFW_KEY_DOWN) {
            cfg.durabilityHudY = Math.min(100f, cfg.durabilityHudY + step);
            changed = true;
        } else if (input.key() == GLFW.GLFW_KEY_LEFT) {
            cfg.durabilityHudX = Math.max(0f, cfg.durabilityHudX - step);
            changed = true;
        } else if (input.key() == GLFW.GLFW_KEY_RIGHT) {
            cfg.durabilityHudX = Math.min(100f, cfg.durabilityHudX + step);
            changed = true;
        }

        if (changed) {
            PvpTweaksConfig.save(); // Save the updated position if any arrow key was pressed
            return true; // Event consumed
        }

        // Pass the event to superclass if not handled
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Show TAB toggle instruction - ALWAYS visible
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7ePress TAB to hide/show buttons"), width / 2, height - 42, 0xFFFFFFFF);

        if (showUi) {
            // Show movement instructions
            ctx.drawCenteredTextWithShadow(textRenderer, INSTRUCTION_TEXT_VISIBLE_HUD, width / 2, 10, 0xFFFFFFFF);
            
            // Draw current settings status
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            String status = String.format("Align: %s | Frame: %s | Exact: %s", 
                cfg.durabilityHudAlign, cfg.durabilityHudBackground ? "ON" : "OFF", cfg.durabilityHudShowExact ? "ON" : "OFF");
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a77" + status), width / 2, 22, 0xAAAAAA);

            super.render(ctx, mouseX, mouseY, delta);
        } else {
            // When UI is hidden: show instruction to bring it back
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a77Settings hidden."), width / 2, height - 20, 0xAAAAAA);
        }
    }

    @Override
    public boolean shouldPause() {
        // This screen should not pause the game when open
        return false;
    }
}
