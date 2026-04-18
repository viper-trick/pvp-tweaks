package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import net.minecraft.client.sound.PositionedSoundInstance;

import java.util.ArrayList;
import java.util.List;

public class DurabilityHudRenderer {
    private static long lastAlertTime = 0;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.durabilityHudEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        List<ItemStack> items = new ArrayList<>();
        if (cfg.durabilityHudShowArmor) {
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            for (EquipmentSlot slot : slots) {
                ItemStack stack = client.player.getEquippedStack(slot);
                if (stack != null && !stack.isEmpty()) items.add(stack);
            }
        }
        if (cfg.durabilityHudShowMainHand) {
            ItemStack stack = client.player.getMainHandStack();
            if (stack != null && !stack.isEmpty()) items.add(stack);
        }
        if (cfg.durabilityHudShowOffHand) {
            ItemStack stack = client.player.getOffHandStack();
            if (stack != null && !stack.isEmpty()) items.add(stack);
        }

        if (items.isEmpty()) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        int x = (int) (width * (cfg.durabilityHudX / 100.0f));
        int y = (int) (height * (cfg.durabilityHudY / 100.0f));

        int offset = 0;
        for (ItemStack stack : items) {
            int ix = x;
            int iy = y;
            if ("vertical".equals(cfg.durabilityHudAlign)) iy += offset;
            else ix += offset;

            // Background slot
            if (cfg.durabilityHudBackground) {
                context.fill(ix, iy, ix + 16, iy + 16, 0x55000000);
                // Simple frame
                context.fill(ix, iy, ix + 16, iy + 1, 0x88FFFFFF);
                context.fill(ix, iy + 15, ix + 16, iy + 16, 0x88000000);
                context.fill(ix, iy, ix + 1, iy + 16, 0x88FFFFFF);
                context.fill(ix + 15, iy, ix + 16, iy + 16, 0x88000000);
            }

            // Low durability check
            boolean low = stack.isDamageable() && stack.getDamage() > (stack.getMaxDamage() * 0.9);
            boolean blink = cfg.durabilityHudLowAlert && low && (System.currentTimeMillis() % 1000 < 500);

            if (blink) {
                context.fill(ix, iy, ix + 16, iy + 16, 0x66FF0000);
                // Trigger sound once every 5 seconds if low
                if (System.currentTimeMillis() - lastAlertTime >= 5000) {
                    playSound(cfg.soundDurabilityLow);
                    lastAlertTime = System.currentTimeMillis();
                }
            }

            context.drawItem(stack, ix, iy);
            context.drawStackOverlay(client.textRenderer, stack, ix, iy);

            if (cfg.durabilityHudShowExact && stack.isDamageable()) {
                int dur = stack.getMaxDamage() - stack.getDamage();
                String text = String.valueOf(dur);
                int col = low ? 0xFFFF5555 : 0xFFFFFFFF;
                int textWidth = client.textRenderer.getWidth(text);
                // Center text above the 16x16 item slot
                context.drawTextWithShadow(client.textRenderer, text, ix + 8 - textWidth / 2, iy - 10, col);
            }

            offset += 20;
        }
    }

    private static void playSound(SoundProfile p) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        SoundEvent ev = null;
        if (p.isDefault()) {
            ev = net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value();
        } else if (p.isCustom()) {
            Identifier id = com.pvptweaks.sound.CustomSoundManager.registerCustomSound(p.customPath);
            if (id != null) ev = SoundEvent.of(id);
        } else if (p.isPreset()) {
            Identifier id = Identifier.tryParse(p.presetId);
            if (id != null) ev = SoundEvent.of(id);
        }

        if (ev != null) {
            mc.getSoundManager().play(PositionedSoundInstance.master(ev, 1.0f, 1.0f));
        }
    }
}
