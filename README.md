# PVP Tweaks 1.9.1

Comprehensive PVP performance and visual optimizations for Modern Minecraft (1.21.4–1.21.11).

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
- Fabric Language Kotlin 1.11.0+kotlin.2.0.0

**Optional:**
- Mod Menu — adds a config screen button in the mods list
- Cloth Config — required for the legacy settings screen

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

These scripts run `./gradlew build` for all versions, then move the resulting JARs (excluding `-sources.jar`) into `final-jars/` at the project root.

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
│   ├── resources/          # Mixin configs, assets, mod metadata, icons
│   ├── build.gradle        # Version-specific dependencies & mappings
│   └── gradle.properties   # MC version, yarn mappings, fabric version
├── main/                   # Shared source (compiled per-version via subproject)
│   ├── java/               # Shared source code
│   └── resources/          # Shared assets, reference mappings, icon
├── build.gradle            # Root build config (Fabric Loom, common deps)
├── settings.gradle         # Enumerates all version subprojects
├── gradle.properties       # Shared build properties
├── build-all.sh            # Linux/macOS build helper
└── build-all.bat           # Windows build helper
```
