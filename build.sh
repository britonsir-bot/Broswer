#!/usr/bin/env bash
set -Eeuo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"
echo "=== AUREXIS Browser 2.0 / ARMv7 build ==="
if ! command -v java >/dev/null 2>&1; then echo "ERROR: JDK 17 is required."; exit 1; fi
JAVA_MAJOR="$(java -version 2>&1 | sed -n '1s/.*version "\([0-9]*\).*/\1/p')"
if [ "$JAVA_MAJOR" != "17" ]; then echo "ERROR: JDK 17 required; detected ${JAVA_MAJOR:-unknown}."; exit 1; fi
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$SDK" ]; then echo "ERROR: Android SDK is not configured. Use GitHub Actions on a supported host."; exit 2; fi
if [ ! -f "$SDK/platforms/android-35/android.jar" ]; then echo "ERROR: Android SDK Platform 35 missing."; exit 1; fi
if ! find "$SDK/build-tools" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | grep -q .; then echo "ERROR: Android Build-Tools missing."; exit 1; fi
export ANDROID_HOME="$SDK" ANDROID_SDK_ROOT="$SDK"
./gradlew --no-daemon --stacktrace :app:clean :app:assembleDebug
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
test -s "$APK"
echo "BUILD SUCCESS: $APK"
