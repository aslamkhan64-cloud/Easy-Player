# Easy Player — Native Android source (Kotlin + Jetpack Compose)

This is a **native Android application project**, not a WebView or website wrapper.

## Included in this build
- Soft neutral frosted-glass style UI (no neon/glow)
- Local audio library scan and folder grouping
- Favorites, recently played and most-played tracking
- Basic playlist-name creation and queue-based next/previous playback
- Background playback service and a basic playback notification
- GitHub Actions workflow to build a real Android debug APK in the cloud

## Build an APK from a phone using GitHub
1. Create a new GitHub repository and upload the **contents** of this folder (keep `.github/workflows/android-apk.yml`).
2. Open the repository's **Actions** tab.
3. Select **Build Easy Player APK** and tap **Run workflow** (or wait for the automatic build after pushing to `main`).
4. When the run finishes successfully, open that run and download the `Easy-Player-debug-APK` artifact. Extract it to get `app-debug.apk`.

The workflow installs JDK 17, Android SDK 35 and Gradle 8.9 in GitHub's hosted build environment. No Gradle wrapper or computer-side Android Studio is required.

## Important status
This is a working starter implementation, **not yet a fully finished production music player**. Remaining work includes adding/removing playlist tracks, a fully connected playback seek bar and progress updates, equalizer/bass boost, complete lock-screen/media-session controls, notification permission UX, and real-device testing. The workflow can compile the project, but a successful build has not been run in this chat environment.

## Technical details
- Application ID: `com.akm.easyplayer`
- Display name: `Easy Player`
- Minimum Android: 6.0 (API 23)
- Target / compile SDK: 35
- Android Gradle Plugin: 8.7.3
- Kotlin: 2.0.21
- Gradle: 8.9
- Build type: Debug (installable for testing; not a signed Play Store release)
