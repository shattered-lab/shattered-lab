# Shattered Lab

**[Download the latest release](https://github.com/shattered-lab/shattered-lab/releases/latest)**

Shattered Lab is a sandbox mod for [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) 4.0.2. Try any item, any monster and any build without a long run.

## Features

- **A library on floor 0.** Endless shelves hold every item in the game, sorted into rooms, with signs that show what is where.
- **The lab assistant's grimoire:**
  - workshop: level, enchantment, glyph, curse, strength requirement
  - cheats: god mode, one-hit kills, infinite charges and talents
  - a lift to any floor and monster summoning
  - floor tools, a hero editor, gear kits and run snapshots
- **An arena** with training dummies that count damage, a bestiary, a trap range, a garden and wells.
- **New items:** scroll of mega upgrade, scroll of degradation, scroll of polymorph, wand of everything, bag of holding and a library card.
- **Optional effects:** 3D sound and echo, dynamic light, and shader effects (glow, water reflections, screen effects). All are off by default.
- English, Russian and Ukrainian.

## Install

- **Android 5.0+:** `ShatteredLab-*.apk`
- **PC (Windows, macOS, Linux):** `ShatteredLab-*.jar`, needs Java 11+

The game checks this page for new versions and offers to download them.

## Crash reports

If the Android version crashes, it sends an anonymous crash report through Firebase Crashlytics so the bug can be fixed. You can turn this off in Settings, on the connection tab.

## Building

The project builds with Gradle, the same way as Shattered Pixel Dungeon: see [`docs/`](docs/).

The mod's code lives in `core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/hacks/`. Changes to the original game files are marked with `//SHD`.

## License

[GPLv3](LICENSE.txt), like the original game. Shattered Lab is based on [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) by Evan Debenham, which is based on [Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle) by Watabou. 3D sound and echo are adapted from [Warped Pixel Dungeon](https://github.com/GabriWar/warpedpd) by GabriWar, and the dynamic light and shader effects from [Reforged Pixel Dungeon](https://github.com/DenySilver/reforged-pixel-dungeon) by DenySilver, both GPLv3.

This is an unofficial mod and is not affiliated with Shattered Pixel.
