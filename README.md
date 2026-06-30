# PVP Tweaks 1.9.1

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
git clone https://github.com/viper-trick/pvp-tweaks.git
cd pvp-tweaks
./gradlew build
```

Built JARs are placed in each version's `build/libs/` directory, e.g. `src/1.21.11/build/libs/pvp-tweaks-<version>.jar`.

### Build all versions (helper scripts)

```sh
# Linux/macOS
./build-all.sh

# Windows
build-all.bat
```

These scripts run `./gradlew build` for all versions, then move the resulting JARs (excluding `-sources.jar`) into `final-jars/` at the project root.

### Building a single version

```sh
./gradlew :1_21_11:build
```

The subproject name uses underscores in place of dots (`1_21_11` for MC 1.21.11). See `settings.gradle` for the full list.

## Project Structure

```
├── build.gradle              # Root build config (Fabric Loom, shared source logic)
├── settings.gradle           # Enumerates all 12 version subprojects
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
│   ├── 1.21.4/ … 1.21.11/   # Per-version subproject dirs
│   │   ├── java/             # Override source (empty unless version-specific changes needed)
│   │   ├── resources/        # Version-specific resources (fabric.mod.json, mixin configs)
│   │   └── gradle.properties # MC version, yarn mappings, fabric version
│   │
│   ├── 26.1/ … 26.2/        # 26.x subproject dirs
│   │   ├── java/             # Override source (26.2 has per-version 26.2 Vulkan API port)
│   │   └── resources/        # Version-specific resources
│   │
│   └── final-jars/           # Output directory after running build-all.sh
│
└── build/                    # Gradle build outputs (per subproject)
```

### Source organisation

The project uses **shared source groups** to minimise duplication across 12 Minecraft versions:

| Group | Versions | API |
|-------|----------|-----|
| `group-1.21.4-8` | 1.21.4 – 1.21.10 | PoseStack/Matrix3x2fStack, ResourceLocation, old Util |
| `group-1.21.9-11` | 1.21.11 | Identifier, new Util, newest 1.21 API |
| `group-26x` | 26.1, 26.1.1, 26.1.2, 26.2 | Unobfuscated Mojang API |

When a subproject dir has `.java` files in its `java/` directory, the build automatically merges shared + override source into `build/mergedJava`. Otherwise it uses the shared group directly.

MC 26.2 required significant manual API porting due to the Vulkan rendering rewrite (no `migrateMappings` path available).
