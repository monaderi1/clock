# Dual City Clock — Android Widget

Two big 24-hour live clocks on your Android Home screen. Default cities are Melbourne and Tehran; 42 built-in cities in total.

## Features
- Large live `HH:mm` clocks, no dates, no clutter.
- Adjustable clock font size (36–100sp) with live preview in settings.
- Vertical stacked layout (default, gives numbers more width) or horizontal side-by-side layout.
- Automatically scales numbers down if the widget's current size is too small. **Resize the Home widget to make the numbers truly bigger**.
- Choose two cities, swap their positions, and update all widgets. Time zones respect daylight-saving rules.
- No network connection, runtime permissions, alarm, or background service required. `TextClock` manages live updates.

## Build and install through GitHub Actions
1. Open the **Actions** tab of this GitHub repository.
2. Choose **Build Android APK** → **Run workflow** (a push to main also triggers a build).
3. Open the completed run; download the artifact **DualCityClock-APK**.
4. Extract and install `app-debug.apk`.
5. On your phone: long-press an empty area of the Home screen → **Widgets** → **Dual City Clock** → Add. Resize it to 4×3 or **4×4** for larger clocks.
6. Tap the widget to adjust city, font size, or stacked/side-by-side layout.

> Debug APKs produced on different GitHub Actions runners may have different signing keys. Uninstalling the previous debug build might be required to install a new one. For reliable upgrades, configure a persistent signing key.

## Build locally
Android Studio with JDK 17 and Android SDK 35; run `gradle assembleDebug` (Gradle 8.9). Minimum Android version: 8.0 (API 26).

## Implementation
Native Android Java app + `AppWidgetProvider` + `RemoteViews` + `TextClock`. Widget auto-resizing is handled using `onAppWidgetOptionsChanged` and available width/height. City preferences and desired clock size are stored locally.
