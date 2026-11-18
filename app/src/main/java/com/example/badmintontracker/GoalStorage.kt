
package com.example.badmintontracker

import android.content.Context

data class PlayerGoals(
    val weeklyMatchTarget: Int = 5,
    val weeklyTrainingMinutesTarget: Int = 300,
    val weeklyWinTarget: Int = 3
)

object GoalStorage {

    private const val PREFS_NAME = "badminton_tracker_goals"

    fun saveGoals(context: Context, goals: PlayerGoals) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt("weeklyMatchTarget", goals.weeklyMatchTarget)
            .putInt(
                "weeklyTrainingMinutesTarget",
                goals.weeklyTrainingMinutesTarget
            )
            .putInt("weeklyWinTarget", goals.weeklyWinTarget)
            .apply()
    }

    fun loadGoals(context: Context): PlayerGoals {
        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return PlayerGoals(
            weeklyMatchTarget = prefs.getInt("weeklyMatchTarget", 5),
            weeklyTrainingMinutesTarget = prefs.getInt(
                "weeklyTrainingMinutesTarget",
                300
            ),
            weeklyWinTarget = prefs.getInt("weeklyWinTarget", 3)
        )
    }
}
