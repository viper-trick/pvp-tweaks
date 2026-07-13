# PVP Tweaks 1.9.3

Comprehensive PVP performance and visual optimizations for Modern Minecraft (1.21.4–26.2).

## Features

- **Home** — Settings dashboard, mod version info, quick links.
- **Item Sizes** — Per-item-type scale and offset adjustments (held items, armor, tools, food, etc.).
- **Visuals** — Fire preset system (entity fire height/opacity, ground fire toggle), fire overlay scaling, fullbright, custom FOV, potion color override, menu background blur toggle.
- **HUD** — CPS counter, armor durability %, coordinates, potion effects display — all movable and scalable via built-in adjuster screens.
- **Sounds** — Profile-based sound replacement with a built-in picker GUI, import/export, per-profile sound packs, and pitch/tempo control.
- **Optimization** — ExplosionTracker suppresses crystal and respawn anchor explosion sounds, particles, and animations through adjustable post-explosion cooldowns.
- **Profiles** — Save, load, import, and export full config profiles.
- **Zoom** — Configurable zoom (smooth or instant) with adjustable FOV and scroll sensitivity.
- **General QoL** — Smooth scrolling in GUIs, vertical sync toggle, item tooltip scaling, and more.

Access settings via the config keybind or the Mod Menu screen (if installed). A modern hub GUI is available by default; a Cloth Config–based legacy menu can be enabled as an alternative.

## License

All Rights Reserved. The source code is made available for educational and review purposes only. You may not distribute, sublicense, or sell modified or unmodified builds of this mod.

## Dependencies

**Required:**
- Fabric Loader ≥0.18.4
- Fabric API (any version matching the Minecraft target)

**Optional:**
- Mod Menu — adds a config screen button in the mods list
- Cloth Config — required for the legacy settings screen

## Building

### Prerequisites

- JDK 25 or later (required for 26.x versions; JDK 21+ for 1.21.x only)
- Git

### Steps

```sh
./gradlew :1_21_4_5:build
```

Each group JAR covers a range of compatible MC versions. See `settings.gradle` for the full group list.

Built JARs are placed in each build group's `build/libs/` directory, e.g. `src/build-1.21.4-5/build/libs/pvp-tweaks-<version>.jar`.

### Build all groups (helper scripts)

```sh
# Linux/macOS
./build-all.sh

# Windows
build-all.bat
```

These scripts run `./gradlew build` for all 6 groups, then move the resulting JARs (excluding `-sources.jar`) into `final-jars/` at the project root.

### Building a single group

```sh
./gradlew :1_21_4_5:build
```

The subproject name uses underscores in place of dots and hyphens (`1_21_4_5` for group `1.21.4-5`). See `settings.gradle` for the full list.

## Project Structure

```
├── build.gradle              # Root build config (Fabric Loom, shared source logic)
├── settings.gradle           # Enumerates all 6 multi-version build groups
├── gradle.properties         # Shared build properties (mod version, loader, loom)
├── build-all.sh              # Linux/macOS build helper
├── build-all.bat             # Windows build helper
├── README.md                 # This file
│
├── src/
│   ├── group-1.21.4-8/       # Shared source: MC 1.21.4–1.21.10 (PoseStack/ResourceLocation API)
│   │   ├── java/             # Shared Mojang-mapped source
│   │   └── resources/        # Shared assets, mixin configs, mod metadata, icons
│   │
│   ├── group-1.21.9-11/      # Shared source: MC 1.21.11 (newest Identifier/Util API)
│   │   ├── java/             # Shared Mojang-mapped source
│   │   └── resources/        # Shared assets
│   │
│   ├── group-26x/            # Shared source: MC 26.1–26.2 (unobfuscated shared API)
│   │   ├── java/             # Shared Mojang-mapped source
│   │   └── resources/        # Shared assets
│   │
│   ├── build-1.21.4-5/       # Build group: JAR covers MC 1.21.4–1.21.5
│   │   ├── java/             # Override source
│   │   ├── resources/        # Group-specific resources (fabric.mod.json, version range)
│   │   └── gradle.properties # MC version, fabric version, java version
│   │
│   ├── build-1.21.6-8/       # Build group: JAR covers MC 1.21.6–1.21.8
│   ├── build-1.21.9-10/      # Build group: JAR covers MC 1.21.9–1.21.10
│   ├── build-1.21.11/        # Build group: JAR covers MC 1.21.11
│   ├── build-26.1/           # Build group: JAR covers MC 26.1, 26.1.1, 26.1.2
│   ├── build-26.2/           # Build group: JAR covers MC 26.2
│   │
│   ├── 1.21.4/ … 26.2/      # Per-version source dirs (kept as reference, not built)
│   │
│   └── final-jars/           # Output directory after running build-all.sh
│
└── build/                    # Gradle build outputs (per build group)
```

### Source organisation

The project uses **shared source groups** to minimise duplication across 12 MC versions, combined into **6 build groups** that each produce one multi-version JAR:

| Build Group | Shared Source | Covers | Distinguishing API |
|-------------|--------------|--------|--------------------|
| `build-1.21.4-5` | `group-1.21.4-8` | 1.21.4, 1.21.5 | `PoseStack` / `Matrix3x2fStack` |
| `build-1.21.6-8` | `group-1.21.4-8` | 1.21.6, 1.21.7, 1.21.8 | `PoseStack` + `ItemStack` rendering API |
| `build-1.21.9-10` | `group-1.21.4-8` | 1.21.9, 1.21.10 | Same API as 1.21.6-8 (only cosmetic diff) |
| `build-1.21.11` | `group-1.21.9-11` | 1.21.11 | `Identifier`, new `Util`, `onPress` mouse handler |
| `build-26.1` | `group-26x` | 26.1, 26.1.1, 26.1.2 | Unobfuscated, pre-Vulkan API |
| `build-26.2` | `group-26x` | 26.2 | Unobfuscated, Vulkan rendering API |

When a build group dir has `.java` files in its `java/` directory, the build automatically merges shared + override source into `build/mergedJava`. Otherwise it uses the shared group directly.

MC 26.2 required significant manual API porting due to the Vulkan rendering rewrite (no `migrateMappings` path available).
