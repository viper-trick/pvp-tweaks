package com.pvptweaks.gui;

import com.pvptweaks.config.PvpTweaksConfig;
import com.pvptweaks.config.SoundProfile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class DurabilityHudRenderer {
    /** When true, the real durability HUD is suppressed (sample preview active in adjuster). */
    public static boolean sampleActive = false;
    private static long lastAlertTime = 0;

    /** Tracks which items were already "low" so we can detect new low events. */
    private static final Set<String> previouslyLow = new HashSet<>();
    /** True once we've played the sound; reset when ALL previously-low items recover. */
    private static boolean alertPlayed = false;

    public static void render(GuiGraphics context, DeltaTracker tickCounter) {
        if (sampleActive) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!cfg.durabilityHudEnabled) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;

        List<ItemStack> items = new ArrayList<>();
        if (cfg.durabilityHudShowArmor) {
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            for (EquipmentSlot slot : slots) {
                ItemStack stack = client.player.getItemBySlot(slot);
                if (stack != null && !stack.isEmpty()) items.add(stack);
            }
        }
        if (cfg.durabilityHudShowMainHand) {
            ItemStack stack = client.player.getMainHandItem();
            if (stack != null && !stack.isEmpty()) items.add(stack);
        }
        if (cfg.durabilityHudShowOffHand) {
            ItemStack stack = client.player.getOffhandItem();
            if (stack != null && !stack.isEmpty()) items.add(stack);
        }

        if (items.isEmpty()) return;

        // ── Durability alert sound-once logic ────────────────────────────────
        Set<String> currentlyLow = new HashSet<>();
        for (ItemStack stack : items) {
            boolean low = stack.isDamageableItem() && stack.getDamageValue() > (stack.getMaxDamage() * 0.9);
            if (low) currentlyLow.add(stack.getItem().toString());
        }

        // Detect any item that newly entered "low" state
        boolean anyNewLow = false;
        for (String key : currentlyLow) {
            if (!previouslyLow.contains(key)) { anyNewLow = true; break; }
        }
        if (anyNewLow) alertPlayed = false;  // reset on new low item

        // If ALL items recovered from low, also reset
        if (currentlyLow.isEmpty() && !previouslyLow.isEmpty()) alertPlayed = false;

        previouslyLow.clear();
        previouslyLow.addAll(currentlyLow);
        // ─────────────────────────────────────────────────────────────────────

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();

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
                context.fill(ix, iy, ix + 16, iy + 1, 0x88FFFFFF);
                context.fill(ix, iy + 15, ix + 16, iy + 16, 0x88000000);
                context.fill(ix, iy, ix + 1, iy + 16, 0x88FFFFFF);
                context.fill(ix + 15, iy, ix + 16, iy + 16, 0x88000000);
            }

            // Low durability check
            boolean low = stack.isDamageableItem() && stack.getDamageValue() > (stack.getMaxDamage() * 0.9);
            boolean blink = cfg.durabilityHudLowAlert && low && (System.currentTimeMillis() % 1000 < 500);

            if (blink) {
                context.fill(ix, iy, ix + 16, iy + 16, 0x66FF0000);

                // Trigger alert sound
                boolean shouldPlay;
                if (cfg.durabilityAlertSoundOnce) {
                    // Play once only — stop repeating after first play
                    shouldPlay = !alertPlayed;
                } else {
                    // Original behaviour: repeat every 5 seconds
                    shouldPlay = System.currentTimeMillis() - lastAlertTime >= 5000;
                }

                if (shouldPlay) {
                    playSound(cfg.soundDurabilityLow);
                    lastAlertTime = System.currentTimeMillis();
                    alertPlayed = true;
                }
            }

            context.renderItem(stack, ix, iy);
            context.renderItemDecorations(client.font, stack, ix, iy);

            if (cfg.durabilityHudShowExact && stack.isDamageableItem()) {
                int dur = stack.getMaxDamage() - stack.getDamageValue();
                String text = String.valueOf(dur);
                int col = low ? 0xFFFF5555 : 0xFFFFFFFF;
                int tw = client.font.width(text);
                int tx, ty;
                if ("vertical".equals(cfg.durabilityHudAlign)) {
                    tx = ix + 18;
                    ty = iy + 4;
                } else {
                    tx = ix + 8 - tw / 2;
                    ty = iy - 10;
                }
                context.drawString(client.font, text, tx, ty, col);
            }

            offset += 20;
        }
    }

    private static void playSound(SoundProfile p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        SoundEvent ev = null;
        if (p.isDefault()) {
            ev = net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value();
        } else if (p.isCustom()) {
            ResourceLocation id = com.pvptweaks.sound.CustomSoundManager.registerCustomSound(p.customPath);
            if (id != null) ev = SoundEvent.createVariableRangeEvent(id);
        } else if (p.isPreset()) {
            ResourceLocation id = ResourceLocation.tryParse(p.presetId);
            if (id != null) {
                if ("pvptweaks".equals(id.getNamespace())) {
                    com.pvptweaks.sound.CustomSoundManager.injectIfMissing(id);
                }
                ev = SoundEvent.createVariableRangeEvent(id);
            }
        }

        if (ev != null) {
            SimpleSoundInstance inst = SimpleSoundInstance.forUI(ev, p.pitchPct / 100f);
            try {
                java.lang.reflect.Field f = SimpleSoundInstance.class.getSuperclass().getDeclaredField("volume");
                f.setAccessible(true);
                f.setFloat(inst, p.volumePct / 100f);
            } catch (Exception ignored) {}
            mc.getSoundManager().play(inst);
        }
    }
}
