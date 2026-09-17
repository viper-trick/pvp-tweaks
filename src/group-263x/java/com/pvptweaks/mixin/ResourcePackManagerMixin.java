package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.KnownPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Injects the PVP Tweaks dynamic resource pack into PackRepository after reload.
 * In 26.1, providePackProfiles was removed; we use the reload() hook instead.
 */
@Mixin(PackRepository.class)
public class ResourcePackManagerMixin {

    @Inject(method = "reload", at = @At("RETURN"), require = 0)
    private void pvptweaks$injectDynamicPack(CallbackInfo ci) {
        PackRepository self = (PackRepository)(Object)this;
        if (self.getPack("pvptweaks:dynamic") != null) return;

        PackLocationInfo info = new PackLocationInfo(
            "pvptweaks:dynamic",
            net.minecraft.network.chat.Component.literal("PVP Tweaks Dynamic"),
            PackSource.BUILT_IN,
            Optional.<KnownPack>empty()
        );
        PackSelectionConfig pos = new PackSelectionConfig(
            true,
            Pack.Position.BOTTOM,
            true
        );

        Pack.ResourcesSupplier factory = new Pack.ResourcesSupplier() {
            @Override public PackResources openPrimary(PackLocationInfo info) {
                return new PvpTweaksDynamicPack();
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
            self.addPack(profile.getId());
        }
    }
}
