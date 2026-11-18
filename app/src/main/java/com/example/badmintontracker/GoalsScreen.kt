
package com.example.badmintontracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badmintontracker.data.local.MatchEntity
import com.example.badmintontracker.data.local.TrainingEntity
import java.util.Calendar

private val GoalBlue = Color(0xFF48B2D2)
private val GoalBlack = Color(0xFF1B191D)
private val GoalGrey = Color(0xFFEEEEEE)

@Composable
fun GoalsScreen(
    matches: List<MatchEntity>,
    trainingSessions: List<TrainingEntity>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var goals by remember {
        mutableStateOf(GoalStorage.loadGoals(context))
    }

    var showEditDialog by remember { mutableStateOf(false) }

    val calendar = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val weekStart = calendar.timeInMillis

    val weekEnd = remember(weekStart) {
        Calendar.getInstance().apply {
            timeInMillis = weekStart
            add(Calendar.DAY_OF_YEAR, 7)
        }.timeInMillis
    }

    val weeklyMatches = matches.filter {
        it.playedAt >= weekStart && it.playedAt < weekEnd
    }

    val weeklyTraining = trainingSessions.filter {
        it.playedAt >= weekStart && it.playedAt < weekEnd
    }

    val weeklyWins = weeklyMatches.count {
        it.playerScore > it.opponentScore
    }

    val weeklyTrainingMinutes = weeklyTraining.sumOf {
        it.durationMinutes
    }

    val weeklyMatchCount = weeklyMatches.size

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Back", color = GoalBlue)
                }

                Text(
                    "Goals & Progress",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoalBlack
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                "YOUR WEEKLY GOALS",
                color = GoalBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Small improvements lead to better performance.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            GoalProgressCard(
                title = "🏸 Match Target",
                current = weeklyMatchCount,
                target = goals.weeklyMatchTarget,
                unit = "matches"
            )

            GoalProgressCard(
                title = "🏆 Winning Target",
                current = weeklyWins,
                target = goals.weeklyWinTarget,
                unit = "wins"
            )

            GoalProgressCard(
                title = "💪 Training Target",
                current = weeklyTrainingMinutes,
                target = goals.weeklyTrainingMinutesTarget,
                unit = "minutes",
                displayCurrent = formatMinutes(weeklyTrainingMinutes),
                displayTarget = formatMinutes(
                    goals.weeklyTrainingMinutesTarget
                )
            )

            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoalBlue
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Edit Weekly Goals")
            }

            HorizontalDivider(color = GoalGrey)

            Text(
                "WEEKLY SUMMARY",
                color = GoalBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            SummaryCard(
                title = "Matches Played",
                value = weeklyMatchCount.toString()
            )

            SummaryCard(
                title = "Matches Won",
                value = weeklyWins.toString()
            )

            SummaryCard(
                title = "Training Sessions",
                value = weeklyTraining.size.toString()
            )

            SummaryCard(
                title = "Training Time",
                value = formatMinutes(weeklyTrainingMinutes)
            )

            val overallCompleted =
                (if (weeklyMatchCount >= goals.weeklyMatchTarget) 1 else 0) +
                        (if (weeklyWins >= goals.weeklyWinTarget) 1 else 0) +
                        (if (weeklyTrainingMinutes >= goals.weeklyTrainingMinutesTarget) 1 else 0)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = GoalGrey
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Goal Achievement",
                        fontWeight = FontWeight.Bold,
                        color = GoalBlack
                    )

                    Text(
                        "$overallCompleted out of 3 weekly goals completed",
                        color = GoalBlue,
                        fontWeight = FontWeight.Bold
                    )

                    LinearProgressIndicator(
                        progress = { overallCompleted / 3f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp),
                        color = GoalBlue,
                        trackColor = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showEditDialog) {
        EditGoalsDialog(
            currentGoals = goals,
            onDismiss = { showEditDialog = false },
            onSave = { updatedGoals ->
                GoalStorage.saveGoals(context, updatedGoals)
                goals = updatedGoals
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun GoalProgressCard(
    title: String,
    current: Int,
    target: Int,
    unit: String,
    displayCurrent: String = current.toString(),
    displayTarget: String = target.toString()
) {
    val progress = if (target > 0) {
        (current.toFloat() / target).coerceIn(0f, 1f)
    } else {
        0f
    }

    val completed = current >= target

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoalGrey
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = GoalBlack
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$displayCurrent / $displayTarget $unit",
                    fontWeight = FontWeight.SemiBold,
                    color = GoalBlack
                )

                Text(
                    if (completed) "Completed ✓"
                    else "${(progress * 100).toInt()}%",
                    color = if (completed) {
                        Color(0xFF168653)
                    } else {
                        GoalBlue
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = GoalBlue,
                trackColor = Color.LightGray
            )

            Text(
                if (completed) {
                    "Great work! You reached your target."
                } else {
                    "${(target - current).coerceAtLeast(0)} $unit remaining"
                },
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GoalGrey
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                color = GoalBlack,
                fontWeight = FontWeight.Medium
            )

            Text(
                value,
                color = GoalBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun EditGoalsDialog(
    currentGoals: PlayerGoals,
    onDismiss: () -> Unit,
    onSave: (PlayerGoals) -> Unit
) {
    var matchTarget by remember {
        mutableStateOf(currentGoals.weeklyMatchTarget.toString())
    }

    var winTarget by remember {
        mutableStateOf(currentGoals.weeklyWinTarget.toString())
    }

    var trainingTarget by remember {
        mutableStateOf(
            currentGoals.weeklyTrainingMinutesTarget.toString()
        )
    }

    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Weekly Goals")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = matchTarget,
                    onValueChange = {
                        matchTarget = it.filter(Char::isDigit)
                    },
                    label = { Text("Weekly Match Target") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = winTarget,
                    onValueChange = {
                        winTarget = it.filter(Char::isDigit)
                    },
                    label = { Text("Weekly Win Target") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = trainingTarget,
                    onValueChange = {
                        trainingTarget = it.filter(Char::isDigit)
                    },
                    label = { Text("Weekly Training Minutes") },
                    singleLine = true
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val matches = matchTarget.toIntOrNull()
                    val wins = winTarget.toIntOrNull()
                    val training = trainingTarget.toIntOrNull()

                    when {
                        matches == null || wins == null || training == null ->
                            errorMessage = "Enter all three goals."

                        matches <= 0 || wins <= 0 || training <= 0 ->
                            errorMessage = "Goals must be greater than zero."

                        wins > matches ->
                            errorMessage = "Win target cannot exceed match target."

                        else -> onSave(
                            PlayerGoals(
                                weeklyMatchTarget = matches,
                                weeklyTrainingMinutesTarget = training,
                                weeklyWinTarget = wins
                            )
                        )
                    }
                }
            ) {
                Text("Save", color = GoalBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return if (hours > 0) {
        "${hours}h ${remainingMinutes}m"
    } else {
        "${remainingMinutes}m"
    }
}
