# AUREXIS Browser 2.0 — Repaired Gecko Edition

This package preserves the supplied AUREXIS Browser 2.0 Android application source, including its Java MainActivity, GeckoView-based rendering, navigation, tab strip, Mozilla Add-ons catalogue/search, signed-XPI installation flow, dark AUREXIS styling, Android manifest and desktop-shell launcher integration.

## What was repaired / added

- Pinned the build to **Gradle 8.9**, compatible with the project's Android Gradle Plugin 8.7.3.
- Added a lightweight gradlew bootstrap script and wrapper properties.
- Hardened build.sh with JDK 17 and Android SDK Platform 35 / Build-Tools checks.
- Added a GitHub Actions workflow that builds the ARMv7 APK on a supported x86-64 Linux runner.
- Kept the original app Java source and manifest unchanged.

## Build using GitHub Actions

Open the repository's Actions tab, select **Build AUREXIS Browser 2 (ARMv7)**, and run the workflow. Download artifact **AUREXIS-Browser-2.0-ARMv7-APK** after a successful run.

## Local build

Requirements: JDK 17, Android SDK Platform 35, Build-Tools 35.x and internet access to Google Maven, Maven Central and Mozilla Maven.

Run:
```bash
chmod +x gradlew build.sh install.sh
./build.sh
./install.sh
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Desktop integration

Run `bash ./integrate-desktop.sh` after installing the APK. It creates a launcher that starts the Android activity. This is Android Activity integration, not an X11-native Gecko widget.

## Scope and limitations

Included: GeckoView session/runtime, navigation, multiple in-app tabs, Mozilla Add-ons search and signed-XPI installation flow. Bookmarks/history/download manager, private-window isolation UI, ad blocking, complete desktop Firefox add-on compatibility, tab restore and expanded permission UX remain outside this initial scope.

The source README reports ARMv7 and about 1.8 GB RAM, below Mozilla's stated 2 GB baseline. Architecture compatibility does not guarantee performance.

## Honest build status

This is source code, not a precompiled or device-tested APK. Compilation and device testing remain necessary.
