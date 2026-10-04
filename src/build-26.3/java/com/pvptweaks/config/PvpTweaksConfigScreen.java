package com.pvptweaks.config;

import com.pvptweaks.gui.PvpTweaksHubScreen;
import net.minecraft.client.gui.screens.Screen;

public class PvpTweaksConfigScreen {
    public static Screen build(Screen parent) {
        return new PvpTweaksHubScreen(parent);
    }
}
