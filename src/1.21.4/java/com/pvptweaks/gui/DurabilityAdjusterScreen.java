package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DurabilityAdjusterScreen extends Screen {
    private final Screen parent;
    private boolean dragging = false;
    private double dragOffsetX, dragOffsetY;
    private List<ItemStack> sampleItems = null;

    public DurabilityAdjusterScreen(Screen parent) {
        super(Text.literal("Durability Adjuster"));
        this.parent = parent;
        DurabilityHudRenderer.sampleActive = (sampleItems != null);
    }

    @Override
    public void removed() {
        super.removed();
        DurabilityHudRenderer.sampleActive = false;
    }

    @Override
    protected void init() {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int btnY = height - 35;
        addDrawableChild(new ModernButtonWidget(20, btnY, 70, 20, (Text)Text.literal("Align: " + cfg.durabilityHudAlign), () -> {
            cfg.durabilityHudAlign = "vertical".equals(cfg.durabilityHudAlign) ? "horizontal" : "vertical";
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(95, btnY, 70, 20, (Text)Text.literal("Frame: " + (cfg.durabilityHudBackground ? "ON" : "OFF")), () -> {
            cfg.durabilityHudBackground = !cfg.durabilityHudBackground;
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(170, btnY, 70, 20, (Text)Text.literal("Exact: " + (cfg.durabilityHudShowExact ? "ON" : "OFF")), () -> {
            cfg.durabilityHudShowExact = !cfg.durabilityHudShowExact;
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(width - 245, btnY, 75, 20, (Text)Text.literal(sampleItems == null ? "Armor Sample" : "\u2714 Sampled"), () -> {
            if (sampleItems == null) {
                sampleItems = createSampleItems();
                DurabilityHudRenderer.sampleActive = true;
            } else {
                sampleItems = null;
                DurabilityHudRenderer.sampleActive = false;
            }
            refresh();
        }));
        addDrawableChild(new ModernButtonWidget(width - 160, btnY, 70, 20, (Text)Text.literal("Reset"), () -> {
            cfg.durabilityHudX = 5f; cfg.durabilityHudY = 5f;
        }));
        addDrawableChild(new ModernButtonWidget(width - 85, btnY, 70, 20, (Text)Text.literal("Done"), () -> {
            PvpTweaksConfig.save(); client.setScreen(parent);
        }));
    }

    private static List<ItemStack> createSampleItems() {
        List<ItemStack> list = new ArrayList<>();
        ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
        helmet.setDamage((int)(helmet.getMaxDamage() * 0.25));
        list.add(helmet);

        ItemStack chest = new ItemStack(Items.NETHERITE_CHESTPLATE);
        chest.setDamage((int)(chest.getMaxDamage() * 0.92));
        list.add(chest);

        ItemStack legs = new ItemStack(Items.NETHERITE_LEGGINGS);
        legs.setDamage((int)(legs.getMaxDamage() * 0.15));
        list.add(legs);

        ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
        boots.setDamage((int)(boots.getMaxDamage() * 0.60));
        list.add(boots);

        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.setDamage((int)(sword.getMaxDamage() * 0.35));
        list.add(sword);

        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.setDamage((int)(shield.getMaxDamage() * 0.50));
        list.add(shield);

        return list;
    }

    private void refresh() { this.clearChildren(); this.init(); }

    private int hudItemCount() {
        return sampleItems != null ? sampleItems.size() : 6;
    }

    private int hudWidth(PvpTweaksConfig cfg) {
        if ("vertical".equals(cfg.durabilityHudAlign)) return 50;
        return hudItemCount() * 20;
    }

    private int hudHeight(PvpTweaksConfig cfg) {
        if ("vertical".equals(cfg.durabilityHudAlign)) return hudItemCount() * 20;
        return 26;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawGradientRect(context, 0, 0, width, 40, 0x88000000, 0x00000000);
        RenderUtils.drawGradientRect(context, 0, height - 50, width, 50, 0x00000000, 0x88000000);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("\u00a7lDURABILITY HUD ADJUSTER"), width / 2, 10, UiPalette.ACCENT_BLUE);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Drag to move, arrows for precise nudging"), width / 2, 22, UiPalette.TEXT_SECONDARY);
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int hudX = (int) (width * (cfg.durabilityHudX / 100.0f));
        int hudY = (int) (height * (cfg.durabilityHudY / 100.0f));
        int hw = hudWidth(cfg);
        int hh = hudHeight(cfg);
        if (dragging) RenderUtils.drawOutline(context, hudX - 5, hudY - 5, hw + 10, hh + 10, 1, UiPalette.ACCENT_BLUE);

        if (sampleItems != null) {
            renderSampleItems(context, hudX, hudY, cfg);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderSampleItems(DrawContext context, int x, int y, PvpTweaksConfig cfg) {
        int offset = 0;
        for (ItemStack stack : sampleItems) {
            int ix = x;
            int iy = y;
            if ("vertical".equals(cfg.durabilityHudAlign)) iy += offset;
            else ix += offset;

            if (cfg.durabilityHudBackground) {
                context.fill(ix, iy, ix + 16, iy + 16, 0x55000000);
                context.fill(ix, iy, ix + 16, iy + 1, 0x88FFFFFF);
                context.fill(ix, iy + 15, ix + 16, iy + 16, 0x88000000);
                context.fill(ix, iy, ix + 1, iy + 16, 0x88FFFFFF);
                context.fill(ix + 15, iy, ix + 16, iy + 16, 0x88000000);
            }

            boolean low = stack.isDamageable() && stack.getDamage() > (stack.getMaxDamage() * 0.9);
            if (low) {
                context.fill(ix, iy, ix + 16, iy + 16, 0x33FF0000);
            }

            context.drawItem(stack, ix, iy);
            context.drawStackOverlay(client.textRenderer, stack, ix, iy);

            if (cfg.durabilityHudShowExact && stack.isDamageable()) {
                int dur = stack.getMaxDamage() - stack.getDamage();
                String text = String.valueOf(dur);
                int col = low ? 0xFFFF5555 : 0xFFFFFFFF;
                int tw = client.textRenderer.getWidth(text);
                int tx, ty;
                if ("vertical".equals(cfg.durabilityHudAlign)) {
                    tx = ix + 18;
                    ty = iy + 4;
                } else {
                    tx = ix + 8 - tw / 2;
                    ty = iy - 10;
                }
                context.drawTextWithShadow(client.textRenderer, text, tx, ty, col);
            }

            offset += 20;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        int hudX = (int) (width * (cfg.durabilityHudX / 100.0f));
        int hudY = (int) (height * (cfg.durabilityHudY / 100.0f));
        int hw = hudWidth(cfg);
        int hh = hudHeight(cfg);
        if (mouseX >= hudX - 10 && mouseX <= hudX + hw + 10 && mouseY >= hudY - 10 && mouseY <= hudY + hh + 10) {
            dragging = true;
            dragOffsetX = mouseX - hudX;
            dragOffsetY = mouseY - hudY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            PvpTweaksConfig cfg = PvpTweaksConfig.get();
            cfg.durabilityHudX = (float) MathHelper.clamp(((mouseX - dragOffsetX) * 100.0) / width, 0, 95);
            cfg.durabilityHudY = (float) MathHelper.clamp(((mouseY - dragOffsetY) * 100.0) / height, 0, 95);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) { dragging = false; return super.mouseReleased(mouseX, mouseY, button); }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        float step = 0.1f;
        if (keyCode == GLFW.GLFW_KEY_UP) { cfg.durabilityHudY -= step; }
        else if (keyCode == GLFW.GLFW_KEY_DOWN) { cfg.durabilityHudY += step; }
        else if (keyCode == GLFW.GLFW_KEY_LEFT) { cfg.durabilityHudX -= step; }
        else if (keyCode == GLFW.GLFW_KEY_RIGHT) { cfg.durabilityHudX += step; }
        else if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_H) {
            PvpTweaksConfig.save(); client.setScreen(parent); return true;
        }
        cfg.durabilityHudX = MathHelper.clamp(cfg.durabilityHudX, 0, 100);
        cfg.durabilityHudY = MathHelper.clamp(cfg.durabilityHudY, 0, 100);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Empty override to remove background blur/shading
    }

    @Override public boolean shouldPause() { return false; }
}
