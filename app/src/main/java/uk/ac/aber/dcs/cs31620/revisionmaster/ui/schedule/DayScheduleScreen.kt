package uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBarWithDelete
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.formatTime

/**
 * Composable function for displaying the schedule for a specific day.
 *
 * @param day The day for which the schedule is being displayed.
 * @param navController The navigation controller for navigating between screens.
 * @param userViewModel View model for accessing user data.
 */
@Composable
fun DayScheduleScreen(
    day: String,
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // Collect user's study schedules
    val schedules by userViewModel.schedules.observeAsState(initial = null)
    var refreshTrigger by remember { mutableStateOf(false) }

    // Fetch user's study schedules, and refresh when refreshTrigger is set to true
    LaunchedEffect(Unit, refreshTrigger) {
        userViewModel.getSchedules()
    }

    // Sort the schedules by start time
    val sortedSchedules = schedules!!.sortedBy { it.startTime }

    // Filter schedules for the current day of the week
    val schedulesForCurrentDay = sortedSchedules.filter { it.dayOfWeek == day }

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            // Custom top app bar with delete option
            SmallTopAppBarWithDelete(
                title = day,
                navController = navController,
                onDeleteClick = {
                    // Delete schedules for the current day
                    userViewModel.deleteSchedulesContainingDay(day)
                    // Navigate back
                    navController.navigateUp()
                }
            )
        },
        floatingActionButton = {
            // Floating action button for adding new session
            FloatingActionButton(
                onClick = {
                    // Navigate to add session screen
                    navController.navigate(Screen.AddSchedule.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.addSchedule))
            }
        }
    ) { innerPadding ->
        // Column for arranging sessions vertically
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            // Loop through each session for the current day
            schedulesForCurrentDay.forEach { session ->
                SessionCard(
                    session = session,
                    navController = navController,
                    userViewModel = userViewModel,
                    // Callback function to trigger refresh
                    onSessionDeleted = { refreshTrigger = !refreshTrigger }
                )
            }
        }
    }
}

/**
 * Composable function for displaying a single session.
 *
 * @param session The session to be displayed.
 * @param navController The navigation controller for navigating between screens.
 * @param userViewModel View model for accessing user data.
 * @param onSessionDeleted Callback function triggered when session is deleted.
 */
@Composable
fun SessionCard(
    session: Schedule,
    navController: NavController,
    userViewModel: UserViewModel,
    onSessionDeleted: () -> Unit
) {
    // State for controlling dialog visibility
    var showDialog by remember { mutableStateOf(false) }

    // Card for displaying session information
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { showDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon indicating if it's a repeating session
                if (session.repeat) {
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
                // Session focus and description
                Text(
                    text = "${session.focus} - ${session.description}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Session start and end time
            Text(
                text = "${formatTime(session.startTime!!)} - ${formatTime(session.endTime!!)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

    // AlertDialog for edit and delete options
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(stringResource(R.string.adjustSession))
            },
            text = { stringResource(id = R.string.sessionChanges) },
            confirmButton = {
                Button(
                    onClick = {
                        // Navigate to edit screen
                        navController.navigate(Screen.EditSchedule.route + "/${session.id}")
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.edit))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Delete session
                        userViewModel.deleteSchedule(scheduleId = session.id)
                        // Trigger refresh
                        onSessionDeleted()
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            }
        )
    }
}


