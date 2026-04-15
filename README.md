# PVP Tweaks

A Fabric client-side mod for Minecraft 1.21.11 that lets you fine-tune PVP-related visuals and sounds.

## Features

| Category | What you can tweak |
|---|---|
| **Competitive Profile** | Built-in preset for Crystal PVP: silent explosions, 30% totem animation, 5% totem particles, hit crits enabled |
| **Profiles System** | Save current config as a named profile or load existing ones instantly |
| **Share / Sync** | **Import/Export** via system clipboard: share settings by copying a JSON string |
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
3. Drop `pvp-tweaks-1.2.0-beta.jar` into your `mods` folder

## Usage

- **Mod Button**: Access the mod settings directly from the top of the **Pause Menu** or via **Mod Menu**
- **Presets**: Apply the **Competitive** preset for an instant pro setup
- **Custom Sounds**: Place `.ogg` files in `.minecraft/config/pvptweaks/sounds/`, then use the **Sound Picker** button inside each category
- **Sharing**: Use the **Export to Clipboard** button in the **Share** category to copy your settings string for others

## Dependencies

| Mod | Version |
|---|---|
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | 0.141.3+1.21.11 |
| Cloth Config | 21.11.153 |
| Mod Menu | 11.0.1 |

## Building
```bash
./gradlew build
```

Output: `build/libs/pvp-tweaks-1.2.0-beta.jar`

## Contact & License

All Rights Reserved — contact yag.fvt@gmail.com for licensing inquiries.
