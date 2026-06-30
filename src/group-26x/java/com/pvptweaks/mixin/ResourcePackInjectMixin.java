package com.pvptweaks.mixin;

import com.pvptweaks.resources.PvpTweaksDynamicPack;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

/**
 * Injects the PVP Tweaks dynamic resource pack into the reload pipeline.
 * createReload(Executor, Executor, CompletableFuture, List) — we inject at
 * RETURN and swap in a modified list for the ReloadInstance.
 *
 * Note: This doesn't let us modify the original list param, but the dynamic
 * pack is also injected via PackRepository for UI visibility.
 */
@Mixin(ReloadableResourceManager.class)
public class ResourcePackInjectMixin {

    @Inject(method = "createReload", at = @At("HEAD"), require = 0)
    private void pvptweaks$injectFirePack(CallbackInfoReturnable<?> cir) {
        // This mixin is intentionally minimal — the PvpTweaksDynamicPack is
        // injected through the PackRepository mixin instead.
        // Keeping this as a placeholder to avoid class load failures.
    }
}
