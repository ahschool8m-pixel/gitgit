# ReelBin
Counts Reels / Shorts you swipe through and shows a dustbin that fills up, pinned beside the front camera.

## Build
1. Open this folder in Android Studio (Koala or newer) and let Gradle sync.
2. Run on your phone (USB debugging on) or Build > Build APK.
3. Open ReelBin > "Enable ReelBin in Accessibility settings" > turn it on.
   (On Android 13+, if greyed out: App info > ⋮ > Allow restricted settings.)
4. Use the sliders to slide the bin next to your camera hole.

## Tuning
Counting relies on view IDs of Instagram / YouTube Shorts / Facebook reel screens
(`markers` list in ReelService.kt). If an app update breaks counting, adjust that list.
Count resets automatically every midnight.
