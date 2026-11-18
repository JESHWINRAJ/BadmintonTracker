
package com.example.badmintontracker

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badmintontracker.data.local.MatchEntity
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

private val StatsGreen = Color(0xFF48B2D2)
private val StatsLightGreen = Color(0xFFEEEEEE)
private val StatsBackground = Color(0xFFFFFFFF)
private val StatsDark = Color(0xFF1B191D)

private enum class MatchPeriod(val label: String) {
    ALL_TIME("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month")
}

@Composable
fun StatisticsScreen(
    matches: List<MatchEntity>,
    onBack: () -> Unit
) {
    var selectedPeriod by remember {
        mutableStateOf(MatchPeriod.ALL_TIME)
    }

    val filteredMatches = remember(matches, selectedPeriod) {
        val now = Calendar.getInstance()
        val start = Calendar.getInstance()

        when (selectedPeriod) {
            MatchPeriod.ALL_TIME -> Long.MIN_VALUE

            MatchPeriod.TODAY -> {
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)
                start.timeInMillis
            }

            MatchPeriod.THIS_WEEK -> {
                start.set(Calendar.DAY_OF_WEEK, start.firstDayOfWeek)
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)
                start.timeInMillis
            }

            MatchPeriod.THIS_MONTH -> {
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0)
                start.set(Calendar.MINUTE, 0)
                start.set(Calendar.SECOND, 0)
                start.set(Calendar.MILLISECOND, 0)
                start.timeInMillis
            }
        }

        matches
            .filter { it.playedAt >= start.timeInMillis && it.playedAt <= now.timeInMillis }
            .sortedByDescending { it.playedAt }
    }

    val totalMatches = filteredMatches.size
    val wins = filteredMatches.count {
        it.playerScore > it.opponentScore
    }
    val losses = filteredMatches.count {
        it.playerScore < it.opponentScore
    }

    val winRate = if (totalMatches > 0) {
        wins * 100 / totalMatches
    } else {
        0
    }

    val bestWinStreak = remember(filteredMatches) {
        var current = 0
        var best = 0

        filteredMatches.asReversed().forEach { match ->
            if (match.playerScore > match.opponentScore) {
                current++
                best = maxOf(best, current)
            } else {
                current = 0
            }
        }

        best
    }

    val currentWinStreak = remember(filteredMatches) {
        var streak = 0

        for (match in filteredMatches) {
            if (match.playerScore > match.opponentScore) {
                streak++
            } else {
                break
            }
        }

        streak
    }

    Scaffold(
        containerColor = StatsBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text(
                        "← Back",
                        color = StatsGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Statistics",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatsDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Performance Period",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = StatsDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MatchPeriod.values().forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(period.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatsGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                item {
                    Text(
                        "${selectedPeriod.label} Performance",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatsDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            "Matches",
                            totalMatches.toString(),
                            Modifier.weight(1f)
                        )

                        AnalyticsCard(
                            "Wins",
                            wins.toString(),
                            Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            "Losses",
                            losses.toString(),
                            Modifier.weight(1f)
                        )

                        AnalyticsCard(
                            "Win Rate",
                            "$winRate%",
                            Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                "Win / Loss Analysis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatsDark
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            if (totalMatches == 0) {
                                Text(
                                    "No matches found for this period.",
                                    color = Color.Gray
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    if (wins > 0) {
                                        Box(
                                            modifier = Modifier
                                                .weight(wins.toFloat())
                                                .fillMaxHeight()
                                                .background(
                                                    StatsGreen,
                                                    RoundedCornerShape(6.dp)
                                                )
                                        )
                                    }

                                    if (losses > 0) {
                                        Box(
                                            modifier = Modifier
                                                .weight(losses.toFloat())
                                                .fillMaxHeight()
                                                .background(
                                                    Color(0xFFE57373),
                                                    RoundedCornerShape(6.dp)
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "🟢 Wins: $wins",
                                        color = StatsDark,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        "🔴 Losses: $losses",
                                        color = StatsDark,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Winning Streak",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatsDark
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StatsLightGreen
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Best Win Streak",
                                    color = StatsGreen,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "$bestWinStreak consecutive wins",
                                    color = StatsDark,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                "🏆 $bestWinStreak",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatsGreen
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                "Current Win Streak",
                                fontWeight = FontWeight.Bold,
                                color = StatsDark
                            )

                            Text(
                                "$currentWinStreak consecutive wins",
                                color = StatsGreen,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Recent Matches",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatsDark
                    )
                }

                if (filteredMatches.isEmpty()) {
                    item {
                        Text(
                            "No match history available for this period.",
                            color = Color.Gray
                        )
                    }
                } else {
                    items(
                        items = filteredMatches.take(5),
                        key = { it.id }
                    ) { match ->

                        val isWin = match.playerScore > match.opponentScore

                        val formattedDate = remember(match.playedAt) {
                            SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(Date(match.playedAt))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
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
                                        "vs ${match.opponent}",
                                        fontWeight = FontWeight.Bold,
                                        color = StatsDark
                                    )

                                    Text(
                                        "Score: ${match.playerScore} - ${match.opponentScore}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )

                                    Text(
                                        formattedDate,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    if (isWin) "WIN" else "LOSS",
                                    color = if (isWin) StatsGreen else Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                color = StatsGreen
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                title,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
