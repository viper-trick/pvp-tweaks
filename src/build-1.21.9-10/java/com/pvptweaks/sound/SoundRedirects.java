package com.pvptweaks.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

public class SoundRedirects {
    private static final Map<ResourceLocation, ResourceLocation> REDIRECTS = new ConcurrentHashMap<>();

    public static void set(ResourceLocation original, ResourceLocation replacement) {
        REDIRECTS.put(original, replacement);
    }

    public static ResourceLocation get(ResourceLocation original) {
        return REDIRECTS.get(original);
    }

    public static void remove(ResourceLocation original) {
        REDIRECTS.remove(original);
    }

    public static void clear() {
        REDIRECTS.clear();
    }
}
