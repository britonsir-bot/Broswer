# AUREXIS Browser 2 — Gecko Edition

An Android-native GeckoView browser intended to be launched from the AUREXIS desktop shell. The browser engine is Mozilla GeckoView, not WebKitGTK or Android WebView.

## Verified target
- Device ABI: `armeabi-v7a` (ARMv7)
- Android: API 35 / Android 15
- GeckoView artifact: `org.mozilla.geckoview:geckoview-armeabi-v7a:160.0.20261104181552`
- minSdk 26
- Java 17, Android Gradle Plugin 8.7.3
- ABI filter restricts output to ARMv7.

## Important device limitation
The phone is Android 15 and ARMv7, but its reported total RAM is about 1.8 GB, below Mozilla's stated 2 GB baseline. Architecture compatibility does not guarantee performance.

## Build
This is Android source, not a prebuilt APK. Build on a machine with JDK 17, Android SDK platform 35, build-tools 35.x, Gradle 8.9+ and internet access.

```sh
bash ./build.sh
```

## Install APK
```sh
bash ./install.sh
```

## AUREXIS desktop integration
The install script writes `~/.local/bin/aurexis-browser2` and `~/.local/share/applications/aurexis-browser2.desktop`. The launcher calls Android Activity Manager; it is not a native X11 window.

## Current scope
Included: modern Android UI, Gecko rendering session, navigation, multiple in-app tabs, Mozilla Add-ons search and signed-XPI installation flow. Bookmarks/history/download manager, private-window isolation UI, ad blocking, full desktop Firefox add-on compatibility and tab restore are not yet included.
