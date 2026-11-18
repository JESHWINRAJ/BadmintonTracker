
package com.example.badmintontracker

import android.content.Context

data class PlayerProfile(
    val playerName: String = "",
    val playingStyle: String = "All-round",
    val playingCategory: String = "Both",
    val preferredRacket: String = "",
    val playingHand: String = "Right"
)

object ProfileStorage {

    private const val PREFS_NAME = "badminton_tracker_profile"

    fun saveProfile(context: Context, profile: PlayerProfile) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString("playerName", profile.playerName)
            .putString("playingStyle", profile.playingStyle)
            .putString("playingCategory", profile.playingCategory)
            .putString("preferredRacket", profile.preferredRacket)
            .putString("playingHand", profile.playingHand)
            .apply()
    }

    fun loadProfile(context: Context): PlayerProfile {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return PlayerProfile(
            playerName = prefs.getString("playerName", "") ?: "",
            playingStyle = prefs.getString("playingStyle", "All-round") ?: "All-round",
            playingCategory = prefs.getString("playingCategory", "Both") ?: "Both",
            preferredRacket = prefs.getString("preferredRacket", "") ?: "",
            playingHand = prefs.getString("playingHand", "Right") ?: "Right"
        )
    }

    fun saveSetting(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun loadSetting(context: Context, key: String, defaultValue: Boolean): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(key, defaultValue)
    }
}
