# Flag Master

Flag Master is a fully offline Android app for learning the flags of the world. It covers every country (UN members plus widely recognized states and US/Canadian territories), all US state flags, and every Canadian provincial and territorial flag. No network access, no accounts, no API keys.

## Features

- **Quiz mode** — choose any combination of three flag sets (world countries, US states, Canadian provinces/territories). Flags are shown one at a time; you type the full name and press **Guess**. As you type, the single closest matching name appears below the field so you can confirm spelling, but it never autofills and never affects scoring. A running score sits in the top-right corner.
- **Spelling counts** — the answer must match exactly (case-insensitive, trimmed). Close-but-wrong answers are marked wrong.
- **Keep going until you've got them all** — a wrong flag is reshuffled back into the queue a few positions later (never immediately), so you keep cycling until the whole selected set is complete.
- **Explore mode** — tap any flag to see where the country/region is on an offline world map, view its flag, and read a short fun fact.

## Download the APK

Every push to `main` builds the app in GitHub Actions. You can grab the APK two ways:

1. **From a build's artifacts** — open the **Actions** tab, click the latest successful **Android Build** run, and download the `flag-master-debug-apk` artifact (a zip containing `app-debug.apk`).
2. **From the `latest` release** — open the **Releases** page and download `app-debug.apk` from the release tagged `latest`, which is refreshed on every successful `main` build.

## Install on a Samsung Galaxy S21 (sideload)

1. Transfer `app-debug.apk` to the phone — by USB cable, or download it directly on the device from the Releases page.
2. Open **My Files** (or Chrome's downloads) and tap `app-debug.apk`.
3. Android will prompt that the installing app (My Files / Chrome) isn't allowed to install unknown apps. Tap **Settings**, then enable **Allow from this source** for that app. (Also at *Settings → Apps → [installer app] → Install unknown apps*.)
4. Go back and tap **Install**, then **Open** when it finishes.

This is a debug build, so Android may warn that it's from an unknown developer — that's expected for sideloaded APKs.
