# PVP Tweaks

A comprehensive Fabric client-side optimization and visual utility mod for Minecraft 1.21.11.

PVP Tweaks consolidates multiple PVP utilities into a single, high-performance mod. It provides deep control over combat visuals, audio, and network-related responsiveness without the need for multiple mods or resource packs.

## Core Features

### ⚔️ Performance Optimizers
Mitigate PING and latency effects for a smoother combat experience:
- **Crystal Optimizer**: Immediate client-side removal of End Crystal entities upon attack, allowing for zero-delay re-placement.
- **Anchor Optimizer**: Replaces exploded Respawn Anchors with a client-side "fake" block (Fern) instantly, keeping the coordinate clear for immediate placement before server confirmation.

### 📊 HUD Utilities
Fully customizable, movable HUD elements with live preview:
- **CPS HUD**: Left/Right CPS tracker with customizable scale, position, color, shadow, and a "Smart Rainbow" mode.
- **Durability HUD**: Track armor and held item durability. Includes low-durability blinking alerts and custom alert sounds.
- **HUD Adjusters**: Dedicated screens to drag and drop HUD elements in real-time.

### 🔊 Audio Control
- **Per-Event Volume**: Independent sliders (0-200%) for End Crystals, Respawn Anchors, TNT, and player hits.
- **Sound Picker**: Built-in browser for all vanilla sound events.
- **Custom Sounds**: Import `.mp3`, `.ogg`, `.wav`, or `.flac` files directly without creating a resource pack.

### 🔥 Visual Optimization
- **Fire Presets**: Six built-in height presets (Vanilla to None).
- **Fire Overlay**: Adjustable screen overlay scale (0-200%).
- **Item Scaling**: Fine-tune the first-person scale of Swords, Axes, Shields, Totems, and more.
- **Shield Adjuster**: Live adjustment of shield position (X/Y/Z) and rotation.

### 📁 Profile System
- **Presets**: Built-in "Competitive" preset for instant optimization.
- **Named Profiles**: Save and switch between multiple configuration snapshots in-game.
- **Share**: Export and import settings directly via the system clipboard.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.11.
2. Install dependencies: [Fabric API](https://modrinth.com/mod/fabric-api), [Cloth Config](https://modrinth.com/mod/cloth-config), [Mod Menu](https://modrinth.com/mod/modmenu).
3. Place `pvp-tweaks-1.4.0.jar` into your `mods` folder.

| Dependency | Required Version |
|---|---|
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | 0.141.3+1.21.11 |
| Cloth Config | 21.11.153 |
| Mod Menu | 17.0.0 |

*Note: MP3/WAV conversion requires `ffmpeg` installed on your system.*

## Building from Source

```bash
./gradlew build
```
Output: `build/libs/pvp-tweaks-1.4.0.jar`

## License
Distributed under the MIT License. See `LICENSE` for more information.
