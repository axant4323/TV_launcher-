# Device Cleanup & Custom Google TV Launcher: Complete Project Documentation

**Project Name**: Safe Device-Specific Android TV Cleanup & Minimal Launcher  
**Target Device**: Xiaomi Mi Box 4 (`MIBOX4`, Codename: `oneday`)  
**Network / ADB Address**: `192.168.1.8:5555`  
**OS Version**: Android 9 (API 28, Build: `PI.3933 release-keys`, Security Patch: `2021-07-05`)  
**Execution Date**: October 28, 2026  
**Status**: Cleaned (11 Bloatware Packages Disabled); Stock Factory Launcher Re-enabled and Restored as Default Home

---

## 1. Executive Summary & Safety Policy

This project executed a safe, zero-root, non-destructive debloating workflow and replaced the stock, ad-heavy launcher on a Xiaomi Mi Box 4 with a lightweight, private, Google TV style custom launcher.

### Core Safeguards Enforced:
- **Zero Root / Zero Bootloader Modification**: No `su`, no bootloader unlocking, no custom recovery, no firmware flashing.
- **Unmodified System Partition**: No `adb remount` or file deletion in `/system` or `/vendor`. The read-only factory OS partition remains 100% intact.
- **User-Space Isolation**: All modifications use `pm disable-user --user 0 <PACKAGE>`, restricting changes exclusively to user profile 0.
- **100% Reversibility**: Any disabled package (including the factory stock launcher) can be re-enabled instantly with zero data loss.
- **Preserved Critical Hardware Services**: Bluetooth BLE drivers, Amlogic SoC drivers, Chromecast built-in, and Xiaomi OTA update services were strictly protected.

---

## 2. Target Device Hardware & Audit Snapshot

Prior to making any changes, a complete, dated snapshot was exported to:
[`backups/audit_192.168.1.8_5555_20261028_161143/`](file:///home/akshant/Adithya_tv/backups/audit_192.168.1.8_5555_20261028_161143/)

### Audit Inventory:
- **Total Installed Packages**: 99
- **System Packages**: 86
- **Third-Party / User Packages**: 13
- **Identified Bloatware Candidates**: 11
- **Factory Default Launcher**: `com.google.android.tvlauncher/.MainActivity`
- **Audit Manifest**: [`audit_manifest.json`](file:///home/akshant/Adithya_tv/backups/audit_192.168.1.8_5555_20261028_161143/audit_manifest.json)
- **Classified Safety Report**: [`audit_report.md`](file:///home/akshant/Adithya_tv/backups/audit_192.168.1.8_5555_20261028_161143/audit_report.md)

---

## 3. Debloating & Telemetry Removal Log

All 11 candidate packages were sequentially disabled for primary user 0. Automated TV responsiveness checks verified that the TV kernel and shell responded normally after each operation.

| # | Package ID | Visible Name | Role & Reason for Removal | Status | Rollback Command |
| :-: | :--- | :--- | :--- | :--- | :--- |
| **1** | `com.miui.tv.analytics` | Xiaomi TV Analytics | Background device telemetry & user behavior logging | `disabled-user` | `adb shell pm enable com.miui.tv.analytics` |
| **2** | `tv.alphonso.alphonso_eula` | Alphonso ACR Tracker | Automated Content Recognition SDK (fingerprints TV audio/video to serve ads) | `disabled-user` | `adb shell pm enable tv.alphonso.alphonso_eula` |
| **3** | `com.mitv.tvhome.atv` | PatchWall ATV Integration | Injects Xiaomi PatchWall sponsored rows into Android TV Home | `disabled-user` | `adb shell pm enable com.mitv.tvhome.atv` |
| **4** | `com.mitv.tvhome.michannel` | Xiaomi Mi Channel | Promotional live channel aggregator and ad banner service | `disabled-user` | `adb shell pm enable com.mitv.tvhome.michannel` |
| **5** | `com.mitv.tvhome.mitvplus` | Xiaomi Mi TV+ | Xiaomi promotional live channel streaming daemon | `disabled-user` | `adb shell pm enable com.mitv.tvhome.mitvplus` |
| **6** | `com.xiaomi.android.tvsetup.partnercustomizer` | Xiaomi Partner Customizer | Auto-installs and recommends sponsored third-party apps | `disabled-user` | `adb shell pm enable com.xiaomi.android.tvsetup.partnercustomizer` |
| **7** | `android.autoinstalls.config.xioami.mibox3` | Auto-Install Config | Xiaomi partner auto-install configuration stub | `disabled-user` | `adb shell pm enable android.autoinstalls.config.xioami.mibox3` |
| **8** | `com.xm.webcontent` | Xiaomi Web Content | Web asset loader for promotional banners in Xiaomi menus | `disabled-user` | `adb shell pm enable com.xm.webcontent` |
| **9** | `com.mitv.videoplayer` | Xiaomi Mi Video Player | Duplicate media player (Nova Video Player & streaming apps active) | `disabled-user` | `adb shell pm enable com.mitv.videoplayer` |
| **10**| `com.mitv.download.service` | Xiaomi Download Service | Background download daemon used by Xiaomi promotional store | `disabled-user` | `adb shell pm enable com.mitv.download.service` |
| **11**| `mitv.service` | Xiaomi MiTV Core Service | Background service supporting PatchWall features | `disabled-user` | `adb shell pm enable mitv.service` |
| **12**| `com.google.android.tvlauncher` | Stock Android TV Home | Factory default launcher (Re-enabled and restored as active default Home app) | `enabled` (Active Default) | `adb shell pm disable-user --user 0 com.google.android.tvlauncher` |

Rollback Log File:  
[`backups/rollback_192.168.1.8_5555_20261028_161548.json`](file:///home/akshant/Adithya_tv/backups/rollback_192.168.1.8_5555_20261028_161548.json)

---

## 4. Protected Services & Features Decision Log

The following critical services were identified and placed on the **Strict Never-Touch List**:

| Package / Service | Classification | Rationale & Protection Policy |
| :--- | :--- | :--- |
| `com.droidlogic.ble` & `com.android.bluetooth` | **Core Hardware** | **CRITICAL**: The physical Mi Box remote is exclusively Bluetooth Low Energy (BLE). Disabling Bluetooth permanently disconnects the remote, requiring an external USB mouse to recover. |
| `com.droidlogic` & `com.droidlogic.overlay` | **Core Hardware** | Amlogic SoC hardware abstraction, HDR display pipeline, and resolution controllers. Must never be touched. |
| `com.google.android.apps.mediashell` | **Ecosystem Feature** | Chromecast built-in receiver. Preserved to maintain phone/laptop casting. |
| `com.google.android.katniss` | **Ecosystem Feature** | Google Assistant / Voice Search bridge for the remote microphone button. Preserved. |
| `com.xiaomi.mitv.updateservice` | **System Maintenance** | Official Xiaomi OTA firmware update daemon. Preserved per user request. |
| `com.netflix.ninja`, `in.startv.hotstar`, `com.amazon.amazonvideo.livingroom`, `tv.airtel.xstream.tvapp`, `com.jio.media.stb.ondemand`, `com.sonyliv`, `com.google.android.youtube.tv` | **User Apps** | Prioritized streaming applications. Preserved and pinned to the launcher. |

---

## 5. Custom Google TV Launcher Architecture

A custom Android TV launcher built specifically for this box using **Kotlin**, **Jetpack Compose Material 3**, and **Gradle 8.7**.

### Component Hierarchy:
```
launcher/
├── app/
│   ├── build.gradle.kts                   # AGP 8.4.0, Compose BOM 2024.04.01, Coil 2.6.0
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml        # LEANBACK_LAUNCHER & HOME category intents
│       │   └── java/com/adithyatv/launcher/
│       │       ├── MainActivity.kt        # Lifecycle, tab routing, D-pad back handler
│       │       ├── data/
│       │       │   ├── AppModel.kt        # App metadata & key models
│       │       │   ├── AppRepository.kt   # Discovery & priority favorites sorting
│       │       │   ├── InputModel.kt      # Hardware HDMI / AV input models
│       │       │   ├── InputRepository.kt # TV input manager integration
│       │       │   ├── PreferencesRepository.kt # Ordered favorites & UI scaling persistence
│       │       │   ├── ReleaseModel.kt    # Media release metadata model
│       │       │   └── ReleaseRepository.kt # 2024 releases catalog for Netflix, Prime, etc.
│       │       └── ui/
│       │           ├── components/
│       │           │   ├── DpadFocusableCard.kt # Scale (1.05x), cyan glow, remote key events
│       │           │   ├── GoogleTvAppPill.kt   # 'Your Apps' squircle icon cards
│       │           │   ├── GoogleTvHeader.kt    # Branding, top tabs (For you, Live, Apps, etc.)
│       │           │   ├── HeroSpotlightBanner.kt # Full-width cinematic backdrop & Watch action
│       │           │   ├── InputCard.kt         # HDMI 1, HDMI 2, AV switcher cards
│       │           │   └── MediaReleaseCard.kt  # 16:9 release card with backdrop synchronization
│       │           ├── screens/
│       │           │   ├── GoogleTvHomeScreen.kt # Main Google TV home screen
│       │           │   ├── AllAppsScreen.kt     # App grid with 1-click favorite dialog
│       │           │   ├── InputsScreen.kt      # TV video sources screen
│       │           │   ├── SearchScreen.kt      # Real-time search filter
│       │           │   └── SettingsScreen.kt    # System Settings link, UI scaling, recovery
│       │           └── theme/
│       │               ├── Color.kt             # Dark slate (#0B0E14), cyan focus (#58A6FF)
│       │               ├── Theme.kt             # Material 3 dark color scheme
│       │               └── Type.kt              # 10-foot viewing distance typography
│       └── test/                                # JUnit 4 unit tests
```

### Key Interactive Features:
1. **Dynamic Hero Spotlight Synchronization**:
   - Moving focus across any movie/show card below automatically updates the top spotlight banner's backdrop, title, and synopsis.
2. **Pinned Favorites**:
   - Pinned in exact priority order: **Netflix**, **Prime Video**, **Disney+ Hotstar**, **JioCinema**, **Airtel Xstream**, **YouTube**, **Sony LIV**.
3. **D-pad Remote Remote Control Fix**:
   - Physical D-pad Center key events (`KEYCODE_DPAD_CENTER`, `KEYCODE_ENTER`) are intercepted at the hardware level.
   - Long-press holding for ~450ms directly toggles favorite.
   - Clicking any app in the **Apps** tab opens an accessible TV dialog: **"Open Application"** | **"★ Add/Remove from Favorites"** | **"Cancel"**.
4. **Recent Releases Catalog**:
   - Dedicated release rows with high-res artwork for Netflix, Prime Video, Disney+ Hotstar, JioCinema, Airtel Xstream, and YouTube.
   - Clicking **"Watch on [App]"** or any release card launches that application immediately.

---

## 6. Default Launcher Configuration & Verification

The launcher was configured and verified as the default Home app on `192.168.1.8:5555`:

### Verification Checks Performed:
```bash
# 1. Resolve active home activity
adb -s 192.168.1.8:5555 shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
# Output:
# priority=0 preferredOrder=0 match=0x108000 specificIndex=-1 isDefault=true
# com.adithyatv.launcher.debug/com.adithyatv.launcher.MainActivity

# 2. Test physical Home button event
adb -s 192.168.1.8:5555 shell input keyevent KEYCODE_HOME
# Output:
# mFocusedApp=Token{... ActivityRecord{... u0 com.adithyatv.launcher.debug/com.adithyatv.launcher.MainActivity}}
# mCurrentFocus=Window{... u0 com.adithyatv.launcher.debug/com.adithyatv.launcher.MainActivity}
```

---

## 7. Operator Manual & Rollback Commands

All tools are located in `tools/` and are fully executable from the terminal.

### How to Re-enable Any Single Package:
```bash
adb -s 192.168.1.8:5555 shell pm enable <PACKAGE_NAME>
```

### How to Restore the Factory Android TV Home Launcher:
```bash
adb -s 192.168.1.8:5555 shell pm enable com.google.android.tvlauncher
```

### How to Perform a Full One-Click Rollback (Restores All 12 Packages):
```bash
python3 tools/rollback_device.py --log backups/rollback_192.168.1.8_5555_20261028_161548.json
```

### How to Recompile and Update the Launcher APK:
```bash
cd launcher
./gradlew assembleDebug
adb -s 192.168.1.8:5555 install -r app/build/outputs/apk/debug/app-debug.apk
```

### Emergency Fallback:
If the custom launcher is ever uninstalled (`adb uninstall com.adithyatv.launcher.debug`) and the factory launcher is re-enabled, the Mi Box immediately falls back to factory stock behavior with zero data loss.
