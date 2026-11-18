
package com.example.badmintontracker

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

private val BackupBlue = Color(0xFF48B2D2)
private val BackupBlack = Color(0xFF1B191D)
private val BackupGrey = Color(0xFFEEEEEE)

@Composable
fun BackupScreen(
    matches: List<MatchEntity>,
    trainingSessions: List<TrainingEntity>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var statusMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->

        if (uri != null) {
            try {
                val goals = GoalStorage.loadGoals(context)

                val backupJson = BackupManager.createBackupJson(
                    matches = matches,
                    trainingSessions = trainingSessions,
                    goals = goals
                )

                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(backupJson.toByteArray(Charsets.UTF_8))
                    output.flush()
                } ?: throw IllegalStateException("Unable to create backup file")

                isSuccess = true
                statusMessage = "Backup exported successfully!"
            } catch (exception: Exception) {
                isSuccess = false
                statusMessage = "Backup failed: ${exception.localizedMessage}"
            }
        }
    }

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
                    Text("← Back", color = BackupBlue)
                }

                Text(
                    "Backup & Restore",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackupBlack
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
                "DATA MANAGEMENT",
                color = BackupBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Keep your badminton records safe with a backup.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BackupGrey
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Your Backup Includes",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackupBlack
                    )

                    BackupItem("🏸 Match history")
                    BackupItem("🏆 Scores and match results")
                    BackupItem("📝 Match types and notes")
                    BackupItem("💪 Training sessions and duration")
                    BackupItem("🎯 Weekly goals")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BackupGrey
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Export Backup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackupBlack
                    )

                    Text(
                        "Save all your matches, training records and goals as a JSON file.",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = {
                            backupLauncher.launch(
                                "BadmintonTracker_Backup.json"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BackupBlue
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export Backup")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BackupGrey
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Backup Information",
                        fontWeight = FontWeight.Bold,
                        color = BackupBlack
                    )

                    BackupInfoRow(
                        "Total Matches",
                        matches.size.toString()
                    )

                    BackupInfoRow(
                        "Training Sessions",
                        trainingSessions.size.toString()
                    )

                    BackupInfoRow(
                        "Backup Format",
                        "JSON"
                    )
                }
            }

            if (statusMessage.isNotBlank()) {
                Text(
                    text = statusMessage,
                    color = if (isSuccess) {
                        Color(0xFF168653)
                    } else {
                        Color.Red
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                "Keep your exported backup file somewhere safe. You can use it to restore your records in a future version.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BackupItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text,
            color = BackupBlack,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BackupInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color.DarkGray)
        Text(
            value,
            color = BackupBlack,
            fontWeight = FontWeight.Bold
        )
    }
}
