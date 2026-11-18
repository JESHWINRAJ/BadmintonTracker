
package com.example.badmintontracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badmintontracker.data.local.TrainingEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val TrainingStatsBlue = Color(0xFF48B2D2)
private val TrainingStatsBlack = Color(0xFF1B191D)
private val TrainingStatsGrey = Color(0xFFEEEEEE)

@Composable
fun TrainingAnalyticsScreen(
    sessions: List<TrainingEntity>,
    onBack: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }

    val todayStart = remember(now) {
        Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val weekStart = remember(now) {
        Calendar.getInstance().apply {
            timeInMillis = todayStart
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }.timeInMillis
    }

    val monthStart = remember(now) {
        Calendar.getInstance().apply {
            timeInMillis = todayStart
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
    }

    val weeklySessions = sessions.filter {
        it.playedAt >= weekStart && it.playedAt <= now
    }

    val monthlySessions = sessions.filter {
        it.playedAt >= monthStart && it.playedAt <= now
    }

    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val weeklyMinutes = weeklySessions.sumOf { it.durationMinutes }
    val monthlyMinutes = monthlySessions.sumOf { it.durationMinutes }

    val categoryOrder = listOf(
        "Footwork", "Smash", "Defence", "Stamina", "Other"
    )

    val categoryMinutes = categoryOrder.associateWith { category ->
        sessions.filter { it.trainingType.equals(category, ignoreCase = true) }
            .sumOf { it.durationMinutes }
    }

    val mostPractised = categoryMinutes.maxByOrNull { it.value }

    val recentSessions = remember(sessions) {
        sessions.sortedByDescending { it.playedAt }.take(10)
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text(
                        "← Back",
                        color = TrainingStatsBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "Training Analytics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrainingStatsBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                item {
                    Text(
                        "Overall Training",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrainingStatsBlack
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TrainingMetricCard(
                            "Sessions",
                            sessions.size.toString(),
                            Modifier.weight(1f)
                        )

                        TrainingMetricCard(
                            "Total Hours",
                            formatDuration(totalMinutes),
                            Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        "Training Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrainingStatsBlack
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TrainingPeriodCard(
                            title = "This Week",
                            minutes = weeklyMinutes,
                            count = weeklySessions.size,
                            modifier = Modifier.weight(1f)
                        )

                        TrainingPeriodCard(
                            title = "This Month",
                            minutes = monthlyMinutes,
                            count = monthlySessions.size,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TrainingStatsGrey
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Training Categories",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrainingStatsBlack
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (totalMinutes == 0) {
                                Text(
                                    "Add training sessions to see your category breakdown.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            } else {
                                categoryOrder.forEach { category ->
                                    val minutes = categoryMinutes[category] ?: 0
                                    val fraction = minutes.toFloat() / totalMinutes

                                    Column(
                                        modifier = Modifier.padding(bottom = 14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                category,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TrainingStatsBlack
                                            )

                                            Text(
                                                formatDuration(minutes),
                                                color = Color.DarkGray,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .background(
                                                    Color.White,
                                                    RoundedCornerShape(10.dp)
                                                )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(fraction)
                                                    .fillMaxHeight()
                                                    .background(
                                                        TrainingStatsBlue,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F6FA)
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Most Practised Category",
                                color = TrainingStatsBlue,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                if ((mostPractised?.value ?: 0) > 0) {
                                    mostPractised!!.key
                                } else {
                                    "No data yet"
                                },
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrainingStatsBlack
                            )

                            Text(
                                if ((mostPractised?.value ?: 0) > 0) {
                                    "${formatDuration(mostPractised!!.value)} of training"
                                } else {
                                    "Start recording sessions to discover your focus area."
                                },
                                color = Color.DarkGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Recent Training Sessions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrainingStatsBlack
                    )
                }

                if (recentSessions.isEmpty()) {
                    item {
                        Text(
                            "No training sessions recorded yet.",
                            color = Color.Gray
                        )
                    }
                } else {
                    items(
                        items = recentSessions,
                        key = { it.id }
                    ) { session ->
                        TrainingAnalyticsCard(session)
                    }
                }
            }
        }
    }
}

private fun formatDuration(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

@Composable
private fun TrainingMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrainingStatsGrey
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = TrainingStatsBlue
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(title, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun TrainingPeriodCard(
    title: String,
    minutes: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrainingStatsGrey
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = TrainingStatsBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                formatDuration(minutes),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = TrainingStatsBlue
            )

            Text(
                "$count sessions",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TrainingAnalyticsCard(
    session: TrainingEntity
) {
    val date = remember(session.playedAt) {
        SimpleDateFormat(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        ).format(Date(session.playedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = TrainingStatsGrey
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
                    session.trainingType,
                    fontWeight = FontWeight.Bold,
                    color = TrainingStatsBlack
                )

                Text(
                    date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                formatDuration(session.durationMinutes),
                fontWeight = FontWeight.Bold,
                color = TrainingStatsBlue
            )
        }
    }
}
