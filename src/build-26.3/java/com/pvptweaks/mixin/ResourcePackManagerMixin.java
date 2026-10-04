package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PackRepository.class)
public class ResourcePackManagerMixin {

    @Inject(method = "discoverAvailable", at = @At("RETURN"), cancellable = true)
    private void pvptweaks$injectDynamicPack(CallbackInfoReturnable<Map<String, Pack>> cir) {
        Map<String, Pack> original = cir.getReturnValue();
        Map<String, Pack> mutable = new java.util.HashMap<>(original);

        PackLocationInfo info = new PackLocationInfo(
            "pvptweaks:dynamic",
            net.minecraft.network.chat.Component.literal("PVP Tweaks Dynamic"),
            PackSource.BUILT_IN,
            java.util.Optional.empty()
        );
        PackSelectionConfig pos = new PackSelectionConfig(
            true,
            Pack.Position.BOTTOM,
            true
        );

        Pack.ResourcesSupplier factory = new Pack.ResourcesSupplier() {
            @Override public PackResources openPrimary(PackLocationInfo info) {
                return new com.pvptweaks.resources.PvpTweaksDynamicPack();
            }
            @Override public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return openPrimary(info);
            }
        };

        Pack profile = Pack.readMetaAndCreate(
            info,
            factory,
            PackType.CLIENT_RESOURCES,
            pos
        );

        if (profile != null) {
            mutable.put(profile.getId(), profile);
            cir.setReturnValue(java.util.Collections.unmodifiableMap(mutable));
        }
    }
}
