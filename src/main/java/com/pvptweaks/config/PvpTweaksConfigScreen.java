package com.pvptweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import com.pvptweaks.config.PvpTweaksProfiles;
import com.pvptweaks.gui.ButtonEntry;
import com.pvptweaks.gui.SoundPickerScreen;
import com.pvptweaks.gui.SoundPickerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PvpTweaksConfigScreen {

    private static me.shedaniel.clothconfig2.gui.entries.TextListEntry lbl(
            ConfigEntryBuilder e, String color, String text) {
        return e.startTextDescription(
            Text.literal(color + "\u00a7l" + text + "\u00a7r")).build();
    }

    private static void addSoundField(ConfigCategory cat, ConfigEntryBuilder e,
            SoundProfile profile) {
        cat.addEntry(e.startTextDescription(
            Text.literal("\u00a77Active: " + soundStatus(profile))).build());
        // כפתור אמיתי שפותח SoundPickerScreen
        cat.addEntry(new ButtonEntry(
            Text.literal("\u00a7b\u266a Open Sound Picker"),
            () -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null) {
                    net.minecraft.client.gui.screen.Screen cur = mc.currentScreen;
                    mc.setScreen(new SoundPickerScreen(cur, profile, "Sound",
                        () -> PvpTweaksConfig.save()));
                }
            }
        ));
    }


    private static String soundStatus(SoundProfile p) {
        if (p == null || p.isDefault()) return "\u00a77Vanilla Default";
        if (p.isCustom()) {
            try { return "\u00a7aCustom: " + java.nio.file.Paths.get(p.customPath).getFileName(); }
            catch (Exception ex) { return "\u00a7aCustom File"; }
        }
        return "\u00a7b" + p.presetId;
    }

    public static Screen build(Screen parent) {
        PvpTweaksConfig cfg = PvpTweaksConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("\u00a76\u2694 \u00a7lPVP Tweaks\u00a7r \u00a76\u2694"))
            .setSavingRunnable(() -> {
                PvpTweaksConfig.save();
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null) mc.reloadResources();
            });

        ConfigEntryBuilder e = builder.entryBuilder();

        // ── Main ─────────────────────────────────────────────────────────────
        ConfigCategory main = builder.getOrCreateCategory(Text.literal("\u00a7a\u2605 Main"));

        // Competitive preset
        main.addEntry(e.startTextDescription(Text.literal(
            "\u00a76\u25b6\u00a7r Quick Presets")).build());
        main.addEntry(new ButtonEntry(
            Text.literal("\u00a7c\u2694 Apply Competitive Preset"),
            () -> {
                PvpTweaksProfiles.applyCompetitive();
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null) {
                    mc.setScreen(build(parent));
                    mc.reloadResources();
                }
            }
        ));
        main.addEntry(e.startTextDescription(Text.literal(
            "\u00a78Quiet crystals/anchors, no particles, flat fire, low overlay")).build());
        main.addEntry(new ButtonEntry(
            Text.literal("\u00a77\u21ba Reset to Default"),
            () -> {
                PvpTweaksProfiles.applyDefault();
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null) {
                    mc.setScreen(build(parent));
                    mc.reloadResources();
                }
            }
        ));

        // Profiles
        main.addEntry(e.startTextDescription(Text.literal("")).build());
        main.addEntry(e.startTextDescription(Text.literal(
            "\u00a76\u25b6\u00a7r Custom Profiles")).build());
        main.addEntry(e.startTextDescription(Text.literal(
            "\u00a78Note: Save changes first to include them in the profile.")).build());

        // Save current config as profile
        main.addEntry(new ButtonEntry(
            Text.literal("\u00a7a\u2714 Save Current as Profile"),
            () -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc == null) return;
                // Open a simple name-entry dialog
                mc.setScreen(new com.pvptweaks.gui.ProfileNameScreen(
                    mc.currentScreen, null,
                    name -> {
                        PvpTweaksProfiles.save(name);
                        MinecraftClient.getInstance().execute(() -> {
                            MinecraftClient.getInstance().setScreen(build(parent));
                        });
                    }
                ));
            }
        ));

        // List existing profiles with Load/Delete buttons
        java.util.List<String> profiles = PvpTweaksProfiles.list();
        if (profiles.isEmpty()) {
            main.addEntry(e.startTextDescription(Text.literal(
                "\u00a78No saved profiles yet.")).build());
        } else {
            for (String pname : profiles) {
                final String pn = pname;
                main.addEntry(new ButtonEntry(
                    Text.literal("\u00a7b\u25b6 " + pn + "  \u00a77[Load]"),
                    () -> {
                        if (PvpTweaksProfiles.load(pn)) {
                            MinecraftClient mc = MinecraftClient.getInstance();
                            if (mc != null) {
                                mc.setScreen(build(parent));
                                mc.reloadResources();
                            }
                        }
                    }
                ));
            }
        }

        // ── Item Sizes
        ConfigCategory items = builder.getOrCreateCategory(Text.literal("\u00a7b\u2694 Item Sizes"));
        items.addEntry(lbl(e, "\u00a7e", "Melee"));
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fSword"), cfg.swordScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.swordScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fAxe"), cfg.axeScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.axeScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fMace"), cfg.maceScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.maceScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fTrident"), cfg.tridentScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.tridentScalePct = v).build());
        items.addEntry(lbl(e, "\u00a7e", "Ranged"));
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fBow"), cfg.bowScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.bowScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fCrossbow"), cfg.crossbowScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.crossbowScalePct = v).build());
        items.addEntry(lbl(e, "\u00a7e", "Utility / PVP"));
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fShield"), cfg.shieldScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> { cfg.shieldScalePct = v; PvpTweaksConfig.save(); }).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fTotem of Undying"), cfg.totemScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.totemScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fGolden Apple"), cfg.goldenAppleScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.goldenAppleScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a7fRespawn Anchor"), cfg.anchorScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.anchorScalePct = v).build());
        items.addEntry(e.startIntSlider(Text.literal("\u00a78Other Items"), cfg.otherItemScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.otherItemScalePct = v).build());

                // Totem Pop
        ConfigCategory totem = builder.getOrCreateCategory(Text.literal("\u00a7d\u2756 Totem Pop"));
        totem.addEntry(e.startIntSlider(Text.literal("\u00a7fAnimation Size (%)"), cfg.totemPopAnimScalePct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("0=hidden | 100=normal | 200=double")).setSaveConsumer(v -> cfg.totemPopAnimScalePct = v).build());
        totem.addEntry(e.startIntSlider(Text.literal("\u00a7fParticle Density (%)"), cfg.totemPopScalePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.totemPopScalePct = v).build());
        totem.addEntry(e.startIntSlider(Text.literal("\u00a7fSound Volume (%)"), cfg.totemPopVolumePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.totemPopVolumePct = v).build());
        totem.addEntry(lbl(e, "\u00a7d", "Custom Sound"));
        addSoundField(totem, e, cfg.soundTotem);

        // End Crystal
        ConfigCategory crystal = builder.getOrCreateCategory(Text.literal("\u00a73\u25c6 End Crystal"));
        crystal.addEntry(e.startIntSlider(Text.literal("\u00a7fCrystal Entity Size (%)"), cfg.endCrystalScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.endCrystalScalePct = v).build());
        crystal.addEntry(e.startIntSlider(Text.literal("\u00a7fAmbient Particles (%) \u00a78-- end_rod glow"), cfg.crystalParticlePct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("The glowing particles floating around the crystal")).setSaveConsumer(v -> cfg.crystalParticlePct = v).build());
        crystal.addEntry(e.startIntSlider(Text.literal("\u00a7fExplosion Particles (%)"), cfg.enderExplosionParticlePct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("Smoke/explosion particles from crystal pop")).setSaveConsumer(v -> cfg.enderExplosionParticlePct = v).build());
        crystal.addEntry(e.startIntSlider(Text.literal("\u00a7fPop Volume (%)"), cfg.crystalPopVolumePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.crystalPopVolumePct = v).build());
        crystal.addEntry(lbl(e, "\u00a73", "Custom Sound"));
        addSoundField(crystal, e, cfg.soundCrystal);

        // Respawn Anchor
        ConfigCategory anchor = builder.getOrCreateCategory(Text.literal("\u00a75\u2694 Respawn Anchor"));
        anchor.addEntry(e.startIntSlider(Text.literal("\u00a7fExplosion Volume (%)"), cfg.respawnAnchorExplosionPct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("Volume of block.respawn_anchor.explode sound")).setSaveConsumer(v -> cfg.respawnAnchorExplosionPct = v).build());
        anchor.addEntry(e.startIntSlider(Text.literal("\u00a7fExplosion Particles (%)"), cfg.anchorExplosionParticlePct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("Smoke/explosion particles from anchor explosion")).setSaveConsumer(v -> cfg.anchorExplosionParticlePct = v).build());
        anchor.addEntry(e.startIntSlider(Text.literal("\u00a7fAnchor Size in Hand (%)"), cfg.anchorScalePct, 25, 300).setDefaultValue(100).setSaveConsumer(v -> cfg.anchorScalePct = v).build());
        anchor.addEntry(lbl(e, "\u00a75", "Custom Sound"));
        addSoundField(anchor, e, cfg.soundAnchor);

        // Other Explosions
        ConfigCategory exp = builder.getOrCreateCategory(Text.literal("\u00a7c\ud83d\udca5 Other Explosions"));
        exp.addEntry(e.startTextDescription(Text.literal("\u00a77TNT, Creeper, Bed explosions\n\u00a78Does NOT affect End Crystal or Respawn Anchor")).build());
        exp.addEntry(e.startIntSlider(Text.literal("\u00a7fExplosion Volume (%)"), cfg.explosionVolumePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.explosionVolumePct = v).build());
        exp.addEntry(e.startIntSlider(Text.literal("\u00a7fSmoke Particles (%)"), cfg.explosionParticlePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.explosionParticlePct = v).build());
        exp.addEntry(lbl(e, "\u00a7c", "Custom Sound"));
        addSoundField(exp, e, cfg.soundExplosion);

        // Combat
        ConfigCategory combat = builder.getOrCreateCategory(Text.literal("\u00a74\u2694 Combat"));
        combat.addEntry(e.startIntSlider(Text.literal("\u00a7fHit Sound Volume (%)"), cfg.hitVolumePct, 0, 200).setDefaultValue(100).setSaveConsumer(v -> cfg.hitVolumePct = v).build());
        combat.addEntry(e.startBooleanToggle(Text.literal("\u00a7fShow Hit / Crit Particles"), cfg.showHitParticles).setDefaultValue(true).setSaveConsumer(v -> cfg.showHitParticles = v).build());
        combat.addEntry(lbl(e, "\u00a74", "Custom Hit Sound"));
        addSoundField(combat, e, cfg.soundHit);

        // Fire
        ConfigCategory fire = builder.getOrCreateCategory(Text.literal("\u00a76\ud83d\udd25 Fire"));
        String[] firePresets = {"vanilla", "full", "mid", "low", "flat", "none"};
        fire.addEntry(e.startSelector(Text.literal("\u00a7fFire Height"), firePresets, cfg.firePreset)
            .setDefaultValue("vanilla")
            .setTooltip(Text.literal("vanilla=default | full=tall | mid=medium | low=short | flat=ember only | none=hidden"))
            .setSaveConsumer(v -> {
                cfg.firePreset = v;
                PvpTweaksConfig.save();
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null && mc.worldRenderer != null) mc.worldRenderer.reload();
            }).build());
        fire.addEntry(e.startIntSlider(Text.literal("\u00a7fFire Screen Overlay (%)"), cfg.fireOverlayScalePct, 0, 200).setDefaultValue(100).setTooltip(Text.literal("0 = completely hidden while burning")).setSaveConsumer(v -> cfg.fireOverlayScalePct = v).build());

        // Shield
        ConfigCategory shield = builder.getOrCreateCategory(Text.literal("\u00a7f\u2756 Shield"));
        shield.addEntry(lbl(e, "\u00a77", "Size"));
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fSize (%)"), cfg.shieldScalePct, 25, 300).setDefaultValue(100).setTooltip(Text.literal("Shield size in hand")).setSaveConsumer(v -> { cfg.shieldScalePct = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(lbl(e, "\u00a77", "Position  \u00a78(slider, -100 to 100 = -1.0 to 1.0 blocks)"));
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fOffset X  \u00a78Left / Right"), cfg.shieldOffsetX, -100, 100).setDefaultValue(0).setTooltip(Text.literal("< moves Left, > moves Right")).setSaveConsumer(v -> { cfg.shieldOffsetX = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fOffset Y  \u00a78Down / Up"), cfg.shieldOffsetY, -100, 100).setDefaultValue(0).setTooltip(Text.literal("< moves Down, > moves Up")).setSaveConsumer(v -> { cfg.shieldOffsetY = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fOffset Z  \u00a78Closer / Further"), cfg.shieldOffsetZ, -100, 100).setDefaultValue(0).setTooltip(Text.literal("< moves Closer, > moves Further")).setSaveConsumer(v -> { cfg.shieldOffsetZ = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(lbl(e, "\u00a77", "Rotation  \u00a78(degrees, -180 to 180)"));
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fRotation X  \u00a78Tilt Fwd / Back"), cfg.shieldRotX, -180, 180).setDefaultValue(0).setTooltip(Text.literal("< tilts Forward, > tilts Backward")).setSaveConsumer(v -> { cfg.shieldRotX = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fRotation Y  \u00a78Turn Left / Right"), cfg.shieldRotY, -180, 180).setDefaultValue(0).setTooltip(Text.literal("< turns Left, > turns Right")).setSaveConsumer(v -> { cfg.shieldRotY = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(e.startIntSlider(Text.literal("\u00a7fRotation Z  \u00a78Roll Left / Right"), cfg.shieldRotZ, -180, 180).setDefaultValue(0).setTooltip(Text.literal("< rolls Left, > rolls Right")).setSaveConsumer(v -> { cfg.shieldRotZ = v; PvpTweaksConfig.save(); }).build());
        shield.addEntry(new ButtonEntry(
            Text.literal("\u00a7a\u25ba Open Live Shield Adjuster"),
            () -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc != null)
                    mc.setScreen(new com.pvptweaks.gui.ShieldConfigScreen(mc.currentScreen));
            }
        ));
        shield.addEntry(lbl(e, "\u00a7f", "Break Sound"));
        addSoundField(shield, e, cfg.soundShieldBreak);

        // Sound Picker
        ConfigCategory soundCat = builder.getOrCreateCategory(Text.literal("\u00a7e\ud83d\udd0a Sound Picker"));
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7fHow to use custom sounds:")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7e1. \u00a7fGo to each tab (Totem, Crystal, etc.)")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7e2. \u00a7fClick Save and Quit")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7e3. \u00a7fIn-game press ESC → Mods → PVP Tweaks → \u00a7bOpen Sound Picker")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a77In the Sound Picker: search MC sounds, browse your folder, or open the folder to add .ogg files")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a77Current sounds:")).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7b Totem:     " + soundStatus(cfg.soundTotem))).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a73 Crystal:   " + soundStatus(cfg.soundCrystal))).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a7c Explosion: " + soundStatus(cfg.soundExplosion))).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a74 Hit:       " + soundStatus(cfg.soundHit))).build());
        soundCat.addEntry(e.startTextDescription(Text.literal("\u00a75 Anchor:    " + soundStatus(cfg.soundAnchor))).build());

        // ── Share / Import-Export ────────────────────────────────────────────────
        ConfigCategory share = builder.getOrCreateCategory(Text.literal("\u00a7d\ud83d\udce4 Share / Import-Export"));
        share.addEntry(e.startTextDescription(Text.literal(
            "\u00a77Export your current settings to share with others,")).build());
        share.addEntry(e.startTextDescription(Text.literal(
            "\u00a77or import a settings file you received.")).build());
        share.addEntry(e.startTextDescription(Text.literal("")).build());

        share.addEntry(new ButtonEntry(
            Text.literal("\u00a7a\u25b2 Export Settings to Clipboard"),
            () -> {
                try {
                    String json = PvpTweaksProfiles.exportJson();
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        mc.keyboard.setClipboard(json);
                        if (mc.player != null) {
                            mc.player.sendMessage(Text.literal(
                                "\u00a7a[PVP Tweaks] Settings copied to clipboard!"), false);
                        }
                    }
                } catch (Exception ex) {
                    com.pvptweaks.PvpTweaksMod.LOGGER.error(
                        "[PVP Tweaks] Export failed: {}", ex.getMessage());
                }
            }
        ));

        share.addEntry(new ButtonEntry(
            Text.literal("\u00a7e\u25bc Import Settings from Clipboard"),
            () -> {
                try {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc != null) {
                        String json = mc.keyboard.getClipboard();
                        boolean ok = PvpTweaksProfiles.importJson(json);
                        if (mc.player != null) {
                            mc.player.sendMessage(Text.literal(
                                ok ? "\u00a7a[PVP Tweaks] Imported successfully from clipboard!"
                                   : "\u00a7c[PVP Tweaks] Import failed \u2014 invalid clipboard content"), false);
                        }
                        if (ok) {
                            mc.setScreen(build(parent));
                            mc.reloadResources();
                        }
                    }
                } catch (Exception ex) {
                    com.pvptweaks.PvpTweaksMod.LOGGER.error(
                        "[PVP Tweaks] Import failed: {}", ex.getMessage());
                }
            }
        ));

        share.addEntry(e.startTextDescription(Text.literal(
            "\u00a78Quickly share or load settings via system clipboard.")).build());

        return builder.build();
    }
}
