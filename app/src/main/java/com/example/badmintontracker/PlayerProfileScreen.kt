
package com.example.badmintontracker

import androidx.compose.foundation.background
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

private val ProfileBlue = Color(0xFF48B2D2)
private val ProfileBlack = Color(0xFF1B191D)
private val ProfileGrey = Color(0xFFEEEEEE)

@Composable
fun PlayerProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var profile by remember {
        mutableStateOf(ProfileStorage.loadProfile(context))
    }

    var notifications by remember {
        mutableStateOf(
            ProfileStorage.loadSetting(context, "notifications", true)
        )
    }

    var savedMessage by remember { mutableStateOf(false) }

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ProfileBlack,
        unfocusedTextColor = ProfileBlack,
        cursorColor = ProfileBlue,
        focusedLabelColor = ProfileBlue,
        unfocusedLabelColor = Color.Gray,
        focusedBorderColor = ProfileBlue,
        unfocusedBorderColor = Color.DarkGray
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Back", color = ProfileBlue)
                }

                Text(
                    "Player Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileBlack
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "PLAYER INFORMATION",
                color = ProfileBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            ProfileCard {
                OutlinedTextField(
                    value = profile.playerName,
                    onValueChange = {
                        profile = profile.copy(playerName = it)
                        savedMessage = false
                    },
                    label = { Text("Player Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = inputColors
                )

                ProfileDropdown(
                    label = "Playing Style",
                    selected = profile.playingStyle,
                    options = listOf("Attacking", "Defensive", "All-round"),
                    onSelect = {
                        profile = profile.copy(playingStyle = it)
                        savedMessage = false
                    }
                )

                ProfileDropdown(
                    label = "Playing Category",
                    selected = profile.playingCategory,
                    options = listOf("Singles", "Doubles", "Both"),
                    onSelect = {
                        profile = profile.copy(playingCategory = it)
                        savedMessage = false
                    }
                )

                OutlinedTextField(
                    value = profile.preferredRacket,
                    onValueChange = {
                        profile = profile.copy(preferredRacket = it)
                        savedMessage = false
                    },
                    label = { Text("Preferred Racket") },
                    placeholder = { Text("Enter your racket model") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = inputColors
                )

                ProfileDropdown(
                    label = "Playing Hand",
                    selected = profile.playingHand,
                    options = listOf("Right", "Left"),
                    onSelect = {
                        profile = profile.copy(playingHand = it)
                        savedMessage = false
                    }
                )
            }

            Button(
                onClick = {
                    ProfileStorage.saveProfile(context, profile)
                    savedMessage = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProfileBlue
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Profile", color = Color.White)
            }

            if (savedMessage) {
                Text(
                    "Profile saved successfully!",
                    color = Color(0xFF168653),
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(color = ProfileGrey)

            Text(
                "APP SETTINGS",
                color = ProfileBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            ProfileCard {
                SettingRow(
                    title = "Notifications",
                    description = "Save your notification preference",
                    checked = notifications,
                    onCheckedChange = {
                        notifications = it
                        ProfileStorage.saveSetting(
                            context,
                            "notifications",
                            it
                        )
                    }
                )
            }

            Text(
                "ABOUT",
                color = ProfileBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            ProfileCard {
                Text(
                    "BadmintonTracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ProfileBlack
                )

                Text(
                    "Track your matches, monitor your training and improve your badminton performance.",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )

                Text(
                    "App Version: 1.0",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProfileCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProfileGrey
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun ProfileDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            color = ProfileBlack
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ProfileBlack
                )
            ) {
                Text(
                    selected,
                    color = ProfileBlack,
                    modifier = Modifier.weight(1f)
                )
                Text("▼", color = ProfileBlue)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = ProfileBlack
            )
            Text(
                description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ProfileBlue
            )
        )
    }
}
