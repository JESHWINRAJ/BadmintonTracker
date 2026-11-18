
package com.example.badmintontracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badmintontracker.data.local.MatchEntity
import com.example.badmintontracker.data.local.TrainingEntity
import com.example.badmintontracker.ui.viewmodel.MatchViewModel

private val AppBlue = Color(0xFF48B2D2)
private val AppBlack = Color(0xFF1B191D)
private val AppGrey = Color(0xFFEEEEEE)

data class MatchRecord(
    val opponent: String,
    val playerScore: Int,
    val opponentScore: Int,
    val matchType: String,
    val notes: String
)

@Composable
fun DashboardScreen(viewModel: MatchViewModel) {
    val context = LocalContext.current

    val matches by viewModel.matches.collectAsState()
    val trainingSessions by viewModel.trainingSessions.collectAsState()

    var showHistory by remember { mutableStateOf(false) }
    var showTraining by remember { mutableStateOf(false) }
    var showStatistics by remember { mutableStateOf(false) }
    var showTrainingAnalytics by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    var showGoals by remember { mutableStateOf(false) }

    var playerName by remember {
        mutableStateOf(ProfileStorage.loadProfile(context).playerName)
    }

    when {
        showGoals -> GoalsScreen(
            matches = matches,
            trainingSessions = trainingSessions,
            onBack = { showGoals = false }
        )

        showProfile -> PlayerProfileScreen(
            onBack = {
                playerName = ProfileStorage.loadProfile(context).playerName
                showProfile = false
            }
        )

        showTrainingAnalytics -> TrainingAnalyticsScreen(
            sessions = trainingSessions,
            onBack = { showTrainingAnalytics = false }
        )

        showStatistics -> StatisticsScreen(
            matches = matches,
            onBack = { showStatistics = false }
        )

        showTraining -> TrainingScreen(
            sessions = trainingSessions,
            onBack = { showTraining = false },
            onAddTraining = { type, duration ->
                viewModel.addTraining(type, duration)
            },
            onDeleteTraining = { viewModel.deleteTraining(it) }
        )

        showHistory -> MatchHistoryScreen(
            matches = matches,
            onBack = { showHistory = false },
            onDelete = { viewModel.deleteMatch(it) },
            onUpdate = { viewModel.updateMatch(it) }
        )

        else -> DashboardContent(
            matches = matches,
            trainingSessions = trainingSessions,
            playerName = playerName,
            onAddMatch = { opponent, myScore, theirScore, type, notes ->
                viewModel.addMatch(opponent, myScore, theirScore, type, notes)
            },
            onDeleteMatch = { viewModel.deleteMatch(it) },
            onViewAll = { showHistory = true },
            onViewTraining = { showTraining = true },
            onViewStatistics = { showStatistics = true },
            onViewTrainingAnalytics = { showTrainingAnalytics = true },
            onViewProfile = { showProfile = true },
            onViewGoals = { showGoals = true }
        )
    }
}

@Composable
private fun DashboardContent(
    matches: List<MatchEntity>,
    trainingSessions: List<TrainingEntity>,
    playerName: String,
    onAddMatch: (String, Int, Int, String, String) -> Unit,
    onDeleteMatch: (MatchEntity) -> Unit,
    onViewAll: () -> Unit,
    onViewTraining: () -> Unit,
    onViewStatistics: () -> Unit,
    onViewTrainingAnalytics: () -> Unit,
    onViewProfile: () -> Unit,
    onViewGoals: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var matchToDelete by remember { mutableStateOf<MatchEntity?>(null) }

    val totalMatches = matches.size
    val wins = matches.count { it.playerScore > it.opponentScore }
    val losses = matches.count { it.playerScore < it.opponentScore }
    val winRate = if (totalMatches > 0) wins * 100 / totalMatches else 0

    val calendar = remember { java.util.Calendar.getInstance() }
    calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    val weekStart = calendar.timeInMillis

    val weekEnd = remember(weekStart) {
        java.util.Calendar.getInstance().apply {
            timeInMillis = weekStart
            add(java.util.Calendar.DAY_OF_YEAR, 7)
        }.timeInMillis
    }

    val weeklyTraining = trainingSessions.filter {
        it.playedAt >= weekStart && it.playedAt < weekEnd
    }
    val weeklyMinutes = weeklyTraining.sumOf { it.durationMinutes }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "BADMINTON TRACKER",
                    color = AppBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = onViewProfile,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppBlue
                    )
                ) {
                    Text("Profile ⚙")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Hello, ${playerName.ifBlank { "Player" }}! 👋",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = AppBlack
            )

            Text(
                "Ready to improve your game?",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Your Performance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppBlack
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Matches", totalMatches.toString(), Modifier.weight(1f))
                StatCard("Wins", wins.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Losses", losses.toString(), Modifier.weight(1f))
                StatCard("Win Rate", "$winRate%", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewStatistics,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBlue
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("View Detailed Statistics")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewGoals,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppBlue
                )
            ) {
                Text("🎯 Goals & Progress →")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Training Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppGrey
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        "🏸 Weekly Training",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "${weeklyMinutes / 60} hr ${weeklyMinutes % 60} min completed",
                        fontWeight = FontWeight.SemiBold,
                        color = AppBlack
                    )

                    Text(
                        "${weeklyTraining.size} training sessions this week",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onViewTraining,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppBlue
                        )
                    ) {
                        Text("Open Training Tracker →")
                    }

                    OutlinedButton(
                        onClick = onViewTrainingAnalytics,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppBlue
                        )
                    ) {
                        Text("View Training Analytics →")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Matches",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppBlack
                )

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppBlue
                    )
                ) {
                    Text("+ Add Match")
                }
            }

            if (matches.isNotEmpty()) {
                TextButton(
                    onClick = onViewAll,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("View All Matches →", color = AppBlue)
                }
            }

            if (matches.isEmpty()) {
                Text(
                    "No matches recorded yet. Add your first match!",
                    color = Color.Gray
                )
            } else {
                matches.take(3).forEach { match ->
                    MatchCard(
                        opponent = match.opponent,
                        score = "${match.playerScore} - ${match.opponentScore}",
                        result = if (
                            match.playerScore > match.opponentScore
                        ) "WIN" else "LOSS",
                        playedAt = match.playedAt,
                        onDelete = { matchToDelete = match }
                    )

                    Text(
                        "${match.matchType}${if (match.notes.isNotBlank()) " • ${match.notes}" else ""}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 3.dp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showDialog) {
        AddMatchDialog(
            onDismiss = { showDialog = false },
            onSave = { record ->
                onAddMatch(
                    record.opponent,
                    record.playerScore,
                    record.opponentScore,
                    record.matchType,
                    record.notes
                )
                showDialog = false
            }
        )
    }

    matchToDelete?.let { match ->
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text("Delete Match?") },
            text = {
                Text("Delete the match against ${match.opponent}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteMatch(match)
                        matchToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddMatchDialog(
    onDismiss: () -> Unit,
    onSave: (MatchRecord) -> Unit
) {
    var opponent by remember { mutableStateOf("") }
    var playerScore by remember { mutableStateOf("") }
    var opponentScore by remember { mutableStateOf("") }
    var matchType by remember { mutableStateOf("Singles") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Match") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent Name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = playerScore,
                    onValueChange = {
                        playerScore = it.filter(Char::isDigit)
                    },
                    label = { Text("Your Score") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = opponentScore,
                    onValueChange = {
                        opponentScore = it.filter(Char::isDigit)
                    },
                    label = { Text("Opponent Score") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                Text("Match Type", fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = matchType == "Singles",
                        onClick = { matchType = "Singles" }
                    )
                    Text("Singles")

                    Spacer(modifier = Modifier.width(10.dp))

                    RadioButton(
                        selected = matchType == "Doubles",
                        onClick = { matchType = "Doubles" }
                    )
                    Text("Doubles")
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Match Notes (optional)") },
                    minLines = 2
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
                    val myScore = playerScore.toIntOrNull()
                    val theirScore = opponentScore.toIntOrNull()

                    when {
                        opponent.isBlank() ->
                            errorMessage = "Enter opponent name"

                        myScore == null || theirScore == null ->
                            errorMessage = "Enter valid scores"

                        myScore < 0 || theirScore < 0 ->
                            errorMessage = "Scores cannot be negative"

                        myScore == theirScore ->
                            errorMessage = "Scores cannot be equal"

                        else -> onSave(
                            MatchRecord(
                                opponent.trim(),
                                myScore,
                                theirScore,
                                matchType,
                                notes.trim()
                            )
                        )
                    }
                }
            ) {
                Text("Save Match", color = AppBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppGrey
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AppBlue
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(title, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MatchCard(
    opponent: String,
    score: String,
    result: String,
    playedAt: Long,
    onDelete: () -> Unit
) {
    val formattedDate = remember(playedAt) {
        java.text.SimpleDateFormat(
            "dd MMM yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date(playedAt))
    }

    val formattedTime = remember(playedAt) {
        java.text.SimpleDateFormat(
            "hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date(playedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppGrey
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    opponent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppBlack
                )

                Text("Score: $score", fontSize = 13.sp, color = Color.Gray)

                Text(
                    "$formattedDate • $formattedTime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    result,
                    fontWeight = FontWeight.Bold,
                    color = if (result == "WIN") {
                        Color(0xFF168653)
                    } else {
                        Color.Red
                    }
                )

                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}
