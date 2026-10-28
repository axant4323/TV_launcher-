#!/usr/bin/env python3
"""
Device Rollback Script (Phase 2: Reversible Cleanup Rollback)
Safe Android TV Cleanup & Launcher Project

Restores disabled or uninstalled-for-user packages back to their original state:
  - For disabled packages: `pm enable <PACKAGE>`
  - For uninstalled packages: `cmd package install-existing <PACKAGE>`
"""

import argparse
import json
import os
import subprocess
import sys
from typing import Dict, List, Tuple


def run_cmd(cmd: List[str]) -> Tuple[int, str, str]:
    try:
        proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, check=False)
        return proc.returncode, proc.stdout.strip(), proc.stderr.strip()
    except Exception as e:
        return 1, "", str(e)


def get_connected_devices() -> List[Dict[str, str]]:
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
            devices.append({"serial": parts[0], "state": parts[1], "info": " ".join(parts[2:])})
    return devices


def verify_target_serial(target_serial: str):
    devices = get_connected_devices()
    matched = [d for d in devices if d["serial"] == target_serial]
    if not matched:
        print(f"[SECURITY REJECTION] Target serial '{target_serial}' not found in active ADB devices!", file=sys.stderr)
        sys.exit(1)
    if matched[0]["state"] != "device":
        print(f"[SECURITY REJECTION] Device '{target_serial}' state is '{matched[0]['state']}'. Must be 'device'.", file=sys.stderr)
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(description="Android TV Cleanup Rollback")
    parser.add_argument("--log", type=str, help="Path to rollback JSON log file")
    parser.add_argument("--serial", type=str, help="Device serial (if rolling back single package or overriding log)")
    parser.add_argument("--package", type=str, help="Single package ID to restore directly")
    parser.add_argument("--mode", choices=["enable", "install-existing"], default="enable", help="Restore mode for single package")
    args = parser.parse_args()

    if not args.log and not (args.package and args.serial):
        print("[ERROR] You must provide either --log <ROLLBACK_LOG_PATH> or (--package <PKG> and --serial <SERIAL>)", file=sys.stderr)
        sys.exit(1)

    if args.log:
        if not os.path.isfile(args.log):
            print(f"[ERROR] Rollback log not found: {args.log}", file=sys.stderr)
            sys.exit(1)
        with open(args.log, "r") as f:
            log_data = json.load(f)

        serial = args.serial or log_data.get("device_serial")
        if not serial:
            print("[ERROR] No target serial found in log or command line.", file=sys.stderr)
            sys.exit(1)

        verify_target_serial(serial)
        actions = log_data.get("applied_actions", [])
        if not actions:
            print("[INFO] No applied actions in rollback log.")
            sys.exit(0)

        print(f"\n========================================================")
        print(f" EXECUTING ROLLBACK ")
        print(f"========================================================")
        print(f" Target Device: {serial}")
        print(f" Changes to Revert: {len(actions)}")
        print(f"========================================================\n")

        # Roll back in reverse order (LIFO)
        for act in reversed(actions):
            pkg = act["package"]
            action_taken = act.get("action_taken", "disable-user")
            print(f"-> Reverting {pkg} (was {action_taken})...")

            if action_taken == "disable-user":
                cmd = ["adb", "-s", serial, "shell", f"pm enable {pkg}"]
            else:
                cmd = ["adb", "-s", serial, "shell", f"cmd package install-existing {pkg}"]

            print(f"   Executing: {' '.join(cmd)}")
            code, stdout, stderr = run_cmd(cmd)
            print(f"   Result: code={code}, stdout={stdout}, stderr={stderr}")

        print(f"\n[SUCCESS] All {len(actions)} actions reverted successfully.")

    elif args.package:
        verify_target_serial(args.serial)
        pkg = args.package
        if args.mode == "enable":
            cmd = ["adb", "-s", args.serial, "shell", f"pm enable {pkg}"]
        else:
            cmd = ["adb", "-s", args.serial, "shell", f"cmd package install-existing {pkg}"]
        print(f"-> Restoring {pkg} on {args.serial} with {' '.join(cmd)}...")
        code, stdout, stderr = run_cmd(cmd)
        print(f"   Result: code={code}, stdout={stdout}, stderr={stderr}")


if __name__ == "__main__":
    main()
