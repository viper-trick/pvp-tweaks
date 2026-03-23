package com.pvptweaks.mixin;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me.shedaniel.clothconfig2.gui.AbstractConfigScreen", remap = false)
public class ClothConfigApplyMixin {

    @Inject(method = "init()V", at = @At("TAIL"), remap = false, require = 0)
    private void pvptweaks$addApplyButton(CallbackInfo ci) {
        me.shedaniel.clothconfig2.gui.AbstractConfigScreen self =
            (me.shedaniel.clothconfig2.gui.AbstractConfigScreen)(Object) this;

        // מצא את כפתור ה-Save & Quit ועשה כפתור Apply לידו
        ButtonWidget applyBtn = ButtonWidget.builder(
            Text.literal("\u00a7aApply"),
            btn -> {
                try {
                    // קרא ל-saveAll (private) דרך reflection
                    java.lang.reflect.Method saveAll = self.getClass().getDeclaredMethod("saveAll", boolean.class);
                    saveAll.setAccessible(true);
                    saveAll.invoke(self, false);

                    // reload resources (כמו F3+T)
                    net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
                    if (mc != null) mc.reloadResources();
                } catch (Exception e) {
                    com.pvptweaks.PvpTweaksMod.LOGGER.error("[PVP Tweaks] Apply failed: {}", e.getMessage());
                }
            }
        ).dimensions(self.width / 2 - 155, self.height - 26, 70, 20).build();

        // addDrawableChild is protected — call via reflection
        try {
            java.lang.reflect.Method add = net.minecraft.client.gui.screen.Screen.class.getDeclaredMethod("addDrawableChild", net.minecraft.client.gui.Element.class);
            add.setAccessible(true);
            add.invoke(self, applyBtn);
        } catch (Exception ex) { com.pvptweaks.PvpTweaksMod.LOGGER.error("[PVP Tweaks] addDrawableChild failed: {}", ex.getMessage()); }
    }
}
