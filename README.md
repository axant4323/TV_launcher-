# Safe Android TV Cleanup & Minimal Custom Launcher Project

A device-specific, non-destructive, and 100% reversible framework for auditing and reducing bloatware on Android TV / Google TV devices, combined with a lightweight, calm, D-pad optimized custom TV launcher.

---

## Table of Contents
1. [Core Safety & Non-Destructive Principles](#1-core-safety--non-destructive-principles)
2. [Prerequisites & ADB Connection](#2-prerequisites--adb-connection)
3. [Phase 1: Read-Only Device Audit](#3-phase-1-read-only-device-audit)
4. [Phase 2: Approval-Gated Reversible Cleanup](#4-phase-2-approval-gated-reversible-cleanup)
5. [Phase 3: Minimal Android TV Launcher](#5-phase-3-minimal-android-tv-launcher)
6. [Testing & Verification Checklist](#6-testing--verification-checklist)
7. [Rollback & Recovery Procedures](#7-rollback--recovery-procedures)
8. [Permissions Rationale](#8-permissions-rationale)
9. [Device-Specific Decision Log & Known Limitations](#9-device-specific-decision-log--known-limitations)

---

## 1. Core Safety & Non-Destructive Principles

- **No Root Required**: Operates strictly within standard Android Debug Bridge (ADB) permissions.
- **No System-Partition Modification**: No `adb remount`, no file deletion in `/system` or `/vendor`. The underlying read-only OS image remains completely untouched.
- **User-Space Only**: Modifications use `pm disable-user --user 0 <PACKAGE>` (or `pm uninstall --user 0 <PACKAGE>`). This isolates changes to primary user profile `0`.
- **Dual Confirmation Gating**: All destructive/modifying scripts require matching target device serial numbers and an explicit confirmation token (`--confirm CONFIRM_APPLY`).
- **One Package at a Time**: Packages are altered sequentially with automated TV health/responsiveness verification between each action.
- **Immediate Rollback Generation**: Every change writes exact reverse commands to an immutable JSON log file.
- **Conservative Fallback**: The factory stock launcher remains fully installed and can be restored at any time.

---

## 2. Prerequisites & ADB Connection

### Local System Requirements
- Linux / macOS / Windows with `adb` installed (`sudo apt install adb` or Android SDK Platform Tools).
- Java 17+ and Gradle 8.7+ (for building the launcher app).
- Python 3.8+ (for audit, report, and cleanup tools).

### Enabling ADB Debugging on Android TV / Google TV
1. On the TV, open **Settings** > **Device Preferences** (or **System**) > **About**.
2. Scroll to **Android TV OS build** (or **Build Number**) and press the D-pad **Center** button 7 times until you see `"You are now a developer!"`.
3. Go back to **Settings** > **System** > **Developer options**.
4. Enable **USB debugging** (and **Network debugging / Wireless debugging** if connecting over Wi-Fi).

### Connecting to the TV
- **Via USB Cable**:
  Connect your computer to the TV's service/ADB port.
- **Via Wi-Fi / Ethernet**:
  Note the TV's IP address from **Settings > Network & Internet** (e.g. `192.168.1.150`).
  ```bash
  adb connect 192.168.1.150:5555
  ```
- **Authorize the Connection**:
  Look at the TV screen. When prompted with **"Allow USB debugging?"**, check **"Always allow from this computer"** and select **OK**.
- **Verify Connected Devices**:
  ```bash
  adb devices -l
  ```
  Expected output:
  ```text
  List of devices attached
  192.168.1.150:5555    device product:bcm72180 model:SmartTV device:generic_tv
  ```

---

## 3. Phase 1: Read-Only Device Audit

The audit script captures a complete, dated snapshot of your device's properties, installed packages, TV input configurations, and launcher handlers **without altering any device state**.

### Step 1: Run the Audit
```bash
python3 tools/audit_device.py --serial <YOUR_TV_SERIAL_OR_IP:5555>
```

This generates a dated backup directory under `backups/audit_<SERIAL>_<TIMESTAMP>/` containing:
- `adb_devices.txt`: Active ADB device listing and capabilities.
- `getprop.txt`: Full Android system build and hardware properties.
- `pm_packages_*.txt`: Installed packages (all, user 0, enabled, disabled, system, third-party).
- `launcher_home_role.txt`: Current default Home and Leanback launcher intent handlers.
- `dumpsys_tv_input.txt`: Hardware HDMI and AV video input port configurations.
- `settings_*.txt`: Global, secure, and system settings snapshots.
- `audit_manifest.json`: Structured JSON index of the entire audit.

### Step 2: Generate the Classified Safety Report
```bash
python3 tools/generate_audit_report.py --manifest backups/audit_<SERIAL>_<TIMESTAMP>/audit_manifest.json
```

This generates `audit_report.md` grouping all installed packages into four distinct risk tiers:
1. **Core / System-Critical (Never Touch)**:
   - Kernel abstraction, `android`, `com.android.systemui`, `com.android.settings`, `com.android.inputdevices` (remote hardware drivers), `com.android.shell`.
2. **Keep Unless Opted Out**:
   - Google Play Store (`com.android.vending`), Play Services (`com.google.android.gms`), Bluetooth service (`com.android.bluetooth`), Chromecast built-in (`com.google.android.apps.mediashell`), Live TV (`com.android.tv`), stock launcher.
3. **Candidate Bloatware**:
   - Vendor analytics, diagnostic loggers, promotional screensavers, duplicate stores, retail demo modes.
4. **Unknown / Unclassified**:
   - Conservative stance: left unchanged pending explicit user review.

---

## 4. Phase 2: Approval-Gated Reversible Cleanup

> [!CAUTION]
> Cleanup commands are strictly approval-gated. The cleanup tool **refuses to run** unless the device serial matches the connected hardware and the confirmation token is supplied.

### Step 1: Prepare the Approved Action Plan
Create a `cleanup_plan.json` listing only verified, user-approved candidate packages:
```json
{
  "target_serial": "192.168.1.150:5555",
  "actions": [
    {
      "package": "com.vendor.analytics",
      "action": "disable-user",
      "reason": "Unwanted background telemetry tracking"
    },
    {
      "package": "com.vendor.promotional.store",
      "action": "disable-user",
      "reason": "Unused secondary vendor store"
    }
  ]
}
```

### Step 2: Execute Approval-Gated Cleanup
```bash
python3 tools/cleanup_device.py \
  --serial <YOUR_TV_SERIAL_OR_IP:5555> \
  --confirm CONFIRM_APPLY \
  --plan cleanup_plan.json
```

### Safety Features During Execution:
- Executes **one package at a time**.
- Checks TV responsiveness after each package modification.
- If the TV shell fails to respond, it **immediately reverts** the last package and aborts.
- Writes a timestamped rollback manifest `backups/rollback_<SERIAL>_<TIMESTAMP>.json`.

---

## 5. Phase 3: Minimal Android TV Launcher

A standalone Android TV launcher built with Kotlin, Jetpack Compose for TV / Leanback, Material 3, and Gradle.

### Key Features:
- **Cinematic Google TV Interface**:
  - **Dynamic Hero Spotlight Banner**: Full-width cinematic backdrop with title, synopsis, metadata (`4K HDR`, `Dolby Atmos`, release year), and direct **"Watch on <App>"** button.
  - **Reactive Backdrop Animation**: Focusing on any movie or series card dynamically updates the top hero backdrop!
  - **Google TV Navigation Header**: Top navigation tabs (**For you**, **Live**, **Apps**, **Library**, **Search**, and quick Settings).
  - **"Your Apps" Squircle Row**: Google TV style icon cards for your favorite streaming apps (Netflix, Disney+ Hotstar, Prime Video, YouTube, Airtel Xstream, Sony LIV).
  - **Recent Releases Rows**: Curated recent releases categorized by favorite streaming services with high-resolution artwork and direct app launch.
  - **TV Video Inputs Row**: Direct switching for HDMI 1, HDMI 2, HDMI 3, AV, and Cable tuner.
  - **Zero Ads or Tracking**: No sponsored vendor carousels, 100% private.

### Building the Launcher APK
From the project root:
```bash
cd launcher
./gradlew assembleDebug
```
The compiled APK will be at:
`launcher/app/build/outputs/apk/debug/app-debug.apk`

### Testing on TV Without Replacing Stock Launcher
1. Install the APK over ADB:
   ```bash
   adb -s <SERIAL> install -r launcher/app/build/outputs/apk/debug/app-debug.apk
   ```
2. Launch the app directly as a standard activity:
   ```bash
   adb -s <SERIAL> shell am start -n com.adithyatv.launcher.debug/com.adithyatv.launcher.MainActivity
   ```
3. Test all features with the TV physical remote control (D-pad arrows, Center click, Long-press Center, Back key).

---

## 6. Testing & Verification Checklist

Before making the custom launcher default or disabling the stock home app, verify each item:
- [ ] **D-pad Navigation**: Arrow keys move focus smoothly with clear cyan borders.
- [ ] **Back Key Behavior**: Pressing Back on any sub-screen returns to Home; pressing Back on Home stays on Home without crashing.
- [ ] **App Launching**: Primary apps (YouTube, Netflix, Plex, etc.) launch and resume cleanly.
- [ ] **Favorites Management**: Long-pressing an app toggles the favorite badge and updates the Home row.
- [ ] **Physical TV Inputs**: Selecting HDMI 1 / HDMI 2 switches video source properly.
- [ ] **Native Settings Route**: Selecting "Android TV System Settings" successfully opens TV settings.
- [ ] **Wi-Fi & Bluetooth Remotes**: Bluetooth remote pairs, audio works, voice search button behaves predictably.

### Optionally Setting as Default Launcher
Once thoroughly tested:
1. Press the **Home** button on your physical TV remote.
2. The TV will display the **"Select a Home app"** dialog.
3. Select **Adithya TV Launcher** and choose **"Always"**.

---

## 7. Rollback & Recovery Procedures

Every change made by this project is **100% reversible**.

### Option A: Automated Rollback via Script
To restore all packages disabled or uninstalled in a session:
```bash
python3 tools/rollback_device.py --log backups/rollback_<SERIAL>_<TIMESTAMP>.json
```

### Option B: Single Package Manual Rollback
- Re-enable a disabled package:
  ```bash
  adb -s <SERIAL> shell pm enable <PACKAGE_NAME>
  ```
- Restore an uninstalled-for-user package:
  ```bash
  adb -s <SERIAL> shell cmd package install-existing <PACKAGE_NAME>
  ```

### Option C: Reverting to Stock Launcher
- Return to stock launcher via TV Settings:
  Navigate to **Settings > Apps > Special App Access > Default Home App** > select stock launcher.
- Return to stock launcher via ADB:
  ```bash
  # Example for Google TV:
  adb -s <SERIAL> shell cmd package set-home-activity com.google.android.apps.tv.launcherx/.home.HomeActivity
  # Example for standard Android TV:
  adb -s <SERIAL> shell cmd package set-home-activity com.google.android.tvlauncher/.MainActivity
  ```
- Emergency Fallback (Uninstalling Custom Launcher):
  ```bash
  adb -s <SERIAL> uninstall com.adithyatv.launcher.debug
  ```
  *(Android TV will automatically fall back to the factory launcher immediately)*

---

## 8. Permissions Rationale

The custom launcher requests only **one** permission in `AndroidManifest.xml`:
- `QUERY_ALL_PACKAGES`:
  Required by Google on Android 11+ (API 30+) strictly to allow home screen launchers to discover and display installed applications.
- **Zero unnecessary permissions**: The launcher does NOT request `INTERNET`, `ACCESS_FINE_LOCATION`, `READ_EXTERNAL_STORAGE`, or background telephony permissions.

---

## 9. Device-Specific Decision Log & Known Limitations

| Component | Policy | Rationale |
| :--- | :--- | :--- |
| **Bluetooth Remote Service (`com.android.bluetooth`)** | **NEVER DISABLE** | Disabling Bluetooth will disconnect wireless voice remotes, requiring an IR remote or USB mouse to recover. |
| **Chromecast built-in (`com.google.android.apps.mediashell`)** | **KEEP by default** | Disabling stops Google Cast receivers from mobile devices. |
| **TV Settings (`com.android.tv.settings`)** | **NEVER DISABLE** | Primary interface for configuring Wi-Fi, audio output, and inputs. |
| **DRM Services (Widevine)** | **NEVER DISABLE** | Disabling drops Netflix, Prime Video, and Disney+ streaming quality down to SD or causes playback errors. |
| **HDMI-CEC Services** | **NEVER DISABLE** | Disabling prevents remote control of connected soundbars, receivers, and consoles. |
