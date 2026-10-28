package com.adithyatv.launcher.data

/**
 * Representation of an installed TV application.
 */
data class AppItem(
    val id: String,
    val label: String,
    val packageName: String,
    val activityName: String?,
    val isFavorite: Boolean = false,
    val isSystem: Boolean = false
) {
    val displayKey: String
        get() = "$packageName/${activityName ?: ""}"
}
