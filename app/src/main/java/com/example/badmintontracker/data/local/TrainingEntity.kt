
package com.example.badmintontracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class TrainingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val trainingType: String,

    val durationMinutes: Int,

    val playedAt: Long = System.currentTimeMillis()
)
