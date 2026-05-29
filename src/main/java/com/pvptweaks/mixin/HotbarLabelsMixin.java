package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HotbarLabelsMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMainHud(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", shift = At.Shift.AFTER), require = 0)
    private void pvptweaks$hotbarLabels(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        String mode = cfg.hotbarSlotLabelMode;
        if ("off".equals(mode)) return;

        int centerX = context.getScaledWindowWidth() / 2;
        int slotY = context.getScaledWindowHeight() - 16 - 3 - 14;
        TextRenderer textRenderer = client.textRenderer;

        for (int slot = 0; slot < 9; slot++) {
            int slotX = centerX - 90 + slot * 20 + 10;
            Text label;
            if ("numbers".equals(mode)) {
                label = Text.literal(String.valueOf(slot + 1));
            } else if ("both".equals(mode)) {
                Text keyText = client.options.hotbarKeys[slot].getBoundKeyLocalizedText();
                label = Text.literal((slot + 1) + " ").append(keyText);
            } else {
                label = client.options.hotbarKeys[slot].getBoundKeyLocalizedText();
            }

            int textWidth = textRenderer.getWidth(label);
            context.drawText(textRenderer, label, slotX - textWidth / 2, slotY, 0xFFFFFFFF, true);
        }
    }
}
