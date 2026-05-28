# PVP Tweaks

Comprehensive PVP performance and visual optimizations for Modern Minecraft (1.21.4–1.21.11).

## Features

- **Crystal/Anchor Lag Mitigation** — ExplosionTracker suppresses crystal and respawn anchor explosion sounds, particles, and animations through adjustable post-explosion cooldowns.
- **HUD Adjusters** — CPS counter, armor durability %, coordinates, potion effects — all movable and scalable via built-in adjuster screens.
- **Combat Visuals** — Fire preset system (entity fire height/opacity, ground fire toggle), fire overlay scaling, fullbright, custom FOV, item scale, potion color override.
- **Sound Profile System** — Profile-based sound replacement with a built-in picker GUI, import/export, and per-profile sound packs.
- **General QoL** — Smooth scrolling in GUIs, menu background blur toggle, vertical sync toggle, and more.

## License

All Rights Reserved. The source code is made available for educational and review purposes only. You may not distribute, sublicense, or sell modified or unmodified builds of this mod.

## Dependencies

**Required:**
- Fabric Loader ≥0.18.4
- Fabric API (any version matching the Minecraft target)
- Fabric Language Kotlin 1.11.0+kotlin.2.0.0

**Optional:**
- Mod Menu — adds a config screen button
- Cloth Config — required for some config screen features

## Building

### Prerequisites

- JDK 21 or later
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

These scripts run `./gradlew build` for all versions, then collect the resulting JARs (excluding `-sources.jar`) into `final-jars/` at the project root.

### Building a single version

```sh
./gradlew :1_21_11:build
```

The subproject name uses underscores in place of dots (`1_21_11` for MC 1.21.11). See `settings.gradle` for the full list.

## Project Structure

```
src/
├── 1.21.4/ … 1.21.11/     # Version-specific subprojects
│   ├── java/               # Source code
│   ├── resources/          # Mixin configs, assets, mod metadata
│   ├── build.gradle        # Version-specific dependencies & mappings
│   └── gradle.properties   # MC version, yarn mappings, fabric version
├── main/                   # Shared source (compiled per-version via subproject)
├── build.gradle            # Root build config (Fabric Loom, common deps)
├── settings.gradle         # Enumerates all version subprojects
└── gradle.properties       # Shared build properties
```
