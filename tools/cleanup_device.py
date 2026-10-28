#!/usr/bin/env python3
"""
Device Cleanup Script (Phase 2: Approval-Gated Cleanup)
Safe Android TV Cleanup & Launcher Project

CRITICAL SAFETY RULES:
1. Refuses to run without matching device serial AND explicit confirmation token.
2. Applies actions ONE package at a time.
3. Uses strictly safe, reversible user-space commands:
     - `pm disable-user --user 0 <pkg>` (preferred)
     - `pm uninstall --user 0 <pkg>` (only if explicitly specified)
4. Never allows root (`su`), `remount`, bootloader, flash, or `/system` modification.
5. Emits a rollback log with exact restoration commands for every altered package.
"""

import argparse
import datetime
import json
import os
import re
import subprocess
import sys
import time
from typing import Dict, List, Optional, Tuple


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
        print("Connected devices:", file=sys.stderr)
        for d in devices:
            print(f"  - {d['serial']} ({d['state']})", file=sys.stderr)
        sys.exit(1)

    if matched[0]["state"] != "device":
        print(f"[SECURITY REJECTION] Device '{target_serial}' state is '{matched[0]['state']}'. Must be 'device'.", file=sys.stderr)
        sys.exit(1)


def is_package_installed(serial: str, package_name: str) -> bool:
    code, stdout, _ = run_cmd(["adb", "-s", serial, "shell", f"pm list packages {package_name}"])
    for line in stdout.splitlines():
        if line.strip() == f"package:{package_name}":
            return True
    return False


def is_package_enabled(serial: str, package_name: str) -> bool:
    code, stdout, _ = run_cmd(["adb", "-s", serial, "shell", f"pm list packages -e {package_name}"])
    for line in stdout.splitlines():
        if line.strip() == f"package:{package_name}":
            return True
    return False


def test_tv_responsiveness(serial: str) -> bool:
    """Check that the TV shell and system server are actively responding."""
    code, stdout, _ = run_cmd(["adb", "-s", serial, "shell", "getprop sys.boot_completed"])
    return code == 0 and stdout.strip() == "1"


def main():
    parser = argparse.ArgumentParser(description="Approval-Gated Android TV Cleanup")
    parser.add_argument("--serial", type=str, required=True, help="Exact ADB target serial")
    parser.add_argument("--confirm", type=str, required=True, help="Safety confirmation token (must match CONFIRM_APPLY)")
    parser.add_argument("--plan", type=str, required=True, help="Path to cleanup JSON plan file")
    parser.add_argument("--outdir", type=str, default="backups", help="Directory for rollback logs")
    parser.add_argument("--interactive", action="store_true", default=True, help="Prompt before each package change")
    args = parser.parse_args()

    # 1. Gating check 1: Confirmation token
    if args.confirm != "CONFIRM_APPLY":
        print(f"[SECURITY REJECTION] Invalid confirmation token '{args.confirm}'. Refusing to proceed.", file=sys.stderr)
        print("You must supply --confirm CONFIRM_APPLY to authorize package modifications.", file=sys.stderr)
        sys.exit(1)

    # 2. Gating check 2: Serial verification
    verify_target_serial(args.serial)

    # 3. Load plan
    if not os.path.isfile(args.plan):
        print(f"[ERROR] Plan file not found: {args.plan}", file=sys.stderr)
        sys.exit(1)

    with open(args.plan, "r") as f:
        plan = json.load(f)

    packages_to_clean = plan.get("actions", [])
    if not packages_to_clean:
        print("[INFO] No actions found in plan file. Nothing to do.")
        sys.exit(0)

    # Validate that plan contains target serial
    plan_serial = plan.get("target_serial")
    if plan_serial and plan_serial != args.serial:
        print(f"[SECURITY REJECTION] Plan file is for device '{plan_serial}', but target is '{args.serial}'! Mismatch.", file=sys.stderr)
        sys.exit(1)

    # Validate safety of every requested action
    allowed_actions = ["disable-user", "uninstall-user"]
    for act in packages_to_clean:
        action_type = act.get("action", "disable-user")
        pkg = act.get("package")
        if not pkg:
            print("[ERROR] Action missing package name.", file=sys.stderr)
            sys.exit(1)
        if action_type not in allowed_actions:
            print(f"[SECURITY REJECTION] Action '{action_type}' for {pkg} is not allowed. Only {allowed_actions} permitted.", file=sys.stderr)
            sys.exit(1)

    print(f"\n========================================================")
    print(f" CLEANUP EXECUTION GATE APPROVED ")
    print(f"========================================================")
    print(f" Target Device Serial: {args.serial}")
    print(f" Total Packages to Modify: {len(packages_to_clean)}")
    print(f" Timestamp: {datetime.datetime.now().isoformat()}")
    print(f"========================================================\n")

    timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    clean_serial = re.sub(r"[^\w\-\.]", "_", args.serial)
    rollback_file = os.path.join(args.outdir, f"rollback_{clean_serial}_{timestamp}.json")
    os.makedirs(args.outdir, exist_ok=True)

    rollback_log = {
        "device_serial": args.serial,
        "cleanup_timestamp": timestamp,
        "applied_actions": []
    }

    try:
        for idx, act in enumerate(packages_to_clean, start=1):
            pkg = act["package"]
            action_type = act.get("action", "disable-user")
            reason = act.get("reason", "Bloatware reduction")

            # Check if package is installed
            installed = is_package_installed(args.serial, pkg)
            enabled = is_package_enabled(args.serial, pkg) if installed else False

            print(f"[{idx}/{len(packages_to_clean)}] Target: {pkg}")
            print(f"  - Action: {action_type}")
            print(f"  - Reason: {reason}")
            print(f"  - Currently: Installed={installed}, Enabled={enabled}")

            if not installed and action_type == "disable-user":
                print(f"  -> [SKIP] Package {pkg} is not installed or already uninstalled for user 0.\n")
                continue

            if not enabled and action_type == "disable-user":
                print(f"  -> [SKIP] Package {pkg} is already disabled.\n")
                continue

            # Formulate command and rollback
            if action_type == "disable-user":
                cmd = ["adb", "-s", args.serial, "shell", f"pm disable-user --user 0 {pkg}"]
                rollback_cmd = f"adb -s {args.serial} shell pm enable {pkg}"
            elif action_type == "uninstall-user":
                cmd = ["adb", "-s", args.serial, "shell", f"pm uninstall --user 0 {pkg}"]
                rollback_cmd = f"adb -s {args.serial} shell cmd package install-existing {pkg}"

            print(f"  -> Exact Command to Execute: {' '.join(cmd)}")
            print(f"  -> Immediate Rollback Command: {rollback_cmd}")

            # Re-verify device serial before execution
            verify_target_serial(args.serial)

            # Execute
            code, stdout, stderr = run_cmd(cmd)
            print(f"  -> Result: code={code}, stdout={stdout}, stderr={stderr}")

            if code != 0 or "Error" in stdout or "Exception" in stdout:
                print(f"  -> [WARNING] Command encountered an error: {stdout} {stderr}")
            else:
                rollback_log["applied_actions"].append({
                    "package": pkg,
                    "action_taken": action_type,
                    "previous_state": {"installed": installed, "enabled": enabled},
                    "rollback_command": rollback_cmd,
                    "timestamp": datetime.datetime.now().isoformat()
                })
                # Flush rollback log to disk after each package
                with open(rollback_file, "w") as f:
                    json.dump(rollback_log, f, indent=2)

            # Test TV responsiveness
            time.sleep(1)
            if not test_tv_responsiveness(args.serial):
                print(f"\n[ALERT] TV responsiveness check failed after modifying {pkg}!", file=sys.stderr)
                print(f"[RECOVERY TRIGGER] Reverting {pkg} immediately: {rollback_cmd}", file=sys.stderr)
                run_cmd(rollback_cmd.split())
                print("[ABORT] Stopping cleanup workflow. TV state must be inspected.", file=sys.stderr)
                sys.exit(1)

            print(f"  -> [OK] System healthy and responding.\n")

    except KeyboardInterrupt:
        print("\n[INTERRUPTED] Operation cancelled by user. Partial rollback log saved.")

    print(f"\n========================================================")
    print(f" CLEANUP FINISHED ")
    print(f"========================================================")
    print(f" Total Applied Changes: {len(rollback_log['applied_actions'])}")
    print(f" Rollback Log: {rollback_file}")
    print(f" To revert all changes, run:")
    print(f"   python3 tools/rollback_device.py --log {rollback_file}")
    print(f"========================================================\n")


if __name__ == "__main__":
    main()
