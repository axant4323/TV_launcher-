#!/usr/bin/env python3
"""
Generate Audit Report
Phase 1: Categorize packages and generate human-readable safety report.

Classifies packages into:
1. Core / System-Critical: Never touch without specific evidence.
2. Keep unless Opted Out: Play Store, TV Framework, Remotes, Bluetooth, CEC, DRM, Cast, Settings, Updates.
3. Candidate Bloatware: Vendor promotions, duplicate media apps, demos, games, unused vendor stores, analytics.
4. Unknown: Default to Keep, pending review.
"""

import argparse
import json
import os
import sys
from typing import Dict, List, Tuple

# Comprehensive knowledge base for Android TV packages
KNOWN_PACKAGE_ROLES: Dict[str, Dict[str, str]] = {
    # ------------------ Core / System Critical ------------------
    "android": {
        "name": "Android System",
        "category": "Core / System-Critical",
        "purpose": "Core operating system framework and hardware abstraction.",
        "risk": "Critical (Brick / Bootloop risk)",
        "action": "Do not touch"
    },
    "com.android.systemui": {
        "name": "System UI",
        "category": "Core / System-Critical",
        "purpose": "Status bar, notification manager, overlays, navigation bars.",
        "risk": "Critical (UI crash / Black screen)",
        "action": "Do not touch"
    },
    "com.android.settings": {
        "name": "Settings",
        "category": "Core / System-Critical",
        "purpose": "System settings app and core preference provider.",
        "risk": "Critical (Loss of device control)",
        "action": "Do not touch"
    },
    "com.android.tv.settings": {
        "name": "Android TV Settings",
        "category": "Core / System-Critical",
        "purpose": "TV-optimized settings UI (Wi-Fi, Bluetooth remotes, Display, Audio, Inputs).",
        "risk": "Critical (Loss of settings / setup navigation)",
        "action": "Do not touch"
    },
    "com.android.providers.settings": {
        "name": "Settings Storage",
        "category": "Core / System-Critical",
        "purpose": "Database storing global, secure, and system settings.",
        "risk": "Critical (Bootloop)",
        "action": "Do not touch"
    },
    "com.android.packageinstaller": {
        "name": "Package Installer",
        "category": "Core / System-Critical",
        "purpose": "Handles APK parsing and package installation dialogs.",
        "risk": "Critical (Cannot install or update any apps)",
        "action": "Do not touch"
    },
    "com.google.android.packageinstaller": {
        "name": "Google Package Installer",
        "category": "Core / System-Critical",
        "purpose": "System package installation interface.",
        "risk": "Critical (Cannot install or update apps)",
        "action": "Do not touch"
    },
    "com.android.permissioncontroller": {
        "name": "Permission Controller",
        "category": "Core / System-Critical",
        "purpose": "Manages runtime app permissions and security policies.",
        "risk": "Critical (App permission crashes)",
        "action": "Do not touch"
    },
    "com.google.android.permissioncontroller": {
        "name": "Permission Controller (Google)",
        "category": "Core / System-Critical",
        "purpose": "Manages runtime app permissions on Google TV / ATV.",
        "risk": "Critical (App permission crashes)",
        "action": "Do not touch"
    },
    "com.android.keychain": {
        "name": "Key Chain",
        "category": "Core / System-Critical",
        "purpose": "Cryptographic certificate and keystore management.",
        "risk": "High (Network and security failure)",
        "action": "Do not touch"
    },
    "com.android.inputdevices": {
        "name": "Input Devices",
        "category": "Core / System-Critical",
        "purpose": "Kernel input event routing for physical keys, gamepads, IR/RF remotes.",
        "risk": "Critical (Remote control loss)",
        "action": "Do not touch"
    },
    "com.android.hardware.input": {
        "name": "Hardware Input Service",
        "category": "Core / System-Critical",
        "purpose": "Hardware remote and peripheral input handler.",
        "risk": "Critical (Loss of D-pad / key input)",
        "action": "Do not touch"
    },
    "com.android.shell": {
        "name": "ADB Shell Service",
        "category": "Core / System-Critical",
        "purpose": "Required for ADB connection, debugging, and terminal commands.",
        "risk": "Critical (Loss of ADB debugging access)",
        "action": "Do not touch"
    },

    # ------------------ Keep unless Opted Out ------------------
    "com.google.android.gms": {
        "name": "Google Play Services",
        "category": "Keep unless Opted Out",
        "purpose": "Essential for Google login, Play Store, DRM tokens, Cast authentication, push notifications.",
        "risk": "High (App crashes, broken Play Store and Cast)",
        "action": "Keep unless fully de-Googling"
    },
    "com.google.android.gsf": {
        "name": "Google Services Framework",
        "category": "Keep unless Opted Out",
        "purpose": "Cloud communication framework for Google services.",
        "risk": "High (Google Play breaks)",
        "action": "Keep unless fully de-Googling"
    },
    "com.android.vending": {
        "name": "Google Play Store",
        "category": "Keep unless Opted Out",
        "purpose": "Official app store and app updater.",
        "risk": "Medium (Cannot update or download apps via store)",
        "action": "Keep unless using sideload-only workflow"
    },
    "com.google.android.apps.mediashell": {
        "name": "Chromecast built-in",
        "category": "Keep unless Opted Out",
        "purpose": "Receiver for Google Cast from phones, tablets, and browsers.",
        "risk": "Medium (Disables wireless casting / screen mirroring)",
        "action": "Keep if Chromecast is needed"
    },
    "com.android.bluetooth": {
        "name": "Bluetooth System Service",
        "category": "Keep unless Opted Out",
        "purpose": "Bluetooth audio, BLE TV remotes, headphones, gamepads.",
        "risk": "Critical if using Bluetooth remote (Remote will disconnect)",
        "action": "Keep unless TV uses only IR remote and no BT audio"
    },
    "com.google.android.tv.remote.service": {
        "name": "Android TV Remote Service",
        "category": "Keep unless Opted Out",
        "purpose": "Companion remote app support (Google TV phone app, smart remote bridge).",
        "risk": "Low/Medium (Phone remote apps won't connect)",
        "action": "Keep if using phone virtual remote"
    },
    "com.google.android.katniss": {
        "name": "Google Assistant for Android TV",
        "category": "Keep unless Opted Out",
        "purpose": "Voice search, microphone button handling on remote, Assistant commands.",
        "risk": "Low/Medium (Remote mic button won't respond)",
        "action": "Disable only if voice search is not desired"
    },
    "com.google.android.tvlauncher": {
        "name": "Android TV Home Launcher",
        "category": "Keep unless Opted Out",
        "purpose": "Stock stock Android TV Home screen and channel rows.",
        "risk": "Medium (Black screen on Home button if no custom launcher is default)",
        "action": "Keep enabled until custom launcher is installed and tested"
    },
    "com.google.android.apps.tv.launcherx": {
        "name": "Google TV Launcher",
        "category": "Keep unless Opted Out",
        "purpose": "Stock Google TV home screen with recommendations.",
        "risk": "Medium (Black screen on Home button if no replacement launcher)",
        "action": "Keep enabled until custom launcher is installed and tested"
    },
    "com.google.android.tvrecommendations": {
        "name": "Android TV Core Services / Recommendations",
        "category": "Keep unless Opted Out",
        "purpose": "Row recommendations aggregator and provider for stock launcher.",
        "risk": "Low (Stock launcher rows may stop updating)",
        "action": "Optional: Candidate for disable once custom launcher is active"
    },
    "com.android.tv": {
        "name": "Live TV App",
        "category": "Keep unless Opted Out",
        "purpose": "Tuner, Antenna, Cable, and TV Input service front-end.",
        "risk": "Low/Medium (Disables built-in antenna/cable tuner viewer)",
        "action": "Keep if using RF Antenna / Cable TV tuner"
    },
    "com.android.providers.media": {
        "name": "Media Storage",
        "category": "Keep unless Opted Out",
        "purpose": "Indexes local video, audio, and images on storage/USB.",
        "risk": "Medium (Local media players cannot index files)",
        "action": "Keep for local playback support"
    },
    "com.android.providers.downloads": {
        "name": "Download Manager",
        "category": "Keep unless Opted Out",
        "purpose": "Background file downloads for web browsers, Play Store, and sideloaders.",
        "risk": "High (Download failures across apps)",
        "action": "Keep"
    },
    "com.google.android.marvin.talkback": {
        "name": "Android Accessibility Suite (TalkBack)",
        "category": "Keep unless Opted Out",
        "purpose": "Screen reader, accessibility services, switch access.",
        "risk": "Low (No effect unless accessibility is enabled)",
        "action": "Keep unless user explicitly opts out"
    },
    "com.google.android.tungsten.setupwraith": {
        "name": "Android TV Setup Wraith",
        "category": "Keep unless Opted Out",
        "purpose": "Initial device out-of-box setup wizard.",
        "risk": "Low after initial setup; needed during factory reset",
        "action": "Leave enabled or disable only post-setup"
    },

    # Xiaomi Mi Box SoC & Hardware Drivers (DO NOT TOUCH)
    "com.droidlogic": {
        "name": "Amlogic System Service",
        "category": "Core / System-Critical",
        "purpose": "Hardware driver and SoC platform framework for Amlogic processor.",
        "risk": "Critical (Hardware brick risk / system freeze)",
        "action": "Do not touch"
    },
    "com.droidlogic.ble": {
        "name": "Amlogic Bluetooth Remote Driver",
        "category": "Core / System-Critical",
        "purpose": "Essential low-level driver for the Xiaomi Bluetooth remote control.",
        "risk": "Critical (Mi Box Bluetooth Remote will disconnect permanently)",
        "action": "Do not touch"
    },
    "com.droidlogic.overlay": {
        "name": "Amlogic Display Overlay",
        "category": "Core / System-Critical",
        "purpose": "Hardware video resolution, refresh rate, and HDR overlay driver.",
        "risk": "Critical (Display artifacts / black screen)",
        "action": "Do not touch"
    },
    "com.xiaomi.mitv.res": {
        "name": "Xiaomi System Framework Overlay",
        "category": "Core / System-Critical",
        "purpose": "Hardware resource overlay for Xiaomi system UI elements.",
        "risk": "High (UI rendering crash)",
        "action": "Do not touch"
    },
    "com.xiaomi.mitv.updateservice": {
        "name": "Xiaomi System OTA Updater",
        "category": "Keep unless Opted Out",
        "purpose": "Official Xiaomi firmware and security patch OTA update service.",
        "risk": "Medium (Disables firmware and security updates)",
        "action": "Keep (User requested to preserve OTA updates)"
    },

    # User-Requested Apps (Keep as Favorites)
    "com.netflix.ninja": {
        "name": "Netflix for Android TV",
        "category": "Keep unless Opted Out",
        "purpose": "Netflix TV streaming application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "in.startv.hotstar": {
        "name": "Disney+ Hotstar",
        "category": "Keep unless Opted Out",
        "purpose": "Disney+ Hotstar TV streaming application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "com.amazon.amazonvideo.livingroom": {
        "name": "Amazon Prime Video",
        "category": "Keep unless Opted Out",
        "purpose": "Prime Video TV streaming application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "com.google.android.youtube.tv": {
        "name": "YouTube for Android TV",
        "category": "Keep unless Opted Out",
        "purpose": "Official YouTube TV application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "tv.airtel.xstream.tvapp": {
        "name": "Airtel Xstream",
        "category": "Keep unless Opted Out",
        "purpose": "Airtel Xstream TV streaming application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "com.sonyliv": {
        "name": "Sony LIV",
        "category": "Keep unless Opted Out",
        "purpose": "Sony LIV TV streaming application.",
        "risk": "Low (User-requested app)",
        "action": "Keep (User favorite)"
    },
    "com.suntv.sunnxt": {
        "name": "Sun NXT",
        "category": "Keep unless Opted Out",
        "purpose": "Sun NXT TV streaming application.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.jio.media.stb.ondemand": {
        "name": "JioCinema",
        "category": "Keep unless Opted Out",
        "purpose": "JioCinema TV streaming application.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "org.courville.nova": {
        "name": "Nova Video Player",
        "category": "Keep unless Opted Out",
        "purpose": "Open-source local video player.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.spotify.tv.android": {
        "name": "Spotify TV",
        "category": "Keep unless Opted Out",
        "purpose": "Spotify TV music streaming application.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.cinesoft.neestream.androidtv": {
        "name": "Neestream",
        "category": "Keep unless Opted Out",
        "purpose": "Neestream TV streaming application.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.graymatrix.did": {
        "name": "Zee5",
        "category": "Keep unless Opted Out",
        "purpose": "Zee5 TV streaming application.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.tvwebbrowser.v22": {
        "name": "TV Bro / TV Web Browser",
        "category": "Keep unless Opted Out",
        "purpose": "TV web browser.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "com.mobisystems.fileman": {
        "name": "File Commander",
        "category": "Keep unless Opted Out",
        "purpose": "Local and cloud file manager.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },
    "cm.aptoidetv.pt": {
        "name": "Aptoide TV",
        "category": "Keep unless Opted Out",
        "purpose": "Third-party TV application store.",
        "risk": "Low (Installed user app)",
        "action": "Keep"
    },

    # ------------------ Candidate Bloatware / Analytics ------------------
    # Telemetry, Vendor ads, demo apps, preloaded partner trials
    "com.miui.tv.analytics": {
        "name": "Xiaomi TV Analytics",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi TV background telemetry, device logging, and user tracking.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "tv.alphonso.alphonso_eula": {
        "name": "Alphonso ACR Ad Tracker",
        "category": "Candidate Bloatware",
        "purpose": "Automated Content Recognition (ACR) background SDK that fingerprints viewing habits to serve targeted ads.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.tvhome.atv": {
        "name": "Xiaomi PatchWall Android TV Integration",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi PatchWall launcher bridge and promotional channel rows.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.tvhome.michannel": {
        "name": "Xiaomi Mi Channel",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi promotional live channel aggregator.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.tvhome.mitvplus": {
        "name": "Xiaomi Mi TV+",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi promotional streaming TV service and banner injector.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.xiaomi.android.tvsetup.partnercustomizer": {
        "name": "Xiaomi TV Partner Customizer",
        "category": "Candidate Bloatware",
        "purpose": "Preloads and recommends third-party partner applications.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "android.autoinstalls.config.xioami.mibox3": {
        "name": "Xiaomi Auto-Install Configuration",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi carrier/partner auto-install app configuration stub.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.xm.webcontent": {
        "name": "Xiaomi Web Content Provider",
        "category": "Candidate Bloatware",
        "purpose": "Loads web promotional banner content for Xiaomi TV menus.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.videoplayer": {
        "name": "Xiaomi Mi Video Player",
        "category": "Candidate Bloatware",
        "purpose": "Duplicate Xiaomi video player (User already has Nova Player, Netflix, etc.).",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.download.service": {
        "name": "Xiaomi TV Download Service",
        "category": "Candidate Bloatware",
        "purpose": "Background download daemon used by Xiaomi app store / promotions.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.mitv.milinkservice": {
        "name": "Xiaomi MiLink Service",
        "category": "Candidate Bloatware",
        "purpose": "Xiaomi proprietary DLNA cast bridge (Chromecast built-in handles casting).",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "mitv.service": {
        "name": "Xiaomi MiTV Core Daemon",
        "category": "Candidate Bloatware",
        "purpose": "Background service supporting Xiaomi PatchWall services.",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.google.android.play.games": {
        "name": "Google Play Games",
        "category": "Candidate Bloatware",
        "purpose": "Gaming leaderboards and profile service (if TV is not used for gaming).",
        "risk": "Low (100% Reversible)",
        "action": "Candidate for disable if not gaming"
    },
    "com.google.android.youtube.tvunplugged": {
        "name": "YouTube TV",
        "category": "Candidate Bloatware",
        "purpose": "Live TV subscription service (US-centric).",
        "risk": "Low (Reversible)",
        "action": "Candidate for disable if user does not subscribe to YouTube TV"
    },
    "com.miui.analytics": {
        "name": "Xiaomi MIUI Analytics",
        "category": "Candidate Bloatware",
        "purpose": "Vendor analytics and telemetry tracking.",
        "risk": "Low (Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.xiaomi.mitv.advertise": {
        "name": "Xiaomi MiTV Advertising Service",
        "category": "Candidate Bloatware",
        "purpose": "Vendor boot screen and banner ad server.",
        "risk": "Low (Reversible)",
        "action": "Candidate for disable (`pm disable-user --user 0`)"
    },
    "com.xiaomi.mitv.upgrade": {
        "name": "Xiaomi App Store Upgrade Service",
        "category": "Candidate Bloatware",
        "purpose": "Vendor custom app store update daemon.",
        "risk": "Low (Reversible)",
        "action": "Candidate for disable if using Play Store"
    },
    "com.tcl.partnercustomizer": {
        "name": "TCL Partner Customizer",
        "category": "Candidate Bloatware",
        "purpose": "TCL preinstalled partner promotion injector.",
        "risk": "Low (Reversible)",
        "action": "Recommended to disable (`pm disable-user --user 0`)"
    },
    "com.tcl.tv.screensaver": {
        "name": "TCL Promotional ScreenSaver",
        "category": "Candidate Bloatware",
        "purpose": "Screensaver showing vendor ads and promotional trailers.",
        "risk": "Low (Reversible)",
        "action": "Candidate for disable"
    },
    "com.sony.dtv.demomode": {
        "name": "Sony Retail Demo Mode",
        "category": "Candidate Bloatware",
        "purpose": "In-store retail store loop demo mode.",
        "risk": "Low (Reversible)",
        "action": "Recommended to disable (`pm disable-user --user 0`)"
    },
    "com.hisense.hitv.analytics": {
        "name": "Hisense Analytics Service",
        "category": "Candidate Bloatware",
        "purpose": "Viewing telemetry and usage analytics.",
        "risk": "Low (Reversible)",
        "action": "Recommended to disable (`pm disable-user --user 0`)"
    }
}


def classify_package(pkg: str, is_system: bool, is_disabled: bool, current_home: str) -> Dict[str, str]:
    """Determine category and metadata for a package."""
    # Check exact match
    if pkg in KNOWN_PACKAGE_ROLES:
        info = KNOWN_PACKAGE_ROLES[pkg].copy()
        info["package_id"] = pkg
        info["confidence"] = "High"
        info["reversibility"] = "Fully Reversible" if "Core" not in info["category"] else "N/A"
        if pkg in current_home:
            info["purpose"] += " (ACTIVE DEFAULT LAUNCHER)"
            info["action"] = "DO NOT DISABLE until new launcher is installed & set"
            info["risk"] = "High (Active Home App)"
        return info

    # Heuristic classifications for unknown vendor packages
    lower = pkg.lower()

    # Core system signatures
    if (lower.startswith("android.") or
        lower.startswith("com.android.server") or
        lower.startswith("com.android.internal") or
        lower == "com.android.bluetooth" or
        lower == "com.android.keyguard"):
        return {
            "package_id": pkg,
            "name": pkg.split(".")[-1].capitalize(),
            "category": "Core / System-Critical",
            "purpose": "AOSP low-level system component.",
            "confidence": "Medium-High",
            "risk": "High (Core OS dependency)",
            "action": "Do not touch",
            "reversibility": "N/A"
        }

    # Bloatware / Telemetry signatures
    bloat_keywords = ["analytics", "telemetry", "advertise", "promotion", "retail", "demomode", "shopdemo", "tracker", "pushnotification.partner"]
    if any(k in lower for k in bloat_keywords):
        return {
            "package_id": pkg,
            "name": pkg.split(".")[-1].capitalize(),
            "category": "Candidate Bloatware",
            "purpose": f"Vendor telemetry / advertising / demo component (matches: {[k for k in bloat_keywords if k in lower]}).",
            "confidence": "Medium",
            "risk": "Low (Reversible user disable)",
            "action": "Candidate for disable (`pm disable-user --user 0`)",
            "reversibility": "Fully Reversible"
        }

    # Default to Unknown -> Keep
    return {
        "package_id": pkg,
        "name": pkg.split(".")[-1].capitalize(),
        "category": "Unknown",
        "purpose": "Unclassified package. Role not established in knowledge base.",
        "confidence": "Low",
        "risk": "Unknown (Conservative default: Keep)",
        "action": "Leave unchanged pending review",
        "reversibility": "N/A"
    }


def main():
    parser = argparse.ArgumentParser(description="Generate Human-Readable Android TV Audit Report")
    parser.add_argument("--manifest", type=str, required=True, help="Path to audit_manifest.json")
    parser.add_argument("--output", type=str, help="Output markdown path (default: <manifest_dir>/audit_report.md)")
    args = parser.parse_args()

    if not os.path.isfile(args.manifest):
        print(f"[ERROR] Manifest file not found: {args.manifest}", file=sys.stderr)
        sys.exit(1)

    with open(args.manifest, "r") as f:
        manifest = json.load(f)

    manifest_dir = os.path.dirname(os.path.abspath(args.manifest))
    output_path = args.output or os.path.join(manifest_dir, "audit_report.md")

    dev = manifest.get("device_info", {})
    serial = manifest.get("target_serial", "unknown")
    stats = manifest.get("statistics", {})
    all_pkgs = manifest.get("packages", {}).get("all", [])
    system_pkgs = set(manifest.get("packages", {}).get("system", []))
    disabled_pkgs = set(manifest.get("packages", {}).get("disabled", []))
    current_home = manifest.get("launcher", {}).get("current_home", "")

    classified: List[Dict[str, str]] = []
    for pkg in all_pkgs:
        classified.append(classify_package(
            pkg=pkg,
            is_system=(pkg in system_pkgs),
            is_disabled=(pkg in disabled_pkgs),
            current_home=current_home
        ))

    # Group by category
    categories = {
        "Core / System-Critical": [],
        "Keep unless Opted Out": [],
        "Candidate Bloatware": [],
        "Unknown": []
    }
    for item in classified:
        cat = item["category"]
        if cat in categories:
            categories[cat].append(item)
        else:
            categories["Unknown"].append(item)

    # Generate Markdown report
    lines = []
    lines.append(f"# Android TV Device Audit Report")
    lines.append(f"")
    lines.append(f"**Target Device:** {dev.get('brand')} {dev.get('model')} (`{dev.get('device')}`)")
    lines.append(f"- **Serial:** `{serial}`")
    lines.append(f"- **OS Version:** Android {dev.get('android_version')} (SDK {dev.get('sdk_version')})")
    lines.append(f"- **Build Display ID:** `{dev.get('build_display_id')}`")
    lines.append(f"- **Security Patch:** `{dev.get('security_patch')}`")
    lines.append(f"- **Audit Timestamp:** `{manifest.get('audit_timestamp')}`")
    lines.append(f"- **Current Active Home App:** `{current_home.splitlines()[-1] if current_home else 'None resolved'}`")
    lines.append(f"")
    lines.append(f"## Summary Statistics")
    lines.append(f"| Metric | Count |")
    lines.append(f"| :--- | :--- |")
    lines.append(f"| Total Installed Packages | {stats.get('total_packages', len(all_pkgs))} |")
    lines.append(f"| Enabled Packages | {stats.get('enabled_packages', len(all_pkgs) - len(disabled_pkgs))} |")
    lines.append(f"| Already Disabled Packages | {len(disabled_pkgs)} |")
    lines.append(f"| System Packages | {stats.get('system_packages', len(system_pkgs))} |")
    lines.append(f"| Third-Party Packages | {stats.get('third_party_packages', 0)} |")
    lines.append(f"| Candidate Bloatware Identified | {len(categories['Candidate Bloatware'])} |")
    lines.append(f"| Unclassified (Unknown) Packages | {len(categories['Unknown'])} |")
    lines.append(f"")
    lines.append(f"> [!IMPORTANT]")
    lines.append(f"> **Zero-Risk Principle:** No commands are executed automatically. Bloatware removal strictly defaults to `pm disable-user --user 0`, which is 100% reversible via `pm enable` without root or factory reset.")
    lines.append(f"")

    # Tables by category
    for cat_name, items in categories.items():
        lines.append(f"## Category: {cat_name} ({len(items)} packages)")
        if cat_name == "Core / System-Critical":
            lines.append("These packages provide underlying Linux/Android services, input drivers, display hardware controllers, and package management. **Do NOT touch or disable.**\n")
        elif cat_name == "Keep unless Opted Out":
            lines.append("These packages provide Google Play, Bluetooth remotes, Chromecast built-in, TV Settings, DRM, and HDMI-CEC. Only disable if you specifically do not use that feature.\n")
        elif cat_name == "Candidate Bloatware":
            lines.append("Identified vendor analytics, promotional services, duplicate stores, or demo loops. Safe candidates for reversible user disabling pending user approval.\n")
        else:
            lines.append("Packages not recognized in the knowledge base. Default to **Keep - Leave unchanged** to guarantee device stability.\n")

        lines.append("| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |")
        lines.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
        for it in sorted(items, key=lambda x: x["package_id"]):
            lines.append(f"| `{it['package_id']}` | {it['name']} | {it['purpose']} | {it['confidence']} | **{it['action']}** | {it['reversibility']} | {it['risk']} |")
        lines.append("")

    with open(output_path, "w") as f:
        f.write("\n".join(lines) + "\n")

    print(f"[SUCCESS] Audit report generated: {output_path}")


if __name__ == "__main__":
    main()
