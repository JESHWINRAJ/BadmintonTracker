
package com.example.badmintontracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Insert
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("SELECT * FROM matches ORDER BY playedAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT COUNT(*) FROM matches")
    fun getTotalMatches(): Flow<Int>

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}
