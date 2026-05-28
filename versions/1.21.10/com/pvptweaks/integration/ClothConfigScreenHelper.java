package com.pvptweaks.integration;

import com.pvptweaks.config.PvpTweaksConfigScreen;
import net.minecraft.client.gui.screen.Screen;

public class ClothConfigScreenHelper {
    public static Screen buildScreen(Screen parent) {
        return PvpTweaksConfigScreen.build(parent);
    }
}
