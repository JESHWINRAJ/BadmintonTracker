
package com.example.badmintontracker.data.repository

import com.example.badmintontracker.data.local.MatchDao
import com.example.badmintontracker.data.local.MatchEntity
import com.example.badmintontracker.data.local.TrainingDao
import com.example.badmintontracker.data.local.TrainingEntity
import kotlinx.coroutines.flow.Flow

class MatchRepository(
    private val matchDao: MatchDao,
    private val trainingDao: TrainingDao
) {

    val allMatches: Flow<List<MatchEntity>> =
        matchDao.getAllMatches()

    val allTrainingSessions: Flow<List<TrainingEntity>> =
        trainingDao.getAllTrainingSessions()

    suspend fun insertMatch(match: MatchEntity): Long {
        return matchDao.insertMatch(match)
    }

    suspend fun updateMatch(match: MatchEntity) {
        matchDao.updateMatch(match)
    }

    suspend fun deleteMatch(match: MatchEntity) {
        matchDao.deleteMatch(match)
    }

    suspend fun deleteAllMatches() {
        matchDao.deleteAllMatches()
    }

    suspend fun addTraining(training: TrainingEntity): Long {
        return trainingDao.insertTraining(training)
    }

    suspend fun deleteTraining(training: TrainingEntity) {
        trainingDao.deleteTraining(training)
    }
}
