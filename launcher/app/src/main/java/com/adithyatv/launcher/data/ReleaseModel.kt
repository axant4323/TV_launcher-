package com.adithyatv.launcher.data

import androidx.compose.ui.graphics.Color

/**
 * Representation of a recent movie, series, or video release from a favorite streaming app.
 */
data class MediaReleaseItem(
    val id: String,
    val title: String,
    val appSource: String, // "Netflix", "Prime Video", "Disney+ Hotstar", "YouTube", "Sony LIV", "Airtel Xstream"
    val appPackage: String,
    val releaseYear: String,
    val durationOrEpisode: String,
    val ratingBadge: String, // "4K HDR", "U/A 16+", "IMDb 8.5"
    val genre: String,
    val synopsis: String,
    val backdropUrl: String, // High-res cinematic 16:9 backdrop
    val posterUrl: String,   // 2:3 or 16:9 card thumbnail
    val sourceColor: Long = 0xFFE50914, // Accent color for source badge
    val deepLinkUri: String? = null
)
