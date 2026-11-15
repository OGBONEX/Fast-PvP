# Pollithium

Pollithium is a client-side Fabric PvP utility mod with a Lunar-inspired configurable HUD. It is designed for information and visual customization, not automated combat or gameplay cheats.

## Implemented in the current source

- FPS counter.
- Server ping display.
- XYZ coordinates.
- Cardinal direction.
- Local clock.
- CPS counter for left and right mouse clicks.
- Armor durability values.
- Active potion/effect names, amplifier levels, and remaining time.
- WASD keystrokes display.
- Draggable HUD position with saved configuration.
- HUD editor toggles for each module.
- Dark translucent HUD panel and configurable accent/panel colors in config.
- Custom crosshair that replaces the vanilla crosshair when enabled.
- Hold-to-zoom keybind (C by default), restoring the previous FOV when released.
- Toggle sprint keybind (unbound by default; enable it in the HUD editor first).
- F8 toggles the HUD; Right Shift opens the HUD editor.
- Settings persist in `config/pollithium.json`.

## Build target

The first build target is Minecraft **1.21.11**, Fabric, and Java 21.

1. Install JDK 21.
2. Install Gradle 8.14.3 or newer.
3. Clone this repository.
4. Run `gradle clean build --no-daemon`.
5. Find the mod JAR in `build/libs/`.

A GitHub Actions workflow builds the project on pushes and pull requests and uploads the JAR as a workflow artifact when the build succeeds.

## Minecraft version coverage

A single Fabric JAR is not compatible with every Minecraft release. The initial target is 1.21.11. Separate build configurations and testing are required for 1.20.x and the newer 26.x versions. Those targets are not yet included.

## Still to implement

Resizable individual HUD widgets, drag alignment guides, a full color-picker/settings screen, low-fire rendering, custom hit sounds, configurable hit color, visual damage indicators, and dedicated compatibility builds for the rest of the requested Minecraft versions.

## Scope

No aim assistance, autoclicking, reach changes, kill aura, velocity manipulation, or automated combat features.

## License

MIT. See [LICENSE](LICENSE).


## New options added

The latest implementation adds:
- **Low fire** toggle in the Pollithium settings screen.
- **Shulker box tooltip** toggle. When enabled, hovering a shulker box item adds a compact contents list to its tooltip.
- HUD accent-color palette cycling controls.
- HUD position snapping to an 8-pixel grid while dragging.

Open the settings with **Right Shift**. Toggle the HUD with **F8**. Zoom is held with **C**.

These additions are committed to the repository, but they are not yet confirmed by a successful Gradle build or an in-game test. The advanced per-widget resize/independent placement editor and separately tested Minecraft-version builds are still outstanding. Combat hit-feedback enhancements are not included. Do not treat the project as release-ready until those checks pass.
