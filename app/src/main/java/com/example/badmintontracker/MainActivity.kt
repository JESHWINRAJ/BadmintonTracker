
package com.example.badmintontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.badmintontracker.data.local.AppDatabase
import com.example.badmintontracker.data.repository.MatchRepository
import com.example.badmintontracker.ui.theme.BadmintonTrackerTheme
import com.example.badmintontracker.ui.viewmodel.MatchViewModel
import com.example.badmintontracker.ui.viewmodel.MatchViewModelFactory

private val NavigationBlue = Color(0xFF48B2D2)
private val NavigationBlack = Color(0xFF1B191D)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = MatchRepository(
            matchDao = database.matchDao(),
            trainingDao = database.trainingDao()
        )

        val factory = MatchViewModelFactory(repository)

        val viewModel = ViewModelProvider(
            this,
            factory
        )[MatchViewModel::class.java]

        setContent {
            BadmintonTrackerTheme {
                BadmintonMainNavigation(
                    viewModel = viewModel
                )
            }
        }
    }
}

private data class NavigationItem(
    val title: String,
    val icon: String
)

@Composable
private fun BadmintonMainNavigation(
    viewModel: MatchViewModel
) {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val matches by viewModel.matches.collectAsState()
    val trainingSessions by viewModel.trainingSessions.collectAsState()

    val navigationItems = listOf(
        NavigationItem("Home", "⌂"),
        NavigationItem("Matches", "🏸"),
        NavigationItem("Training", "💪"),
        NavigationItem("Statistics", "📊"),
        NavigationItem("Profile", "👤")
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navigationItems.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            Text(
                                text = item.icon,
                                color = if (selectedTab == index) {
                                    NavigationBlue
                                } else {
                                    Color.Gray
                                }
                            )
                        },
                        label = {
                            Text(
                                text = item.title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavigationBlue,
                            selectedTextColor = NavigationBlue,
                            indicatorColor = Color(0xFFE5F7FC),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (selectedTab) {

                // HOME
                0 -> DashboardScreen(
                    viewModel = viewModel
                )

                // MATCH HISTORY
                1 -> MatchHistoryScreen(
                    matches = matches,
                    onBack = {
                        selectedTab = 0
                    },
                    onDelete = {
                        viewModel.deleteMatch(it)
                    },
                    onUpdate = {
                        viewModel.updateMatch(it)
                    }
                )

                // TRAINING
                2 -> TrainingScreen(
                    sessions = trainingSessions,
                    onBack = {
                        selectedTab = 0
                    },
                    onAddTraining = { type, duration ->
                        viewModel.addTraining(type, duration)
                    },
                    onDeleteTraining = {
                        viewModel.deleteTraining(it)
                    }
                )

                // STATISTICS
                3 -> StatisticsScreen(
                    matches = matches,
                    onBack = {
                        selectedTab = 0
                    }
                )

                // PROFILE
                4 -> PlayerProfileScreen(
                    onBack = {
                        selectedTab = 0
                    }
                )
            }
        }
    }
}
