#!/usr/bin/env bash
set -Eeuo pipefail

# Keep this version synchronized with gradle/wrapper/gradle-wrapper.properties.
VERSION="9.3.1"
CACHE="${GRADLE_USER_HOME:-$HOME/.gradle}/aurexis-wrapper"
DIST="$CACHE/gradle-$VERSION"
ARCHIVE="$CACHE/gradle-$VERSION-bin.zip"

if [ ! -x "$DIST/bin/gradle" ]; then
  mkdir -p "$CACHE"
  command -v curl >/dev/null 2>&1 || { echo "ERROR: curl is required."; exit 1; }
  command -v unzip >/dev/null 2>&1 || { echo "ERROR: unzip is required."; exit 1; }
  curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ARCHIVE"
  unzip -q -o "$ARCHIVE" -d "$CACHE"
fi

exec "$DIST/bin/gradle" "$@"
