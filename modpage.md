# ⚔️ PVP Tweaks (in development, beta)

**PVP Tweaks** consolidates essential combat, HUD, and visual utilities into one high-performance, client-side mod for Minecraft **1.21.4–26.X**.

Clicking the in-game **"PVP Tweaks"** button opens a dedicated hub screen with intuitive tabs for *Item Sizes, Visuals, HUD, Sounds, Optimization, Profiles,* and *Info*. Fine-tune totems, shield positioning, fire overlays, crystals, respawn anchors, animations, particles, sounds, custom crosshairs, and more-all from one clean interface.

> ⚠️ **Note to Players & Server Admins:**
> This mod is designed purely for Quality-of-Life (QoL). It does **not** provide any cheats, automation, or unfair advantages. It simply allows you to fine-tune your client's visuals and responsiveness to ensure your game runs smoothly in competitive environments.

---

<details>
<summary>🕹️ How to Use</summary>

1. **Accessing the Config:**
* **In-Game:** Press the configured keybind to open the PVP Tweaks hub screen.
* **Mod Menu:** Select **PVP Tweaks** from your mods list and click **Configure**.


2. **HUD Adjuster:** Click **Adjuster** buttons inside the config to drag and reposition HUD elements freely on your screen.
3. **Sound Picker:** Open the Sound Picker in any category, preview or search for sounds (or import your own), adjust pitch, and save.
4. **Profiles:** Save your entire setup and easily share it using clipboard import/export strings.


</details>

---

## ✨ Core Features

<details>
<summary>📊 HUD Utilities</summary>


*Fully customizable HUD elements with real-time editing:*

* **CPS HUD:** Left/Right CPS tracker with custom scale, position, color, text shadow, and a "Smart Rainbow" mode.
* **Armor Durability HUD:** Tracks worn armor and held items. Includes low-durability blink alerts and custom sound triggers.
* **Coordinates & Potion Effects:** Displays current position and active status effects with full scale/position adjustments.
* **Crosshair Editor:** Custom crosshairs with color, size, opacity, outlines, and crosshair code import support (e.g., CS2 codes).
* **Item Background:** Toggle and customize background rendering in inventory slots.
* **Hotbar Slot Labels:** Display keybind hints or numbers directly on hotbar slots.
* **Live Adjusters:** Drag and drop HUD elements anywhere on screen with interactive visual editors.

</details>



<details>
<summary>🔊 Audio & Visual Control</summary>

* **Sound Profiles:** Profile-based sound replacement system with an integrated picker and per-profile sound packs.
* **Sound Picker:** In-game browser for all vanilla sound events with live previewing and pitch adjustments (Explosions, Combat, Misc, and Custom).
* **Custom Sounds:** Directly import `.mp3`, `.ogg`, `.wav`, or `.flac` audio files without creating a resource pack. *(Note: Legacy versions ≤1.4.8 require [ffmpeg](https://ffmpeg.org) installed).*
* **Durability Alerts:** Set custom sound warnings for low-durability armor and tools.
* **Per-Event Volume Sliders:** Independent volume controls for End Crystals, Respawn Anchors, TNT, hits, and other actions.
* **Explosion Particles:** Fine-tune particle opacity/count for TNT, Creepers, Beds, Ghasts, and Wind Charges.

</details>




<details>
<summary>👁️ Visual Adjustments</summary>

* **Fire Block Height:** Preset heights (*Vanilla, Mid, Low, Flat,* or *None*).
* **Fire Overlay:** Adjust the screen fire overlay scale from `0%` to `200%`.
* **Fullbright:** Integrated gamma customization screen for clear visibility in dark areas.
* **Pumpkin Blur:** Toggle the carved pumpkin screen overlay.
* **Shield Adjuster:** Real-time positioning and rotation tweaking for held shields.
* **Plants Control:** Client-side rendering controls for tall grass and plants.
* **Entity & Animation Scaling:**
* Totem pop animation scale (`0–200%`).
* End Crystal entity scale (`25–300%`).


* **Potion Color Override:** Change indicator colors for active potion effects.
* **GUI Tweaks:** Toggle background blur and enable smooth scrolling in menus.


</details>



<details>
<summary>🛡️ Item Sizes & Custom Scaling</summary>


* **Global Scale:** Master scale slider for all held items (`25-300%`).
* **Per-Item Sliders:** Individual scaling for Swords, Axes, Maces, Tridents, Shields, Armor, Bows, Crossbows, Totems, Golden Apples, Respawn Anchors, and Misc items.
* **Flexible Scaling Modes:**
* *Listed Mode:* Only scales items explicitly configured.
* *Unlisted Mode:* Scales everything *except* configured exceptions.
* *Custom Mode:* Interactive per-item search and configuration screen.


</details>




<details>
<summary>🎨 Modern UI & Experience</summary>

* **Hub Screen:** Sleek, tabbed interface with clean rounded panels and controls (8px border radius).
* **Zoom Utility:** Configurable smooth or instant zoom with customizable FOV and scroll sensitivity.
* **Profile System:** Save, load, import, and export entire configurations via system clipboard or the dedicated profiles folder.


</details>


<details>
<summary>🚀 Performance Optimizers (beta)</summary>

*Designed to mitigate ping and latency effects for a smoother combat experience:*

* **Crystal Optimizer:** Client-side immediate removal of End Crystal entities upon attack, allowing for zero-delay re-placement.
* **Anchor Optimizer:** Instantly replaces exploded Respawn Anchors with a client-side placeholder (Fern) to keep the coordinate clear for immediate placement before server confirmation.
* **Fairness Guarantee:** Strictly visual and client-side collision tweaks that do not interfere with server-side logic.


</details>


---



<details>
<summary>📥 Installation & Dependencies</summary>

1. Download and install [Fabric Loader](https://fabricmc.net/use/installer/).
2. Download and place [Fabric API](https://modrinth.com/mod/fabric-api) into your `.minecraft/mods` folder.
3. Place `pvp-tweaks.jar` into your `.minecraft/mods` folder.

| Dependency | Required / Optional | Notes |
| --- | --- | --- |
| **Fabric Loader** | **Required** | Version `≥0.18.4` |
| **Fabric API** | **Required** | All supported MC versions |
| **Mod Menu** | *Optional* | Adds a "Configure" button directly in your Mods list |
| **Cloth Config** | *Optional* | Additional configuration backend support |


</details>



---

<details>
<summary>🛠️ Issues, Support & Development</summary>

* **Issue Tracker:** Report bugs or crashes on [GitHub Issues](https://github.com/viper-trick/pvp-tweaks/issues). Please attach your `latest.log` file along with a description of the issue.
* **Direct Contact:** Reach out via email at `yag.fvt@gmail.com`.

*Note: AI assistance was utilized during development to optimize workflow; all code has been line-by-line reviewed, tested, and verified for security and stability by a human developer.*

💡 Notes on Compatibility & Testing

> Primary testing is conducted on Minecraft **26.2** *(Linux Mint, GNOME, X11)*.
> All other supported versions are currently in Beta - while core features should function normally, minor version-specific variations may occur.
>I need beta testers please contact me on Discord: vipertrick

Note on Development & Release
The mod hasn't really been officially released yet, it's still in development. It will only be called "Release" after version 2.0.0 (it will probably be reset to "R1.0.0")
</details>



---

<details>
<summary>📌 License & Code Usage</summary>

This project is published under **All Rights Reserved**.

If you would like to use parts of this code in another project or integrate it elsewhere, please contact `yag.fvt@gmail.com` first. Requests are generally welcomed! However, reuploading this mod or modified versions of it to any platform (Modrinth, CurseForge, etc.) without explicit prior permission is strictly prohibited.

</details>

