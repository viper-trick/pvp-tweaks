package com.pvptweaks.sound;

import java.util.List;

/** Curated lists of relevant game sounds for each PVP category. */
public class PvpTweaksGameSounds {

    public static final List<String> TOTEM_PRESETS = List.of(
        "default",
        "item.totem.use",
        "entity.firework_rocket.blast",
        "entity.firework_rocket.large_blast",
        "entity.firework_rocket.twinkle",
        "entity.lightning_bolt.thunder",
        "entity.ender_dragon.growl",
        "block.bell.use",
        "entity.elder_guardian.curse",
        "ui.toast.challenge_complete"
    );

    public static final List<String> CRYSTAL_PRESETS = List.of(
        "default",
        "entity.end_crystal.explode",
        "entity.generic.explode",
        "entity.tnt.primed",
        "entity.firework_rocket.blast",
        "entity.lightning_bolt.impact",
        "block.glass.break",
        "entity.ender_dragon.death"
    );

    public static final List<String> EXPLOSION_PRESETS = List.of(
        "default",
        "entity.generic.explode",
        "entity.end_crystal.explode",
        "entity.tnt.primed",
        "entity.firework_rocket.blast",
        "entity.firework_rocket.large_blast",
        "entity.lightning_bolt.thunder",
        "entity.lightning_bolt.impact",
        "block.anvil.land",
        "entity.iron_golem.death"
    );

    public static final List<String> HIT_PRESETS = List.of(
        "default",
        "entity.player.hurt",
        "entity.generic.hurt",
        "entity.zombie.hurt",
        "block.wood.hit",
        "block.stone.hit",
        "entity.slime.squish",
        "entity.arrow.hit_player",
        "entity.player.hurt_drown"
    );
}
