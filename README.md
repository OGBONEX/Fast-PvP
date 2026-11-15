# Pollithium

Pollithium is a client-side Fabric utility mod with a configurable HUD and visual customization. It is designed for information display and interface personalization.

## Current implementation

- FPS, server ping, XYZ coordinates, cardinal direction, and local clock.
- Left/right click-rate counter.
- Armor durability and active potion/effect information.
- WASD keystrokes display.
- Independent HUD widgets: each module has its own saved position and size.
- HUD editor with widget selection, drag-to-move, bottom-right drag-to-resize, and 8-pixel snapping.
- Visual alignment guides when a selected widget reaches the screen's horizontal or vertical center.
- Custom accent-color editor accepting six-digit RGB or eight-digit ARGB hexadecimal values.
- Dark translucent widget panels and saved settings in `config/pollithium.json`.
- Optional custom crosshair, low-fire display, and compact Shulker Box contents tooltip.
- Hold-to-zoom keybind (C by default), restoring the previous FOV when released.
- Optional toggle-sprint keybind (unbound by default; enable it in the editor first).
- F8 toggles the HUD; Right Shift opens the HUD editor.

## Current build target

The current source tree targets **Minecraft 1.21.11**, Fabric, and Java 21.

1. Install JDK 21.
2. Install Gradle 8.14.3 or newer.
3. Clone this repository.
4. Run `gradle clean build --no-daemon`.
5. Find the mod JAR in `build/libs/`.

GitHub Actions runs the 1.21.11 build on pushes and pull requests and uploads the JAR when the build succeeds. A successful CI run and an in-game test are still required before calling a release ready.

## Minecraft version compatibility

A single Fabric JAR cannot cover every Minecraft version. The compatibility status is documented in [versions/README.md](versions/README.md).

- 1.21.11 is the active source/build target.
- 1.20.x requires a separate compatibility port and build.
- 26.1 and later require a separate port to the unobfuscated Minecraft toolchain and updated Fabric APIs.

Do not install the 1.21.11 JAR into a different Minecraft version.

## Editor use

1. Press **Right Shift** to open the editor.
2. Use the **Selected** button to choose a widget.
3. Drag the selected preview to move it. Drag its bottom-right corner to resize it.
4. Use the HEX field with `RRGGBB` or `AARRGGBB` and press **Apply color**.
5. Close the editor to save settings.

Widgets that are currently positioned underneath the editor's left settings column are shown in a movable preview area on the right while editing. Their saved HUD coordinates are still updated by the same drag distance.

## Scope

Pollithium focuses on HUD information and visual customization. It does not include automated combat or combat hit-feedback features.

## License

MIT. See [LICENSE](LICENSE).
