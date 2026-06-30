package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.server.packs.repository.PackRepository;

@Mixin(PackRepository.class)
public class ResourcePackManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void pvptweaks$addProvider(net.minecraft.server.packs.repository.RepositorySource[] providers, CallbackInfo ci) {
        // No-op for now, better to use the providers field if we can
    }

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$injectDynamicPack(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<java.util.Map<String, net.minecraft.server.packs.repository.Pack>> cir) {
        java.util.Map<String, net.minecraft.server.packs.repository.Pack> original = cir.getReturnValue();
        java.util.Map<String, net.minecraft.server.packs.repository.Pack> mutable = new java.util.HashMap<>(original);

        net.minecraft.server.packs.PackLocationInfo info = new net.minecraft.server.packs.PackLocationInfo(
            "pvptweaks:dynamic",
            net.minecraft.network.chat.Component.literal("PVP Tweaks Dynamic"),
            net.minecraft.server.packs.repository.PackSource.BUILT_IN,
            java.util.Optional.empty()
        );
        net.minecraft.server.packs.PackSelectionConfig pos = new net.minecraft.server.packs.PackSelectionConfig(
            true, // required
            net.minecraft.server.packs.repository.Pack.Position.BOTTOM,
            true  // fixedPosition
        );
        net.minecraft.server.packs.repository.Pack.ResourcesSupplier factory = new net.minecraft.server.packs.repository.Pack.ResourcesSupplier() {
            @Override public net.minecraft.server.packs.PackResources openPrimary(net.minecraft.server.packs.PackLocationInfo info) {
                return new com.pvptweaks.resources.PvpTweaksDynamicPack();
            }
            @Override public net.minecraft.server.packs.PackResources openFull(net.minecraft.server.packs.PackLocationInfo info, net.minecraft.server.packs.repository.Pack.Metadata metadata) {
                return openPrimary(info);
            }
        };
        
        net.minecraft.server.packs.repository.Pack profile = net.minecraft.server.packs.repository.Pack.readMetaAndCreate(
            info,
            factory,
            net.minecraft.server.packs.PackType.CLIENT_RESOURCES,
            pos
        );

        if (profile != null) {
            mutable.put(profile.getId(), profile);
            cir.setReturnValue(java.util.Collections.unmodifiableMap(mutable));
        }
    }
}
