
package com.example.badmintontracker

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
import com.example.badmintontracker.data.local.MatchEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HistoryBlue = Color(0xFF48B2D2)
private val HistoryBlack = Color(0xFF1B191D)
private val HistoryGrey = Color(0xFFEEEEEE)

private enum class ResultFilter(val label: String) {
    ALL("All"),
    WINS("Wins"),
    LOSSES("Losses")
}

private enum class TypeFilter(val label: String) {
    ALL("All Types"),
    SINGLES("Singles"),
    DOUBLES("Doubles")
}

@Composable
fun MatchHistoryScreen(
    matches: List<MatchEntity>,
    onBack: () -> Unit,
    onDelete: (MatchEntity) -> Unit,
    onUpdate: (MatchEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var resultFilter by remember { mutableStateOf(ResultFilter.ALL) }
    var typeFilter by remember { mutableStateOf(TypeFilter.ALL) }

    var matchToDelete by remember { mutableStateOf<MatchEntity?>(null) }
    var matchToEdit by remember { mutableStateOf<MatchEntity?>(null) }

    val filteredMatches = remember(
        matches,
        searchQuery,
        resultFilter,
        typeFilter
    ) {
        matches
            .filter { match ->
                match.opponent.contains(searchQuery.trim(), ignoreCase = true)
            }
            .filter { match ->
                when (resultFilter) {
                    ResultFilter.ALL -> true
                    ResultFilter.WINS -> match.playerScore > match.opponentScore
                    ResultFilter.LOSSES -> match.playerScore < match.opponentScore
                }
            }
            .filter { match ->
                when (typeFilter) {
                    TypeFilter.ALL -> true
                    TypeFilter.SINGLES -> match.matchType == "Singles"
                    TypeFilter.DOUBLES -> match.matchType == "Doubles"
                }
            }
            .sortedByDescending { it.playedAt }
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text(
                        "← Back",
                        color = HistoryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "Match History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HistoryBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search opponent...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    Text("🔍")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HistoryBlue,
                    unfocusedBorderColor = HistoryGrey,
                    focusedLabelColor = HistoryBlue,
                    cursorColor = HistoryBlue
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "Match Result",
                fontWeight = FontWeight.Bold,
                color = HistoryBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultFilter.values().forEach { filter ->
                    FilterChip(
                        selected = resultFilter == filter,
                        onClick = { resultFilter = filter },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HistoryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Match Type",
                fontWeight = FontWeight.Bold,
                color = HistoryBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeFilter.values().forEach { filter ->
                    FilterChip(
                        selected = typeFilter == filter,
                        onClick = { typeFilter = filter },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HistoryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Search Results",
                    fontWeight = FontWeight.Bold,
                    color = HistoryBlack
                )

                Text(
                    "${filteredMatches.size} matches",
                    color = HistoryBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏸", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "No matches found",
                            fontWeight = FontWeight.Bold,
                            color = HistoryBlack
                        )
                        Text(
                            "Try changing your search or filters.",
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
                        items = filteredMatches,
                        key = { it.id }
                    ) { match ->
                        HistoryMatchCard(
                            match = match,
                            onEdit = { matchToEdit = match },
                            onDelete = { matchToDelete = match }
                        )
                    }
                }
            }
        }
    }

    matchToEdit?.let { match ->
        EditMatchDialog(
            match = match,
            onDismiss = { matchToEdit = null },
            onSave = { updated ->
                onUpdate(updated)
                matchToEdit = null
            }
        )
    }

    matchToDelete?.let { match ->
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text("Delete Match?") },
            text = { Text("Delete the match against ${match.opponent}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(match)
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
private fun HistoryMatchCard(
    match: MatchEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isWin = match.playerScore > match.opponentScore

    val date = remember(match.playedAt) {
        SimpleDateFormat(
            "dd MMM yyyy • hh:mm a",
            Locale.getDefault()
        ).format(Date(match.playedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = HistoryGrey
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "vs ${match.opponent}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = HistoryBlack
                )

                Text(
                    if (isWin) "WIN" else "LOSS",
                    fontWeight = FontWeight.Bold,
                    color = if (isWin) Color(0xFF168653) else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Score: ${match.playerScore} - ${match.opponentScore}",
                fontWeight = FontWeight.SemiBold,
                color = HistoryBlack
            )

            Text(
                "Match type: ${match.matchType}",
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Text(
                date,
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (match.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Notes: ${match.notes}",
                    fontSize = 13.sp,
                    color = HistoryBlack
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = HistoryBlue
                    )
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun EditMatchDialog(
    match: MatchEntity,
    onDismiss: () -> Unit,
    onSave: (MatchEntity) -> Unit
) {
    var opponent by remember(match.id) { mutableStateOf(match.opponent) }
    var playerScore by remember(match.id) { mutableStateOf(match.playerScore.toString()) }
    var opponentScore by remember(match.id) { mutableStateOf(match.opponentScore.toString()) }
    var matchType by remember(match.id) { mutableStateOf(match.matchType) }
    var notes by remember(match.id) { mutableStateOf(match.notes) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Match") },
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
                    onValueChange = { playerScore = it.filter(Char::isDigit) },
                    label = { Text("Your Score") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = opponentScore,
                    onValueChange = { opponentScore = it.filter(Char::isDigit) },
                    label = { Text("Opponent Score") },
                    singleLine = true
                )

                Text("Match Type", fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = matchType == "Singles",
                        onClick = { matchType = "Singles" }
                    )
                    Text("Singles")

                    Spacer(modifier = Modifier.width(12.dp))

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

                if (error.isNotBlank()) {
                    Text(
                        error,
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
                        opponent.isBlank() -> error = "Enter opponent name"
                        myScore == null || theirScore == null ->
                            error = "Enter valid scores"
                        myScore < 0 || theirScore < 0 ->
                            error = "Scores cannot be negative"
                        myScore == theirScore ->
                            error = "Scores cannot be equal"
                        else -> onSave(
                            match.copy(
                                opponent = opponent.trim(),
                                playerScore = myScore,
                                opponentScore = theirScore,
                                matchType = matchType,
                                notes = notes.trim()
                            )
                        )
                    }
                }
            ) {
                Text("Save", color = HistoryBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
