#!/usr/bin/env python3
"""
Device Audit Script (Phase 1: Read-Only Audit)
Safe Android TV Cleanup & Launcher Project

This script performs a strictly READ-ONLY audit of a connected Android TV device.
It enforces target device serial validation, captures all device metadata, dumpsys,
and package lists into a dated backup directory, and generates a structured
audit manifest without changing any device state.
"""

import argparse
import datetime
import json
import os
import re
import subprocess
import sys
from typing import Dict, List, Optional, Tuple


def run_cmd(cmd: List[str]) -> Tuple[int, str, str]:
    """Execute a local command and return returncode, stdout, stderr."""
    try:
        proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, check=False)
        return proc.returncode, proc.stdout.strip(), proc.stderr.strip()
    except Exception as e:
        return 1, "", str(e)


def check_adb_available() -> bool:
    code, stdout, _ = run_cmd(["which", "adb"])
    return code == 0


def get_connected_devices() -> List[Dict[str, str]]:
    """Parse `adb devices -l` output."""
    code, stdout, _ = run_cmd(["adb", "devices", "-l"])
    if code != 0:
        return []
    devices = []
    for line in stdout.splitlines()[1:]:
        line = line.strip()
        if not line:
            continue
        parts = line.split()
        if len(parts) >= 2:
            serial = parts[0]
            state = parts[1]
            extra_info = " ".join(parts[2:])
            devices.append({"serial": serial, "state": state, "info": extra_info})
    return devices


def verify_device_serial(target_serial: Optional[str]) -> str:
    """Ensure the target device is connected and authorized."""
    devices = get_connected_devices()
    if not devices:
        print("[ERROR] No ADB devices detected. Please check USB/wireless connection and ADB debugging.", file=sys.stderr)
        sys.exit(1)

    if not target_serial:
        if len(devices) == 1:
            target_serial = devices[0]["serial"]
            print(f"[INFO] No --serial specified. Single connected device found: {target_serial}")
        else:
            print("[ERROR] Multiple devices connected. You MUST specify --serial <SERIAL>:", file=sys.stderr)
            for d in devices:
                print(f"  - Serial: {d['serial']} ({d['state']}) {d['info']}", file=sys.stderr)
            sys.exit(1)

    matched = [d for d in devices if d["serial"] == target_serial]
    if not matched:
        print(f"[ERROR] Target serial '{target_serial}' not found in connected devices:", file=sys.stderr)
        for d in devices:
            print(f"  - Serial: {d['serial']} ({d['state']})", file=sys.stderr)
        sys.exit(1)

    if matched[0]["state"] != "device":
        print(f"[ERROR] Device '{target_serial}' state is '{matched[0]['state']}'. Authorize USB debugging on TV screen first.", file=sys.stderr)
        sys.exit(1)

    print(f"[VERIFIED] Target device connected and authorized: {target_serial}")
    return target_serial


def adb_shell(serial: str, command: str) -> str:
    """Run a read-only adb shell command."""
    code, stdout, stderr = run_cmd(["adb", "-s", serial, "shell", command])
    if code != 0 and stderr:
        return f"[STDERR: {stderr}]\n{stdout}"
    return stdout


def dump_package_details(serial: str, package_name: str) -> Dict[str, str]:
    """Capture dumpsys details for a specific package."""
    raw = adb_shell(serial, f"dumpsys package {package_name}")
    details = {"raw": raw}
    for line in raw.splitlines():
        line = line.strip()
        if line.startswith("versionName="):
            details["versionName"] = line.split("=", 1)[1]
        elif line.startswith("versionCode="):
            details["versionCode"] = line.split("=", 1)[1]
        elif line.startswith("codePath="):
            details["codePath"] = line.split("=", 1)[1]
        elif "userId=" in line and "userId" not in details:
            details["userId"] = line
    return details


def main():
    parser = argparse.ArgumentParser(description="Android TV Read-Only Device Audit")
    parser.add_argument("--serial", type=str, help="ADB device serial (required if multiple devices connected)")
    parser.add_argument("--outdir", type=str, default="backups", help="Base directory for backup snapshots")
    args = parser.parse_args()

    if not check_adb_available():
        print("[ERROR] 'adb' binary not found. Please install adb or add it to PATH.", file=sys.stderr)
        sys.exit(1)

    serial = verify_device_serial(args.serial)

    timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    clean_serial = re.sub(r"[^\w\-\.]", "_", serial)
    backup_dir = os.path.join(args.outdir, f"audit_{clean_serial}_{timestamp}")
    os.makedirs(backup_dir, exist_ok=True)
    print(f"[INFO] Audit backup folder created: {backup_dir}")

    # 1. Device identity & adb devices -l
    _, adb_dev_l, _ = run_cmd(["adb", "devices", "-l"])
    with open(os.path.join(backup_dir, "adb_devices.txt"), "w") as f:
        f.write(adb_dev_l + "\n")

    # 2. Build properties (getprop)
    print("[1/7] Capturing build properties...")
    getprop_raw = adb_shell(serial, "getprop")
    with open(os.path.join(backup_dir, "getprop.txt"), "w") as f:
        f.write(getprop_raw)

    parsed_props = {}
    for line in getprop_raw.splitlines():
        match = re.match(r"^\[(.*?)\]: \[(.*?)\]$", line.strip())
        if match:
            parsed_props[match.group(1)] = match.group(2)

    device_info = {
        "brand": parsed_props.get("ro.product.brand", "unknown"),
        "manufacturer": parsed_props.get("ro.product.manufacturer", "unknown"),
        "model": parsed_props.get("ro.product.model", "unknown"),
        "device": parsed_props.get("ro.product.device", "unknown"),
        "android_version": parsed_props.get("ro.build.version.release", "unknown"),
        "sdk_version": parsed_props.get("ro.build.version.sdk", "unknown"),
        "build_id": parsed_props.get("ro.build.id", "unknown"),
        "build_display_id": parsed_props.get("ro.build.display.id", "unknown"),
        "fingerprint": parsed_props.get("ro.build.fingerprint", "unknown"),
        "security_patch": parsed_props.get("ro.build.version.security_patch", "unknown"),
    }

    # 3. Installed packages listings
    print("[2/7] Listing all installed packages (all, user 0, enabled, disabled, system, 3rd party)...")
    pkg_all_raw = adb_shell(serial, "pm list packages -u -f")
    pkg_u0_raw = adb_shell(serial, "pm list packages --user 0")
    pkg_enabled_raw = adb_shell(serial, "pm list packages -e")
    pkg_disabled_raw = adb_shell(serial, "pm list packages -d")
    pkg_sys_raw = adb_shell(serial, "pm list packages -s")
    pkg_3rd_raw = adb_shell(serial, "pm list packages -3")

    with open(os.path.join(backup_dir, "pm_packages_all_f.txt"), "w") as f:
        f.write(pkg_all_raw)
    with open(os.path.join(backup_dir, "pm_packages_user0.txt"), "w") as f:
        f.write(pkg_u0_raw)
    with open(os.path.join(backup_dir, "pm_packages_enabled.txt"), "w") as f:
        f.write(pkg_enabled_raw)
    with open(os.path.join(backup_dir, "pm_packages_disabled.txt"), "w") as f:
        f.write(pkg_disabled_raw)
    with open(os.path.join(backup_dir, "pm_packages_system.txt"), "w") as f:
        f.write(pkg_sys_raw)
    with open(os.path.join(backup_dir, "pm_packages_thirdparty.txt"), "w") as f:
        f.write(pkg_3rd_raw)

    def clean_pkg_list(raw_text: str) -> List[str]:
        pkgs = []
        for line in raw_text.splitlines():
            line = line.strip()
            if line.startswith("package:"):
                # Handle `package:/path/to/apk=com.example.app` format
                if "=" in line:
                    pkgs.append(line.split("=")[-1])
                else:
                    pkgs.append(line.replace("package:", ""))
        return sorted(list(set(pkgs)))

    all_packages = clean_pkg_list(pkg_all_raw)
    user0_packages = clean_pkg_list(pkg_u0_raw)
    enabled_packages = clean_pkg_list(pkg_enabled_raw)
    disabled_packages = clean_pkg_list(pkg_disabled_raw)
    system_packages = clean_pkg_list(pkg_sys_raw)
    third_party_packages = clean_pkg_list(pkg_3rd_raw)

    # 4. Launcher and Home role information
    print("[3/7] Resolving current home and leanback launcher handlers...")
    home_role = adb_shell(serial, "cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME")
    leanback_role = adb_shell(serial, "cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER")
    with open(os.path.join(backup_dir, "launcher_home_role.txt"), "w") as f:
        f.write("=== HOME INTENT ===\n" + home_role + "\n\n=== LEANBACK LAUNCHER INTENT ===\n" + leanback_role + "\n")

    # 5. TV Inputs snapshot
    print("[4/7] Snapshotting TV input framework (dumpsys tv_input)...")
    tv_inputs = adb_shell(serial, "dumpsys tv_input")
    with open(os.path.join(backup_dir, "dumpsys_tv_input.txt"), "w") as f:
        f.write(tv_inputs)

    # 6. Settings snapshot
    print("[5/7] Dumping system, secure, and global settings...")
    settings_system = adb_shell(serial, "settings list system")
    settings_secure = adb_shell(serial, "settings list secure")
    settings_global = adb_shell(serial, "settings list global")
    with open(os.path.join(backup_dir, "settings_system.txt"), "w") as f:
        f.write(settings_system)
    with open(os.path.join(backup_dir, "settings_secure.txt"), "w") as f:
        f.write(settings_secure)
    with open(os.path.join(backup_dir, "settings_global.txt"), "w") as f:
        f.write(settings_global)

    # 7. Package labels and launch intents
    print("[6/7] Querying launchable Leanback activities...")
    launchable_raw = adb_shell(serial, "cmd package query-activities -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER")
    with open(os.path.join(backup_dir, "query_leanback_activities.txt"), "w") as f:
        f.write(launchable_raw)

    print("[7/7] Generating audit manifest JSON...")
    manifest = {
        "audit_timestamp": timestamp,
        "target_serial": serial,
        "device_info": device_info,
        "launcher": {
            "current_home": home_role.strip(),
            "current_leanback": leanback_role.strip()
        },
        "statistics": {
            "total_packages": len(all_packages),
            "user0_packages": len(user0_packages),
            "enabled_packages": len(enabled_packages),
            "disabled_packages": len(disabled_packages),
            "system_packages": len(system_packages),
            "third_party_packages": len(third_party_packages),
        },
        "packages": {
            "all": all_packages,
            "user0": user0_packages,
            "enabled": enabled_packages,
            "disabled": disabled_packages,
            "system": system_packages,
            "third_party": third_party_packages,
        }
    }

    manifest_path = os.path.join(backup_dir, "audit_manifest.json")
    with open(manifest_path, "w") as f:
        json.dump(manifest, f, indent=2)

    print(f"\n========================================================")
    print(f" AUDIT COMPLETE (Read-Only) ")
    print(f"========================================================")
    print(f" Device:         {device_info['brand']} {device_info['model']} (Android {device_info['android_version']}, SDK {device_info['sdk_version']})")
    print(f" Serial:         {serial}")
    print(f" Total Packages: {len(all_packages)} (Enabled: {len(enabled_packages)}, Disabled: {len(disabled_packages)}, System: {len(system_packages)})")
    print(f" Current Home:   {home_role.splitlines()[-1] if home_role else 'Unknown'}")
    print(f" Backup Folder:  {backup_dir}")
    print(f" Manifest:       {manifest_path}")
    print(f"========================================================")
    print(f"\nNext step: Run `python3 tools/generate_audit_report.py --manifest {manifest_path}` to classify candidate bloatware.")


if __name__ == "__main__":
    main()
