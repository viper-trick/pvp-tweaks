package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Inject(method = "drawItem(Lnet/minecraft/item/ItemStack;III)V", at = @At("HEAD"))
    private void onDrawItem(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        if (stack == null || stack.isEmpty()) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        Item item = stack.getItem();
        String itemPath = net.minecraft.registry.Registries.ITEM.getId(item).toString();
        
        int color = 0;
        boolean drawBg = false;
        if (cfg.totemBackgroundEnabled && item == Items.TOTEM_OF_UNDYING) {
            color = cfg.totemBackgroundColor;
            drawBg = true;
        } else if (cfg.crystalBackgroundEnabled && item == Items.END_CRYSTAL) {
            color = cfg.crystalBackgroundColor;
            drawBg = true;
        } else if (cfg.customItemBackgrounds.containsKey(itemPath)) {
            color = cfg.customItemBackgrounds.get(itemPath);
            drawBg = true;
        }

        if (drawBg) {
            DrawContext context = (DrawContext) (Object) this;
            context.fill(x, y, x + 16, y + 16, color);
        }
    }
}
