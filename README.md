# Pollithium

Pollithium is a Fabric client-side Minecraft PvP utility mod. It focuses on readable information, HUD customization, and visual options—not automated combat or gameplay cheats.

## Current implementation

- FPS, ping, coordinates, and WASD keystrokes HUD.
- HUD visibility toggle (default: **F8**).
- HUD editor (default: **Right Shift**) with draggable preview and module toggles.
- HUD position and module settings saved to `config/pollithium.json`.
- Dark translucent HUD panel with an accent strip.

## Build

The first target is Minecraft **1.21.11** on Java 21.

1. Install JDK 21.
2. Install Gradle 8.14 or newer.
3. Clone this repository.
4. Run `gradle build`.
5. Find the mod JAR in `build/libs/`.

Use the Fabric Loader and Fabric API versions declared in `gradle.properties`.

## Version support

Minecraft versions are not all binary-compatible. Pollithium will use version-specific build targets rather than claiming one JAR works across every release. The initial target is 1.21.11; older 1.20.x releases and the newer 26.x line need their own compatibility work and testing.

## Planned

CPS, armor durability, potion timers, custom crosshair, toggle sprint, perspective/zoom controls, low-fire rendering, configurable colors, resizable HUD modules, and expanded editor controls.

## Safety / scope

Pollithium is a client-side utility mod. It will not implement aim assistance, autoclicking, reach changes, kill aura, velocity manipulation, or automated combat.

## License

MIT. See [LICENSE](LICENSE).
