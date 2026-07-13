## 1.9.3

### Added
- **Fire Block Height controls** — presets (vanilla/full/mid/low/flat/none) now directly control fire block height; renamed from "Entity Fire Height" to "Fire Block Height"
- **Disabled widget styling** — buttons and sliders render grayed out when inactive
- **Flash hint on disabled crosshair settings** — clicking a disabled crosshair control briefly highlights the "Custom" toggle

### Changed
- Fire block height preset changes now only apply via "Save Fire" button or "Done" (no auto-reload on preset cycle)
- "Save Fire" button now only triggers chunk rebuild (no resource pack reload)
- Auto-reload tick handler removed (was causing unwanted reloads on preset change)
- Updated mod version to 1.9.3 across all supported Minecraft versions

### Fixed
- GUI buttons no longer visible above the title bar when scrolling
- Crosshair Adjuster: removed the center vertical divider bar
- Crosshair Adjuster: all settings are locked when Custom is toggled OFF

## 1.9.1

### Added
- **Modern GUI** — rounded corners (radius 8) across all buttons, sliders, and tabs with proper circular-arc rendering
- **Explosion particle controls** — per-type sliders for TNT, Creeper, Bed, Ghast, and Wind Charge particles
- **Pitch control in sound picker** — pitch/tempo slider moved to the top of the sound picker screen

### Changed
- Updated mod version to 1.9.1 across all supported Minecraft versions (1.21.4–1.21.11)

### Fixed
- Slider fill overflowing the container outline at both low and high fill percentages
- Bottom arc direction in rounded rectangle rendering
- Sidebar selected outline using plain rect instead of rounded outline

## 1.9.0

### Added
- **Crosshair Tweak** (HUD tab) — custom crosshair with color, size, and transparency adjustments; supports importing crosshair codes from other games (e.g. CS2)
- **Info tab** — new tab with mod description, version, author, clickable links (GitHub, Issues, Modrinth), and contact information
- **Global "Item Size" slider** — global item scaling control

### Fixed
- Hotbar labels not displaying correctly
- Item background rendering
- Scrolling behavior in menus
- Various crosshair tweak issues

### Changed
- Updated mod version to 1.9.0 across all supported Minecraft versions (1.21.4–1.21.11)
