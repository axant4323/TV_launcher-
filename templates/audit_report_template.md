# Android TV Device Audit & Package Safety Report Template

**Target Device:** `[Brand Model]` (`[Product Codename]`)
- **ADB Serial:** `[SERIAL_NUMBER]`
- **OS Version:** Android `[Version]` (API `[SDK]`)
- **Build ID:** `[Build ID]`
- **Security Patch Level:** `[YYYY-MM-DD]`
- **Audit Date & Time:** `[Timestamp]`
- **Active Home Application:** `[Current Launcher Package/Activity]`

---

## 1. Executive Summary & Safety Policy

This audit is completely **read-only**. No packages are modified, uninstalled, or disabled during the audit phase. 

### Core Safeguards:
- **Zero Root / Zero System Partition Alteration**: The system partition remains 100% read-only and untouched.
- **Reversible Space**: Modifications in Phase 2 only apply to user 0 via `pm disable-user --user 0`, keeping the underlying APK untouched in the OS partition.
- **100% Rollback Guarantee**: Any disabled package can immediately be re-enabled using `pm enable <PACKAGE>`.
- **Active TV Function Preservation**: Essential TV functions (HDMI inputs, CEC, Bluetooth remotes, Wi-Fi, audio, DRM) are strictly identified and protected.

---

## 2. Package Statistics

| Classification Metric | Count | Policy |
| :--- | :--- | :--- |
| **Total Packages Installed** | `[Count]` | Baseline inventory |
| **Active / Enabled Packages** | `[Count]` | Currently running or callable |
| **Already Disabled Packages** | `[Count]` | Previously turned off |
| **Core / System-Critical** | `[Count]` | **NEVER TOUCH** |
| **Keep unless Opted Out** | `[Count]` | Keep enabled (Play Store, Remotes, Cast, DRM, CEC) |
| **Candidate Bloatware** | `[Count]` | Safe to disable upon approval |
| **Unknown / Unclassified** | `[Count]` | Default to KEEP pending explicit review |

---

## 3. Package Inventory & Classification

### 3.1 Core / System-Critical (DO NOT TOUCH)
*These packages are vital for the Android OS kernel, System UI, remote control hardware input events, and display management. Disabling any of these can lead to black screens, non-responsive remotes, or bootloops.*

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `android` | Android System | Core OS & hardware abstraction | High | Do not touch | N/A | Critical |
| `com.android.systemui` | System UI | Status bar, notifications, overlays | High | Do not touch | N/A | Critical |
| `com.android.settings` | Settings | Core preference management | High | Do not touch | N/A | Critical |
| `com.android.tv.settings` | Android TV Settings | TV-optimized settings interface | High | Do not touch | N/A | Critical |
| `com.android.inputdevices` | Input Devices | Physical remote / key event routing | High | Do not touch | N/A | Critical |
| `com.android.shell` | ADB Shell Service | ADB debugging & command access | High | Do not touch | N/A | Critical |

---

### 3.2 Keep Unless Opted Out
*Provides ecosystem functionality such as Google Play, Google Cast (Chromecast built-in), Bluetooth remote pairing, TV tuner / Live TV, and Voice Search. Only disable if you have specifically confirmed you do not use that feature.*

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `com.google.android.gms` | Google Play Services | Auth, DRM tokens, Cast bridge | High | Keep | Reversible | High |
| `com.android.vending` | Google Play Store | App updates & installs | High | Keep | Reversible | Medium |
| `com.google.android.apps.mediashell`| Chromecast built-in | Cast target from phones/laptops | High | Keep | Reversible | Medium |
| `com.android.bluetooth` | Bluetooth Service | Bluetooth remote controls & audio | High | Keep | Reversible | Critical (if BT remote) |
| `com.google.android.katniss` | Google Assistant | Voice search & remote mic key | High | Keep | Reversible | Low/Medium |
| `com.google.android.tvlauncher` | Stock Android TV Home | Stock home screen | High | Keep until new launcher verified | Reversible | Medium |

---

### 3.3 Candidate Bloatware (Pending Approval)
*Vendor promotional engines, preloaded duplicate stores, demo loops, and telemetry agents. These can be safely disabled one-by-one using `pm disable-user --user 0`.*

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `[e.g. com.vendor.analytics]` | Vendor Analytics | Telemetry & ad tracking | High | `pm disable-user --user 0` | 100% Reversible | Low |
| `[e.g. com.vendor.demomode]` | Retail Demo Mode | In-store promotional loop | High | `pm disable-user --user 0` | 100% Reversible | Low |

---

### 3.4 Unknown / Unclassified Packages
*Default conservative stance: Leave unchanged. Only inspect if suspected of causing performance degradation.*

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `[com.unknown.pkg]` | `[Name]` | Unclassified | Low | Leave unchanged | N/A | Unknown |

---

## 4. Phase 2 Proposed Cleanup Plan & Safety Gate

To execute modifications for approved candidate packages:
1. Export approved actions into `cleanup_plan.json`.
2. Run approval-gated cleanup with explicit device serial and safety token:
   ```bash
   python3 tools/cleanup_device.py --serial [TARGET_SERIAL] --confirm CONFIRM_APPLY --plan cleanup_plan.json
   ```
3. To reverse all changes at any time:
   ```bash
   python3 tools/rollback_device.py --log backups/rollback_[SERIAL]_[TIMESTAMP].json
   ```
