package com.pvptweaks.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.Identifier;

public class SoundRedirects {
    private static final Map<Identifier, Identifier> REDIRECTS = new ConcurrentHashMap<>();

    public static void set(Identifier original, Identifier replacement) {
        REDIRECTS.put(original, replacement);
    }

    public static Identifier get(Identifier original) {
        return REDIRECTS.get(original);
    }

    public static void remove(Identifier original) {
        REDIRECTS.remove(original);
    }

    public static void clear() {
        REDIRECTS.clear();
    }
}
