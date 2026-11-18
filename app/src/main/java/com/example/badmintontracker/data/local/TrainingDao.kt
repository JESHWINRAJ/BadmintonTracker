
package com.example.badmintontracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Insert
    suspend fun insertTraining(training: TrainingEntity): Long

    @Query("SELECT * FROM training_sessions ORDER BY playedAt DESC")
    fun getAllTrainingSessions(): Flow<List<TrainingEntity>>

    @Query("""
        SELECT COALESCE(SUM(durationMinutes), 0)
        FROM training_sessions
        WHERE playedAt >= :startTime AND playedAt < :endTime
    """)
    fun getWeeklyTrainingMinutes(
        startTime: Long,
        endTime: Long
    ): Flow<Int>

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)
}
