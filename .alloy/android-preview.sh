#!/usr/bin/env bash
# Runs inside the "android" compose service: installs SDK + Gradle, builds the
# debug APK, renders the Compose UI via Roborazzi, and serves a preview page.
set -euo pipefail
PORT="${PORT:-3000}"
export ANDROID_HOME=/opt/android-sdk GRADLE_USER_HOME=/cache/gradle
OUT=/workspace/.alloy/preview
mkdir -p "$OUT" /cache
cat > "$OUT/index.html" <<'H'
<html><body style="font-family:sans-serif;padding:24px"><h1>OneClick AI Post</h1><p>Building Android app... refresh in a few minutes.</p></body></html>
H
(cd "$OUT" && python3 -m http.server "$PORT" --bind 0.0.0.0) &
apt-get update -qq && apt-get install -y -qq unzip curl python3 >/dev/null
GV=9.3.1
if [ ! -x /cache/gradle-$GV/bin/gradle ]; then
  curl -sSL -o /tmp/g.zip https://services.gradle.org/distributions/gradle-$GV-bin.zip && unzip -q /tmp/g.zip -d /cache
fi
if [ ! -d /cache/sdk/cmdline-tools/latest ]; then
  curl -sSL -o /tmp/c.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
  mkdir -p /cache/sdk/cmdline-tools && unzip -q /tmp/c.zip -d /cache/sdk/cmdline-tools && mv /cache/sdk/cmdline-tools/cmdline-tools /cache/sdk/cmdline-tools/latest
fi
ln -sfn /cache/sdk $ANDROID_HOME
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null || true
cd /workspace
echo "sdk.dir=$ANDROID_HOME" > local.properties
[ -f debug.keystore ] || keytool -genkeypair -keystore debug.keystore -storepass android -keypass android \
  -alias androiddebugkey -keyalg RSA -validity 10000 -dname "CN=Android Debug,O=Android,C=US" >/dev/null
G=/cache/gradle-$GV/bin/gradle
$G --no-daemon :app:assembleDebug
$G --no-daemon :app:testDebugUnitTest --tests '*Screenshot*' -Proborazzi.test.record=true || true
cp app/build/outputs/apk/debug/*.apk "$OUT/app-debug.apk"
cp app/src/test/screenshots/*.png "$OUT/" 2>/dev/null || true
{
  echo '<html><head><title>OneClick AI Post</title></head><body style="font-family:sans-serif;padding:24px;background:#f5f5f5">'
  echo '<h1>OneClick AI Post (Android)</h1><p>Rendered with Robolectric/Roborazzi from the Compose UI. <a href="app-debug.apk">Download debug APK</a></p>'
  for f in "$OUT"/*.png; do n=$(basename "$f"); echo "<figure><img src=\"$n?t=$(date +%s)\" style=\"max-height:85vh;border:1px solid #ccc\"><figcaption>$n</figcaption></figure>"; done
  echo '</body></html>'
} > "$OUT/index.html.tmp" && mv "$OUT/index.html.tmp" "$OUT/index.html"
echo "PREVIEW READY on :$PORT"
wait
