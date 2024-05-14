package uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.formatTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Composable function representing the main screen for displaying schedules.
 * @param navController: Navigation controller to navigate between screens.
 * @param userViewModel: ViewModel for managing user data.
 */
@Composable
fun ScheduleScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // Collect user's study schedules
    val schedulesState by userViewModel.schedules.observeAsState(initial = emptyList())

    // Fetch user's study schedules
    LaunchedEffect(Unit) {
        userViewModel.getSchedules()
    }

    // Get unique days of the week from schedules and sort them by day order
    val uniqueDays = schedulesState.map { it.dayOfWeek }.distinct().sortedBy {
        // Convert day name to its corresponding index in the week
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("EEEE", Locale.getDefault()).parse(it)!!
        calendar.get(Calendar.DAY_OF_WEEK)
    }

    Scaffold(
        topBar = {
            // Displaying the top app bar
            NonMainTopAppBar(
                title = stringResource(R.string.schedule)
            )
        },
        floatingActionButton = {
            // Floating action button for adding new schedules
            FloatingActionButton(
                onClick = {
                    // Navigate to add schedule screen
                    navController.navigate(Screen.AddSchedule.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.addSchedule))
            }
        },
        bottomBar = {
            MainPageNavigationBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        // Column layout for displaying schedules
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            if (schedulesState.isEmpty()) {
                // If there are no schedules at all, display the "No Schedule" card
                NoScheduleCard {
                    // Navigate to add session screen
                    navController.navigate(Screen.AddSchedule.route)
                }
            } else {
                // Display cards for each unique day of the week
                uniqueDays.forEach { day ->
                    val schedulesForDay = schedulesState.filter { it.dayOfWeek == day }

                    ScheduleDayCard(
                        day = day,
                        schedules = schedulesForDay,
                        navController = navController
                    )
                }
            }
        }
    }
}

/**
 * Composable function for displaying a card containing schedules for a specific day.
 * @param day: Day of the week for which schedules are displayed.
 * @param schedules: List of schedules for the specified day.
 * @param navController: Navigation controller to navigate between screens.
 */
@Composable
fun ScheduleDayCard(
    day: String,
    schedules: List<Schedule>,
    navController: NavController
) {
    // Sort schedules by start time
    val sortedSchedules = schedules.sortedBy { it.startTime }

    // Card to display schedules for the day
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { navController.navigate(Screen.DaySchedule.route + "/${day}") }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Display day of the week
            Text(
                text = day,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Display each schedule for the day
            sortedSchedules.forEach { schedule ->
                Row(
                    verticalAlignment = CenterVertically
                ) {
                    // Indicate if it's a repeating session
                    if (schedule.repeat) {
                        Icon(
                            Icons.Filled.Repeat,
                            contentDescription = stringResource(R.string.repeating),
                            modifier = Modifier.padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = stringResource(R.string.schedule),
                            modifier = Modifier.padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Column {
                        // Display focus and description
                        Text(
                            text = "${schedule.focus} - ${schedule.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Display start and end time
                        Text(
                            text = "${formatTime(schedule.startTime!!)} - ${formatTime(schedule.endTime!!)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

/**
 * Composable function for displaying a card when there are no schedules for a specific day.
 * @param onAddSessionClick: Callback function to handle click event for adding a new session.
 */
@Composable
fun NoScheduleCard(
    onAddSessionClick: () -> Unit
) {
    // Card to prompt user to add a new schedule
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddSessionClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display message indicating no schedules
            Text(
                text = stringResource(R.string.noSessions),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Button to add a new schedule
            Button(
                onClick = { onAddSessionClick() }
            ) {
                Text(text = stringResource(R.string.addSchedule))
            }
        }
    }
}
