
package com.example.badmintontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.badmintontracker.data.local.MatchEntity
import com.example.badmintontracker.data.local.TrainingEntity
import com.example.badmintontracker.data.repository.MatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MatchViewModel(
    private val repository: MatchRepository
) : ViewModel() {

    val matches = repository.allMatches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val trainingSessions = repository.allTrainingSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addMatch(
        opponent: String,
        playerScore: Int,
        opponentScore: Int,
        matchType: String = "Singles",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertMatch(
                MatchEntity(
                    opponent = opponent,
                    playerScore = playerScore,
                    opponentScore = opponentScore,
                    matchType = matchType,
                    notes = notes
                )
            )
        }
    }

    fun updateMatch(match: MatchEntity) {
        viewModelScope.launch {
            repository.updateMatch(match)
        }
    }

    fun deleteMatch(match: MatchEntity) {
        viewModelScope.launch {
            repository.deleteMatch(match)
        }
    }

    fun deleteAllMatches() {
        viewModelScope.launch {
            repository.deleteAllMatches()
        }
    }

    fun addTraining(trainingType: String, durationMinutes: Int) {
        viewModelScope.launch {
            repository.addTraining(
                TrainingEntity(
                    trainingType = trainingType,
                    durationMinutes = durationMinutes
                )
            )
        }
    }

    fun deleteTraining(training: TrainingEntity) {
        viewModelScope.launch {
            repository.deleteTraining(training)
        }
    }
}
