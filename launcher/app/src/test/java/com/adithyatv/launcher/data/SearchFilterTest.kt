package com.adithyatv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchFilterTest {

    private val sampleApps = listOf(
        AppItem("com.google.android.youtube.tv", "YouTube", "com.google.android.youtube.tv", null),
        AppItem("com.netflix.ninja", "Netflix", "com.netflix.ninja", null),
        AppItem("com.plexapp.android", "Plex", "com.plexapp.android", null),
        AppItem("org.xbmc.kodi", "Kodi", "org.xbmc.kodi", null),
        AppItem("com.amazon.amazonvideo.livingroom", "Prime Video", "com.amazon.amazonvideo.livingroom", null)
    )

    @Test
    fun testEmptyQueryReturnsAllApps() {
        val result = AppRepository.filterApps(sampleApps, "")
        assertEquals(5, result.size)

        val resultWhitespace = AppRepository.filterApps(sampleApps, "   ")
        assertEquals(5, resultWhitespace.size)
    }

    @Test
    fun testFilterMatchesCaseInsensitiveLabel() {
        val result = AppRepository.filterApps(sampleApps, "you")
        assertEquals(1, result.size)
        assertEquals("YouTube", result[0].label)

        val resultUpper = AppRepository.filterApps(sampleApps, "NETFLIX")
        assertEquals(1, resultUpper.size)
        assertEquals("Netflix", resultUpper[0].label)
    }

    @Test
    fun testFilterMatchesPackageName() {
        val result = AppRepository.filterApps(sampleApps, "amazonvideo")
        assertEquals(1, result.size)
        assertEquals("Prime Video", result[0].label)
    }

    @Test
    fun testNonMatchingQueryReturnsEmptyList() {
        val result = AppRepository.filterApps(sampleApps, "Spotify")
        assertEquals(0, result.size)
    }
}
