package com.adithyatv.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesPersistenceTest {

    @Test
    fun testSerializationAndDeserializationPreservesOrder() {
        val original = listOf(
            "com.google.android.youtube.tv",
            "com.netflix.ninja",
            "com.plexapp.android"
        )

        val serialized = PreferencesRepository.serializeFavorites(original)
        val deserialized = PreferencesRepository.deserializeFavorites(serialized)

        assertEquals(3, deserialized.size)
        assertEquals("com.google.android.youtube.tv", deserialized[0])
        assertEquals("com.netflix.ninja", deserialized[1])
        assertEquals("com.plexapp.android", deserialized[2])
    }

    @Test
    fun testEmptyOrNullSerializationReturnsEmptyList() {
        assertTrue(PreferencesRepository.deserializeFavorites(null).isEmpty())
        assertTrue(PreferencesRepository.deserializeFavorites("").isEmpty())
        assertTrue(PreferencesRepository.deserializeFavorites("[]").isEmpty())
    }

    @Test
    fun testDeserializationDeduplicatesEntries() {
        val rawJson = "[\"com.netflix.ninja\", \"com.netflix.ninja\", \"com.plexapp.android\"]"
        val result = PreferencesRepository.deserializeFavorites(rawJson)

        assertEquals(2, result.size)
        assertEquals("com.netflix.ninja", result[0])
        assertEquals("com.plexapp.android", result[1])
    }
}
