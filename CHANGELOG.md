# PVP Tweaks Changelog

## 1.4.0 (Performance & HUD Update)

### New Features
- **Optimizers Tab (Competitive Updates)**:
    - **Crystal Optimizer**: Instant client-side removal of crystals on attack. This fixes the "ghost crystal" glitch on high-ping servers, allowing for immediate re-placement.
    - **Anchor Optimizer**: Replaces exploded Respawn Anchors with a client-side placeholder (Fern). Since the placeholder is "replaceable" by standard block placement, you can now place a crystal in the anchor's coordinate before the server even confirms the explosion.
- **HUD Systems**:
    - **CPS HUD**: Added a fully customizable Clicks-Per-Second tracker.
        - Movable via a dedicated **CPS Adjuster Screen**.
        - Supports **Smart Rainbow** mode for smooth color cycling.
        - Adjustable scale, shadow, and color presets (White, Red, Green, etc.).
        - Toggleable L/R labels.
    - **Armor Durability HUD**: Added a visual tracker for equipped armor and hand items.
        - Movable via a dedicated **Durability Adjuster Screen**.
        - Customizable alignment (Horizontal/Vertical).
        - Optional low-durability blinking alerts and custom alert sounds.
- **Vanilla Settings Tab**:
    - Renamed from "PvP Presets" and expanded with essential shortcuts:
    - Added **Mouse Sensitivity** slider.
    - Added **Auto Jump** toggle.
    - Added **Fullscreen** toggle.

### Improvements & Fixes
- **Competitive Preset**: Now automatically enables the Crystal and Anchor optimizers.
- **Sound System**: Fixed redundant sound picker logic and improved conversion stability.
- **GUI**: Improved readability of the config screen with clearer category icons.
- **Cleanup**: Removed outdated/redundant mixins (`InGameHudMixin`, etc.) to improve compatibility with other mods like Sodium and Iris.
- **Version Support**: Optimized for Minecraft 1.21.11.

---

## 1.2.0-beta (Initial Release Highlights)
- Custom sound engine (Import MP3/OGG without resource packs).
- Live Shield Adjuster.
- Per-item scaling for swords, axes, and totems.
- Built-in fire presets.
- Profile and Share system.
