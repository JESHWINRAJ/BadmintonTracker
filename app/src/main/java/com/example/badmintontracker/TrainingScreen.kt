
package com.example.badmintontracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badmintontracker.data.local.TrainingEntity

private val TrainingGreen = Color(0xFF48B2D2)
private val TrainingBackground = Color(0xFFFFFFFF)
private val TrainingDark = Color(0xFF1B191D)
private val TrainingLightGreen = Color(0xFFEEEEEE)

private val trainingTypes = listOf(
    "Footwork",
    "Smash",
    "Defence",
    "Stamina",
    "Other"
)

@Composable
fun TrainingScreen(
    sessions: List<TrainingEntity>,
    onBack: () -> Unit,
    onAddTraining: (String, Int) -> Unit,
    onDeleteTraining: (TrainingEntity) -> Unit
) {

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var sessionToDelete by remember {
        mutableStateOf<TrainingEntity?>(null)
    }

    val calendar = remember {
        java.util.Calendar.getInstance()
    }

    calendar.set(
        java.util.Calendar.DAY_OF_WEEK,
        calendar.firstDayOfWeek
    )
    calendar.set(
        java.util.Calendar.HOUR_OF_DAY,
        0
    )
    calendar.set(
        java.util.Calendar.MINUTE,
        0
    )
    calendar.set(
        java.util.Calendar.SECOND,
        0
    )
    calendar.set(
        java.util.Calendar.MILLISECOND,
        0
    )

    val weekStart = calendar.timeInMillis

    val weekEnd = remember(weekStart) {
        val nextWeek = java.util.Calendar.getInstance()
        nextWeek.timeInMillis = weekStart
        nextWeek.add(java.util.Calendar.DAY_OF_YEAR, 7)
        nextWeek.timeInMillis
    }

    val weeklySessions = sessions.filter {
        it.playedAt >= weekStart && it.playedAt < weekEnd
    }

    val weeklyMinutes = weeklySessions.sumOf {
        it.durationMinutes
    }

    val hours = weeklyMinutes / 60
    val minutes = weeklyMinutes % 60

    Scaffold(
        containerColor = TrainingBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = onBack) {
                    Text(
                        "← Back",
                        color = TrainingGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Training Tracker",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrainingDark
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TrainingLightGreen
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "THIS WEEK",
                        color = TrainingGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "$hours hr $minutes min",
                        color = TrainingDark,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "${weeklySessions.size} training sessions completed",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    showAddDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrainingGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "+ Add Training Session",
                    modifier = Modifier.padding(5.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Training History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TrainingDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (sessions.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("🏸", fontSize = 45.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            "No training sessions yet",
                            fontWeight = FontWeight.Bold,
                            color = TrainingDark
                        )

                        Text(
                            "Add your first training session.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {

                    items(
                        items = sessions,
                        key = { it.id }
                    ) { session ->

                        TrainingSessionCard(
                            session = session,
                            onDelete = {
                                sessionToDelete = session
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        AddTrainingDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { type, duration ->
                onAddTraining(type, duration)
                showAddDialog = false
            }
        )
    }

    sessionToDelete?.let { session ->

        AlertDialog(
            onDismissRequest = {
                sessionToDelete = null
            },
            title = {
                Text("Delete Training?")
            },
            text = {
                Text("Delete this training session?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTraining(session)
                        sessionToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        sessionToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


// ADD TRAINING DIALOG

@Composable
private fun AddTrainingDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {

    var selectedType by remember {
        mutableStateOf(trainingTypes.first())
    }

    var duration by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Add Training Session")
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Training Type",
                    fontWeight = FontWeight.Bold
                )

                trainingTypes.forEach { type ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TrainingGreen
                            )
                        )

                        Text(type)
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = {
                        duration = it.filter { char ->
                            char.isDigit()
                        }
                        errorMessage = ""
                    },
                    label = {
                        Text("Duration in minutes")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val durationValue = duration.toIntOrNull()

                    when {

                        durationValue == null -> {
                            errorMessage = "Enter a valid duration"
                        }

                        durationValue <= 0 -> {
                            errorMessage = "Duration must be greater than zero"
                        }

                        durationValue > 1440 -> {
                            errorMessage = "Maximum duration is 1440 minutes"
                        }

                        else -> {
                            onSave(selectedType, durationValue)
                        }
                    }
                }
            ) {
                Text(
                    "Save Session",
                    color = TrainingGreen
                )
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


// TRAINING SESSION CARD

@Composable
private fun TrainingSessionCard(
    session: TrainingEntity,
    onDelete: () -> Unit
) {

    val formattedDate = remember(session.playedAt) {
        java.text.SimpleDateFormat(
            "dd MMM yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date(session.playedAt))
    }

    val formattedTime = remember(session.playedAt) {
        java.text.SimpleDateFormat(
            "hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date(session.playedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    session.trainingType,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrainingDark
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    "${session.durationMinutes} minutes",
                    color = TrainingGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "$formattedDate • $formattedTime",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            TextButton(onClick = onDelete) {
                Text(
                    "Delete",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}
