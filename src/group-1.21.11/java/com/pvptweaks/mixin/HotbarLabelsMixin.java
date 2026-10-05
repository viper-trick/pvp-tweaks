package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HotbarLabelsMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER), require = 0)
    private void pvptweaks$hotbarLabels(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        String mode = cfg.hotbarSlotLabelMode;
        if ("off".equals(mode)) return;

        int centerX = context.guiWidth() / 2;
        // Shifted inside the hotbar slot box to avoid XP bar overlap
        int slotY = context.guiHeight() - 21;
        Font textRenderer = client.font;

        for (int slot = 0; slot < 9; slot++) {
            int slotX = centerX - 90 + slot * 20 + 10;
            Component label;
            if ("numbers".equals(mode)) {
                label = Component.literal(String.valueOf(slot + 1));
            } else {
                String keyStr = client.options.keyHotbarSlots[slot].getTranslatedKeyMessage().getString();
                label = Component.literal(shortenKeyName(keyStr));
            }

            int textWidth = textRenderer.width(label);
            context.drawString(textRenderer, label, slotX - textWidth / 2, slotY, 0xFFFFFFFF, true);
        }
    }

    private static String shortenKeyName(String name) {
        if (name == null) return "";
        String lower = name.toLowerCase();
        
        // Mouse Buttons (e.g. "Mouse Button 4", "Button 4" -> "M4")
        if (lower.contains("mouse button ")) {
            return "M" + name.substring(name.lastIndexOf(' ') + 1);
        }
        if (lower.contains("button ")) {
            return "M" + name.substring(name.lastIndexOf(' ') + 1);
        }
        
        // Modifier & System keys
        if (lower.equals("left shift") || lower.equals("lshift")) return "LS";
        if (lower.equals("right shift") || lower.equals("rshift")) return "RS";
        if (lower.equals("left control") || lower.equals("left ctrl") || lower.equals("lcontrol") || lower.equals("lctrl")) return "LC";
        if (lower.equals("right control") || lower.equals("right ctrl") || lower.equals("rcontrol") || lower.equals("rctrl")) return "RC";
        if (lower.equals("left alt") || lower.equals("lalt")) return "LA";
        if (lower.equals("right alt") || lower.equals("ralt")) return "RA";
        if (lower.equals("caps lock") || lower.equals("caps") || lower.equals("capslock")) return "CAPS";
        if (lower.equals("space") || lower.equals("spacebar")) return "SPC";
        if (lower.equals("backspace")) return "BS";
        if (lower.equals("delete") || lower.equals("del")) return "DEL";
        if (lower.equals("insert") || lower.equals("ins")) return "INS";
        if (lower.equals("page up") || lower.equals("pgup")) return "PU";
        if (lower.equals("page down") || lower.equals("pgdn")) return "PD";
        if (lower.equals("home")) return "HM";
        if (lower.equals("end")) return "END";
        if (lower.equals("escape") || lower.equals("esc")) return "ESC";
        if (lower.equals("enter") || lower.equals("return")) return "ENT";
        if (lower.equals("tab")) return "TAB";
        
        // Num Pad
        if (lower.contains("keypad ") || lower.contains("num pad ") || lower.contains("numpad ")) {
            String last = name.substring(name.lastIndexOf(' ') + 1);
            return "N" + last;
        }

        // Arrow keys
        if (lower.equals("arrow up") || lower.equals("up arrow")) return "UP";
        if (lower.equals("arrow down") || lower.equals("down arrow")) return "DN";
        if (lower.equals("arrow left") || lower.equals("left arrow")) return "LF";
        if (lower.equals("arrow right") || lower.equals("right arrow")) return "RT";
        
        // General fallback: if the string is long, take the first 4 chars
        if (name.length() > 5) {
            return name.substring(0, 4);
        }
        
        return name;
    }
}
