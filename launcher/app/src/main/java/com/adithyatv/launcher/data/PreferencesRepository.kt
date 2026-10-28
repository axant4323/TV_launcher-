package com.adithyatv.launcher.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

/**
 * Handles persistent preferences:
 * - Favorites list (ordered package IDs)
 * - UI Scale factor (0.85x - 1.25x for 10-foot legibility)
 * - Custom input labels
 */
class PreferencesRepository(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        const val PREFS_NAME = "adithya_tv_launcher_prefs"
        const val KEY_FAVORITES_JSON = "key_favorites_ordered_json"
        const val KEY_UI_SCALE = "key_ui_scale_factor"
        const val DEFAULT_UI_SCALE = 1.0f

        // Pure helper methods for unit testing serialization
        fun serializeFavorites(favorites: List<String>): String {
            val jsonArray = JSONArray()
            favorites.forEach { jsonArray.put(it) }
            return jsonArray.toString()
        }

        fun deserializeFavorites(jsonString: String?): List<String> {
            if (jsonString.isNullOrBlank()) return emptyList()
            val list = mutableListOf<String>()
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.optString(i)
                    if (!item.isNullOrBlank() && !list.contains(item)) {
                        list.add(item)
                    }
                }
            } catch (_: Exception) {
                return emptyList()
            }
            return list
        }
    }

    fun getFavorites(): List<String> {
        val raw = prefs.getString(KEY_FAVORITES_JSON, null)
        return deserializeFavorites(raw)
    }

    fun saveFavorites(favorites: List<String>) {
        val serialized = serializeFavorites(favorites.distinct())
        prefs.edit().putString(KEY_FAVORITES_JSON, serialized).apply()
    }

    fun toggleFavorite(packageName: String): Boolean {
        val current = getFavorites().toMutableList()
        val isNowFavorite: Boolean
        if (current.contains(packageName)) {
            current.remove(packageName)
            isNowFavorite = false
        } else {
            current.add(packageName)
            isNowFavorite = true
        }
        saveFavorites(current)
        return isNowFavorite
    }

    fun moveFavorite(packageName: String, moveUp: Boolean): List<String> {
        val current = getFavorites().toMutableList()
        val index = current.indexOf(packageName)
        if (index == -1) return current

        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex in 0 until current.size) {
            val item = current.removeAt(index)
            current.add(targetIndex, item)
            saveFavorites(current)
        }
        return current
    }

    fun getUiScale(): Float {
        return prefs.getFloat(KEY_UI_SCALE, DEFAULT_UI_SCALE)
    }

    fun setUiScale(scale: Float) {
        val clamped = scale.coerceIn(0.85f, 1.25f)
        prefs.edit().putFloat(KEY_UI_SCALE, clamped).apply()
    }
}
