#!/usr/bin/env bash
set -Eeuo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
if [ ! -s "$APK" ]; then echo "APK not found. Run ./build.sh first."; exit 1; fi
if command -v adb >/dev/null 2>&1; then adb install -r "$APK"
elif command -v termux-open >/dev/null 2>&1; then termux-open "$APK"
else echo "APK ready: $APK"; fi
