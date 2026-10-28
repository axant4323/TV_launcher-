package com.adithyatv.launcher.data

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log

class AppRepository(
    private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {
    private val tag = "AppRepository"

    /**
     * Queries all launchable TV and standard applications.
     */
    fun getInstalledApps(): List<AppItem> {
        val pm = context.packageManager
        val selfPackage = context.packageName

        // 1. Primary Leanback launcher activities
        val leanbackIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val leanbackActivities = pm.queryIntentActivities(leanbackIntent, PackageManager.MATCH_ALL)

        // 2. Fallback standard launcher activities
        val standardIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val standardActivities = pm.queryIntentActivities(standardIntent, PackageManager.MATCH_ALL)

        val combined = mutableMapOf<String, ResolveInfo>()

        // Add leanback activities first (preferred)
        for (info in leanbackActivities) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == selfPackage) continue
            combined[pkg] = info
        }

        // Add standard activities if not already present
        for (info in standardActivities) {
            val pkg = info.activityInfo?.packageName ?: continue
            if (pkg == selfPackage) continue
            if (!combined.containsKey(pkg)) {
                combined[pkg] = info
            }
        }

        val favorites = preferencesRepository.getFavorites().toSet()

        val appList = combined.values.map { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(pm).toString()
            val activityName = resolveInfo.activityInfo.name
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            AppItem(
                id = pkg,
                label = label.ifBlank { pkg },
                packageName = pkg,
                activityName = activityName,
                isFavorite = favorites.contains(pkg),
                isSystem = isSystem
            )
        }.sortedBy { it.label.lowercase() }

        return appList
    }

    /**
     * Returns ordered favorites list.
     */
    fun getFavoriteApps(allApps: List<AppItem>): List<AppItem> {
        val appMap = allApps.associateBy { it.packageName }
        val requestedPriority = listOf(
            "com.netflix.ninja",                     // Netflix
            "com.amazon.amazonvideo.livingroom",     // Prime Video
            "in.startv.hotstar",                     // Disney+ Hotstar
            "com.jio.media.stb.ondemand",             // JioCinema
            "tv.airtel.xstream.tvapp",               // Airtel Xstream
            "com.google.android.youtube.tv",         // YouTube
            "com.sonyliv"                            // Sony LIV
        )

        val savedFavs = preferencesRepository.getFavorites()
        val currentSet = savedFavs.toMutableSet()
        val mergedList = savedFavs.toMutableList()

        // Ensure all requested priority apps are in the list if installed
        for (pkg in requestedPriority) {
            if (appMap.containsKey(pkg) && !currentSet.contains(pkg)) {
                mergedList.add(pkg)
                currentSet.add(pkg)
            }
        }

        // If list was empty or newly initialized, populate in priority order
        if (savedFavs.isEmpty()) {
            mergedList.clear()
            for (pkg in requestedPriority) {
                if (appMap.containsKey(pkg)) {
                    mergedList.add(pkg)
                }
            }
        }

        if (mergedList != savedFavs) {
            preferencesRepository.saveFavorites(mergedList)
        }

        return mergedList.mapNotNull { pkg ->
            appMap[pkg]?.copy(isFavorite = true)
        }
    }

    /**
     * Pure search filter logic (testable).
     */
    companion object {
        fun filterApps(apps: List<AppItem>, query: String): List<AppItem> {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) return apps
            val lower = trimmed.lowercase()
            return apps.filter {
                it.label.lowercase().contains(lower) ||
                it.packageName.lowercase().contains(lower)
            }
        }
    }

    /**
     * Safely launches an application without crashing.
     */
    fun launchApp(item: AppItem): Boolean {
        val pm = context.packageManager
        try {
            var intent: Intent? = null
            if (!item.activityName.isNullOrBlank()) {
                intent = Intent(Intent.ACTION_MAIN).apply {
                    component = ComponentName(item.packageName, item.activityName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
            }

            if (intent == null || pm.resolveActivity(intent, 0) == null) {
                // Try leanback launch intent
                intent = pm.getLeanbackLaunchIntentForPackage(item.packageName)
            }

            if (intent == null) {
                // Fallback to standard launch intent
                intent = pm.getLaunchIntentForPackage(item.packageName)
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } else {
                Log.w(tag, "No valid launch intent found for ${item.packageName}")
                return false
            }
        } catch (e: ActivityNotFoundException) {
            Log.e(tag, "Activity not found: ${item.packageName}", e)
            return false
        } catch (e: Exception) {
            Log.e(tag, "Failed to launch ${item.packageName}", e)
            return false
        }
    }
}
