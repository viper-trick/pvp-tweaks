# PVP Tweaks

A Fabric client-side mod for Minecraft 1.21.11 that lets you fine-tune PVP-related visuals and sounds.

## Features

| Category | What you can tweak |
|---|---|
| **End Crystal** | Size, ambient particles, pop volume, custom sound |
| **Respawn Anchor** | Explosion volume, explosion particles, custom sound |
| **Other Explosions** | TNT/Creeper/Bed volume, smoke density, custom sound |
| **Totem Pop** | Animation size, particle density, volume, custom sound |
| **Combat** | Hit sound volume, crit particles, custom sound |
| **Fire** | Screen overlay scale, flame density, height preset (vanilla/full/mid/low/flat/none) |
| **Shield** | Size, position offset (X/Y/Z), rotation (X/Y/Z), break sound |
| **Item Sizes** | Per-item scale: sword, axe, shield, totem, gapple, anchor, bow, crossbow, trident, mace |
| **Custom Sounds** | Replace any PVP sound with a Minecraft preset or a custom .ogg file |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.11
2. Install dependencies: [Fabric API](https://modrinth.com/mod/fabric-api), [Cloth Config](https://modrinth.com/mod/cloth-config), [Mod Menu](https://modrinth.com/mod/modmenu)
3. Drop `pvp-tweaks-1.0.0.jar` into your `mods` folder

## Usage

- Open **Mod Menu** → **PVP Tweaks** to access all settings
- For custom sounds: place `.ogg` files in `.minecraft/config/pvptweaks/sounds/`, then use the **Sound Picker** button inside each category

## Dependencies

| Mod | Version |
|---|---|
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | 0.141.3+1.21.11 |
| Cloth Config | 21.11.153 |
| Mod Menu | 17.0.0-beta.2 |

## Building
```bash
./gradlew build
```

Output: `build/libs/pvp-tweaks-1.0.0.jar`

## Contact & License

All Rights Reserved — contact yag.fvt@gmail.com for licensing inquiries.
