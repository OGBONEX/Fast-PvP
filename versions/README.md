# Minecraft compatibility targets

This file distinguishes the current tested build target from future compatibility work. The repository currently contains one shared source tree; changing dependency versions alone does not make that source compatible with every Minecraft release.

| Minecraft line | Status | Toolchain notes |
|---|---|---|
| 1.20.x | Not ported | Requires its own dependency profile and source/API compatibility work. |
| 1.21.11 | Current target | Java 21, Fabric Loom remap plugin, Yarn mappings, Gradle 9.2.0 workflow. |
| 26.1–26.3 | Not ported | Requires a separate migration to official unobfuscated names, the non-remapping Loom plugin, updated Fabric API names, and a newer Java/Gradle toolchain. |

## Why these are separate builds

Minecraft 26.1 is the first unobfuscated release. Fabric's migration notes state that Yarn and Intermediary are not planned to continue after 1.21.11, and that mods interacting with Minecraft code must be recompiled for the unobfuscated release. Fabric's 26.1 guidance uses Loom 1.15, Gradle 9.4, and Java 25; the 26.3 guidance lists Loom 1.19 and Gradle 9.6. The Fabric API names also changed during the 26.1 transition.

References:
- [Fabric: 1.21.11 mapping transition](https://www.fabricmc.net/2025/12/05/12111.html)
- [Fabric: 26.1 toolchain and API changes](https://fabricmc.net/2026/03/14/261.html)
- [Fabric: 26.3 toolchain guidance](https://fabricmc.net/2026/09/15/263.html)

## Release rule

A target must not be marked supported until its own dependencies resolve, the project compiles in CI, the mod launches in that Minecraft version, and the HUD/editor/mixins are checked in-game. The current workflow only builds 1.21.11. No 1.20.x or 26.x artifact is currently produced.
