
package com.example.badmintontracker

import org.json.JSONArray
import org.json.JSONObject
import com.example.badmintontracker.data.local.MatchEntity
import com.example.badmintontracker.data.local.TrainingEntity

object BackupManager {

    fun createBackupJson(
        matches: List<MatchEntity>,
        trainingSessions: List<TrainingEntity>,
        goals: PlayerGoals
    ): String {

        val root = JSONObject()

        root.put("appName", "BadmintonTracker")
        root.put("backupVersion", 1)
        root.put("backupDate", System.currentTimeMillis())

        // MATCH RECORDS
        val matchesArray = JSONArray()

        matches.forEach { match ->
            val item = JSONObject().apply {
                put("id", match.id)
                put("opponent", match.opponent)
                put("playerScore", match.playerScore)
                put("opponentScore", match.opponentScore)
                put("playedAt", match.playedAt)
                put("matchType", match.matchType)
                put("notes", match.notes)
            }

            matchesArray.put(item)
        }

        root.put("matches", matchesArray)

        // TRAINING RECORDS
        val trainingArray = JSONArray()

        trainingSessions.forEach { session ->
            val item = JSONObject().apply {
                put("id", session.id)
                put("trainingType", session.trainingType)
                put("durationMinutes", session.durationMinutes)
                put("playedAt", session.playedAt)
            }

            trainingArray.put(item)
        }

        root.put("trainingSessions", trainingArray)

        // WEEKLY GOALS
        val goalsObject = JSONObject().apply {
            put("weeklyMatchTarget", goals.weeklyMatchTarget)
            put(
                "weeklyTrainingMinutesTarget",
                goals.weeklyTrainingMinutesTarget
            )
            put("weeklyWinTarget", goals.weeklyWinTarget)
        }

        root.put("goals", goalsObject)

        return root.toString(4)
    }
}
