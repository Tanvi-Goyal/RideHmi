#!/usr/bin/env bash
# Renders every dashboard look on a running emulator and saves them to docs/screenshots.
set -euo pipefail

cd "$(dirname "$0")/.."
TEST_PACKAGE=com.ridehmi.feature.dashboard.ui.test
DEVICE_DIR=/sdcard/Android/data/$TEST_PACKAGE/files/screenshots
OUT=docs/screenshots

# The dashboard is a landscape screen; lock the device to landscape while capturing.
adb shell settings put system accelerometer_rotation 0
adb shell settings put system user_rotation 1

./gradlew -q :feature:dashboard:ui:installDebugAndroidTest
adb shell rm -rf "$DEVICE_DIR"
adb shell am instrument -w \
  -e class com.ridehmi.feature.dashboard.ui.DashboardScreenshotCapture \
  "$TEST_PACKAGE/androidx.test.runner.AndroidJUnitRunner"

mkdir -p "$OUT"
adb pull "$DEVICE_DIR/." "$OUT/"
adb uninstall "$TEST_PACKAGE" >/dev/null
echo "Saved to $OUT"
