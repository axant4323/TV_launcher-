# Android TV Device Audit Report

**Target Device:** Xiaomi MIBOX4 (`oneday`)
- **Serial:** `192.168.1.8:5555`
- **OS Version:** Android 9 (SDK 28)
- **Build Display ID:** `PI.3933 release-keys`
- **Security Patch:** `2021-07-05`
- **Audit Timestamp:** `20261028_161143`
- **Current Active Home App:** `com.google.android.tvlauncher/.MainActivity`

## Summary Statistics
| Metric | Count |
| :--- | :--- |
| Total Installed Packages | 99 |
| Enabled Packages | 99 |
| Already Disabled Packages | 0 |
| System Packages | 86 |
| Third-Party Packages | 13 |
| Candidate Bloatware Identified | 13 |
| Unclassified (Unknown) Packages | 45 |

> [!IMPORTANT]
> **Zero-Risk Principle:** No commands are executed automatically. Bloatware removal strictly defaults to `pm disable-user --user 0`, which is 100% reversible via `pm enable` without root or factory reset.

## Category: Core / System-Critical (12 packages)
These packages provide underlying Linux/Android services, input drivers, display hardware controllers, and package management. **Do NOT touch or disable.**

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `android` | Android System | Core operating system framework and hardware abstraction. (ACTIVE DEFAULT LAUNCHER) | High | **DO NOT DISABLE until new launcher is installed & set** | N/A | High (Active Home App) |
| `com.android.inputdevices` | Input Devices | Kernel input event routing for physical keys, gamepads, IR/RF remotes. | High | **Do not touch** | N/A | Critical (Remote control loss) |
| `com.android.keychain` | Key Chain | Cryptographic certificate and keystore management. | High | **Do not touch** | N/A | High (Network and security failure) |
| `com.android.providers.settings` | Settings Storage | Database storing global, secure, and system settings. | High | **Do not touch** | N/A | Critical (Bootloop) |
| `com.android.shell` | ADB Shell Service | Required for ADB connection, debugging, and terminal commands. | High | **Do not touch** | N/A | Critical (Loss of ADB debugging access) |
| `com.android.systemui` | System UI | Status bar, notification manager, overlays, navigation bars. | High | **Do not touch** | N/A | Critical (UI crash / Black screen) |
| `com.android.tv.settings` | Android TV Settings | TV-optimized settings UI (Wi-Fi, Bluetooth remotes, Display, Audio, Inputs). | High | **Do not touch** | N/A | Critical (Loss of settings / setup navigation) |
| `com.droidlogic` | Amlogic System Service | Hardware driver and SoC platform framework for Amlogic processor. | High | **Do not touch** | N/A | Critical (Hardware brick risk / system freeze) |
| `com.droidlogic.ble` | Amlogic Bluetooth Remote Driver | Essential low-level driver for the Xiaomi Bluetooth remote control. | High | **Do not touch** | N/A | Critical (Mi Box Bluetooth Remote will disconnect permanently) |
| `com.droidlogic.overlay` | Amlogic Display Overlay | Hardware video resolution, refresh rate, and HDR overlay driver. | High | **Do not touch** | N/A | Critical (Display artifacts / black screen) |
| `com.google.android.packageinstaller` | Google Package Installer | System package installation interface. | High | **Do not touch** | N/A | Critical (Cannot install or update apps) |
| `com.xiaomi.mitv.res` | Xiaomi System Framework Overlay | Hardware resource overlay for Xiaomi system UI elements. | High | **Do not touch** | N/A | High (UI rendering crash) |

## Category: Keep unless Opted Out (29 packages)
These packages provide Google Play, Bluetooth remotes, Chromecast built-in, TV Settings, DRM, and HDMI-CEC. Only disable if you specifically do not use that feature.

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `cm.aptoidetv.pt` | Aptoide TV | Third-party TV application store. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.amazon.amazonvideo.livingroom` | Amazon Prime Video | Prime Video TV streaming application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |
| `com.android.bluetooth` | Bluetooth System Service | Bluetooth audio, BLE TV remotes, headphones, gamepads. | High | **Keep unless TV uses only IR remote and no BT audio** | Fully Reversible | Critical if using Bluetooth remote (Remote will disconnect) |
| `com.android.providers.downloads` | Download Manager | Background file downloads for web browsers, Play Store, and sideloaders. | High | **Keep** | Fully Reversible | High (Download failures across apps) |
| `com.android.providers.media` | Media Storage | Indexes local video, audio, and images on storage/USB. | High | **Keep for local playback support** | Fully Reversible | Medium (Local media players cannot index files) |
| `com.android.vending` | Google Play Store | Official app store and app updater. | High | **Keep unless using sideload-only workflow** | Fully Reversible | Medium (Cannot update or download apps via store) |
| `com.cinesoft.neestream.androidtv` | Neestream | Neestream TV streaming application. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.google.android.apps.mediashell` | Chromecast built-in | Receiver for Google Cast from phones, tablets, and browsers. | High | **Keep if Chromecast is needed** | Fully Reversible | Medium (Disables wireless casting / screen mirroring) |
| `com.google.android.gms` | Google Play Services | Essential for Google login, Play Store, DRM tokens, Cast authentication, push notifications. | High | **Keep unless fully de-Googling** | Fully Reversible | High (App crashes, broken Play Store and Cast) |
| `com.google.android.gsf` | Google Services Framework | Cloud communication framework for Google services. | High | **Keep unless fully de-Googling** | Fully Reversible | High (Google Play breaks) |
| `com.google.android.katniss` | Google Assistant for Android TV | Voice search, microphone button handling on remote, Assistant commands. | High | **Disable only if voice search is not desired** | Fully Reversible | Low/Medium (Remote mic button won't respond) |
| `com.google.android.marvin.talkback` | Android Accessibility Suite (TalkBack) | Screen reader, accessibility services, switch access. | High | **Keep unless user explicitly opts out** | Fully Reversible | Low (No effect unless accessibility is enabled) |
| `com.google.android.tungsten.setupwraith` | Android TV Setup Wraith | Initial device out-of-box setup wizard. | High | **Leave enabled or disable only post-setup** | Fully Reversible | Low after initial setup; needed during factory reset |
| `com.google.android.tv.remote.service` | Android TV Remote Service | Companion remote app support (Google TV phone app, smart remote bridge). | High | **Keep if using phone virtual remote** | Fully Reversible | Low/Medium (Phone remote apps won't connect) |
| `com.google.android.tvlauncher` | Android TV Home Launcher | Stock stock Android TV Home screen and channel rows. (ACTIVE DEFAULT LAUNCHER) | High | **DO NOT DISABLE until new launcher is installed & set** | Fully Reversible | High (Active Home App) |
| `com.google.android.tvrecommendations` | Android TV Core Services / Recommendations | Row recommendations aggregator and provider for stock launcher. | High | **Optional: Candidate for disable once custom launcher is active** | Fully Reversible | Low (Stock launcher rows may stop updating) |
| `com.google.android.youtube.tv` | YouTube for Android TV | Official YouTube TV application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |
| `com.graymatrix.did` | Zee5 | Zee5 TV streaming application. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.jio.media.stb.ondemand` | JioCinema | JioCinema TV streaming application. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.mobisystems.fileman` | File Commander | Local and cloud file manager. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.netflix.ninja` | Netflix for Android TV | Netflix TV streaming application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |
| `com.sonyliv` | Sony LIV | Sony LIV TV streaming application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |
| `com.spotify.tv.android` | Spotify TV | Spotify TV music streaming application. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.suntv.sunnxt` | Sun NXT | Sun NXT TV streaming application. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.tvwebbrowser.v22` | TV Bro / TV Web Browser | TV web browser. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `com.xiaomi.mitv.updateservice` | Xiaomi System OTA Updater | Official Xiaomi firmware and security patch OTA update service. | High | **Keep (User requested to preserve OTA updates)** | Fully Reversible | Medium (Disables firmware and security updates) |
| `in.startv.hotstar` | Disney+ Hotstar | Disney+ Hotstar TV streaming application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |
| `org.courville.nova` | Nova Video Player | Open-source local video player. | High | **Keep** | Fully Reversible | Low (Installed user app) |
| `tv.airtel.xstream.tvapp` | Airtel Xstream | Airtel Xstream TV streaming application. | High | **Keep (User favorite)** | Fully Reversible | Low (User-requested app) |

## Category: Candidate Bloatware (13 packages)
Identified vendor analytics, promotional services, duplicate stores, or demo loops. Safe candidates for reversible user disabling pending user approval.

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `android.autoinstalls.config.xioami.mibox3` | Xiaomi Auto-Install Configuration | Xiaomi carrier/partner auto-install app configuration stub. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.google.android.play.games` | Google Play Games | Gaming leaderboards and profile service (if TV is not used for gaming). | High | **Candidate for disable if not gaming** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.download.service` | Xiaomi TV Download Service | Background download daemon used by Xiaomi app store / promotions. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.milinkservice` | Xiaomi MiLink Service | Xiaomi proprietary DLNA cast bridge (Chromecast built-in handles casting). | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.tvhome.atv` | Xiaomi PatchWall Android TV Integration | Xiaomi PatchWall launcher bridge and promotional channel rows. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.tvhome.michannel` | Xiaomi Mi Channel | Xiaomi promotional live channel aggregator. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.tvhome.mitvplus` | Xiaomi Mi TV+ | Xiaomi promotional streaming TV service and banner injector. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.mitv.videoplayer` | Xiaomi Mi Video Player | Duplicate Xiaomi video player (User already has Nova Player, Netflix, etc.). | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.miui.tv.analytics` | Xiaomi TV Analytics | Xiaomi TV background telemetry, device logging, and user tracking. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.xiaomi.android.tvsetup.partnercustomizer` | Xiaomi TV Partner Customizer | Preloads and recommends third-party partner applications. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `com.xm.webcontent` | Xiaomi Web Content Provider | Loads web promotional banner content for Xiaomi TV menus. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `mitv.service` | Xiaomi MiTV Core Daemon | Background service supporting Xiaomi PatchWall services. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |
| `tv.alphonso.alphonso_eula` | Alphonso ACR Ad Tracker | Automated Content Recognition (ACR) background SDK that fingerprints viewing habits to serve targeted ads. | High | **Candidate for disable (`pm disable-user --user 0`)** | Fully Reversible | Low (100% Reversible) |

## Category: Unknown (45 packages)
Packages not recognized in the knowledge base. Default to **Keep - Leave unchanged** to guarantee device stability.

| Package ID | Visible Name | Purpose | Confidence | Proposed Action | Reversibility | Risk |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `com.android.backupconfirm` | Backupconfirm | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.camera2` | Camera2 | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.captiveportallogin` | Captiveportallogin | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.certinstaller` | Certinstaller | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.companiondevicemanager` | Companiondevicemanager | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.cts.ctsshim` | Ctsshim | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.cts.priv.ctsshim` | Ctsshim | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.defcontainer` | Defcontainer | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.dreams.basic` | Basic | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.externalstorage` | Externalstorage | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.htmlviewer` | Htmlviewer | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.location.fused` | Fused | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.onetimeinitializer` | Onetimeinitializer | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.pacprocessor` | Pacprocessor | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.printspooler` | Printspooler | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.providers.calendar` | Calendar | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.providers.contacts` | Contacts | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.providers.tv` | Tv | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.providers.userdictionary` | Userdictionary | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.proxyhandler` | Proxyhandler | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.se` | Se | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.settings.intelligence` | Intelligence | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.sharedstoragebackup` | Sharedstoragebackup | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.statementservice` | Statementservice | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.vpndialogs` | Vpndialogs | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.android.wallpaperbackup` | Wallpaperbackup | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.backdrop` | Backdrop | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.backuptransport` | Backuptransport | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.ext.services` | Services | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.ext.shared` | Shared | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.feedback` | Feedback | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.inputmethod.latin` | Latin | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.onetimeinitializer` | Onetimeinitializer | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.partnersetup` | Partnersetup | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.speech.pumpkin` | Pumpkin | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.sss.authbridge` | Authbridge | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.syncadapters.calendar` | Calendar | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.syncadapters.contacts` | Contacts | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.tts` | Tts | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.tv` | Tv | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.tv.bugreportsender` | Bugreportsender | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.tv.frameworkpackagestubs` | Frameworkpackagestubs | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.videos` | Videos | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.webview` | Webview | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |
| `com.google.android.youtube.tvmusic` | Tvmusic | Unclassified package. Role not established in knowledge base. | Low | **Leave unchanged pending review** | N/A | Unknown (Conservative default: Keep) |

