package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void pvptweaks$addProvider(net.minecraft.resource.ResourcePackProvider[] providers, CallbackInfo ci) {
        // No-op for now, better to use the providers field if we can
    }

    @Inject(method = "providePackProfiles", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$injectDynamicPack(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<java.util.Map<String, net.minecraft.resource.ResourcePackProfile>> cir) {
        java.util.Map<String, net.minecraft.resource.ResourcePackProfile> original = cir.getReturnValue();
        java.util.Map<String, net.minecraft.resource.ResourcePackProfile> mutable = new java.util.HashMap<>(original);

        net.minecraft.resource.ResourcePackInfo info = new net.minecraft.resource.ResourcePackInfo(
            "pvptweaks:dynamic",
            net.minecraft.text.Text.literal("PVP Tweaks Dynamic"),
            net.minecraft.resource.ResourcePackSource.BUILTIN,
            java.util.Optional.empty()
        );
        net.minecraft.resource.ResourcePackPosition pos = new net.minecraft.resource.ResourcePackPosition(
            true, // required
            net.minecraft.resource.ResourcePackProfile.InsertionPosition.BOTTOM,
            true  // fixedPosition
        );
        net.minecraft.resource.ResourcePackProfile.PackFactory factory = new net.minecraft.resource.ResourcePackProfile.PackFactory() {
            @Override public net.minecraft.resource.ResourcePack open(net.minecraft.resource.ResourcePackInfo info) {
                return new com.pvptweaks.resources.PvpTweaksDynamicPack();
            }
            @Override public net.minecraft.resource.ResourcePack openWithOverlays(net.minecraft.resource.ResourcePackInfo info, net.minecraft.resource.ResourcePackProfile.Metadata metadata) {
                return open(info);
            }
        };
        
        net.minecraft.resource.ResourcePackProfile profile = net.minecraft.resource.ResourcePackProfile.create(
            info,
            factory,
            net.minecraft.resource.ResourceType.CLIENT_RESOURCES,
            pos
        );

        if (profile != null) {
            mutable.put(profile.getId(), profile);
            cir.setReturnValue(java.util.Collections.unmodifiableMap(mutable));
        }
    }
}
