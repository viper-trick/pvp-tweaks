package com.pvptweaks.mixin;

import com.pvptweaks.config.PvpTweaksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public class DrawContextMixin {

    private static boolean shouldShowBackground(String mode) {
        if ("off".equals(mode)) return false;
        if ("both".equals(mode)) return true;
        boolean inScreen = Minecraft.getInstance().screen != null;
        return "inventory".equals(mode) == inScreen;
    }

    private void drawBackground(ItemStack stack, int x, int y) {
        if (stack == null || stack.isEmpty()) return;
        PvpTweaksConfig cfg = PvpTweaksConfig.get();
        if (!shouldShowBackground(cfg.itemBackgroundMode)) return;
        Item item = stack.getItem();
        String itemPath = Integer.toString(net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(item));

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
            GuiGraphicsExtractor context = (GuiGraphicsExtractor) (Object) this;
            context.fill(x, y, x + 16, y + 16, color);
        }
    }

    @Inject(method = "item(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void onDrawItem(ItemStack stack, int x, int y, CallbackInfo ci) {
        drawBackground(stack, x, y);
    }

    @Inject(method = "item(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void onDrawItemWithSeed(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        drawBackground(stack, x, y);
    }

    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void onDrawItemWithEntity(LivingEntity entity, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        drawBackground(stack, x, y);
    }
}
