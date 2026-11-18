
package com.example.badmintontracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val opponent: String,
    val playerScore: Int,
    val opponentScore: Int,
    val playedAt: Long = System.currentTimeMillis(),
    val matchType: String = "Singles",
    val notes: String = ""
)
