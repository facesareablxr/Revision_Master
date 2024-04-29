package uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
/*

*/
/**
 * Top-level composable function for the Schedule Screen.
 * @param navController is the NavHostController for navigation
 * @param viewModel is the FitnessViewModel for accessing workout data
 *//*

@Composable
fun ScheduleScreenTopLevel(
    navController: NavHostController,
    viewModel: UserViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    // State variable for storing the list of workouts for the week
    var sessionsForWeek by remember {
        mutableStateOf<List<RevisionSession>>(emptyList())
    }

    // Fetching workouts for the week asynchronously
    LaunchedEffect(key1 = Unit) {
        try {
            val result = viewModel.readWorkoutsWithExercises()
            coroutineScope.launch {
                workoutsForWeek = result
            }
        } catch (e: Exception) {
            // Handle the exception or log it
        }
    }

    // Display the Schedule Screen
    ScheduleScreen(navController, workoutsForWeek, onDelete = { deletedWorkout ->
        // Remove the deleted workout from the list
        workoutsForWeek = workoutsForWeek.filter { it.dayOfWeek != deletedWorkout }
    })
}

*/
/**
 * Represents the Schedule Screen content, displaying each workout scheduled for the week.
 * @param navController is the NavHostController for navigation
 * @param workoutsForWeek is the list of workouts with exercises for the week
 * @param onDelete is the callback for deleting a workout
 *//*

@Composable
fun ScheduleScreen(
    navController: NavHostController,
    workoutsForWeek: List<WorkoutWithExercises>,
    onDelete: (String) -> Unit
) {
    // Coroutine scope for handling asynchronous operations
    val coroutineScope = rememberCoroutineScope()

    // Display the Top Level Scaffold with a floating action button
    TopLevelScaffold(
        navController = navController,
        coroutineScope = coroutineScope,
        floatingActionButton = {
            // Floating action button for adding a new workout
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddWorkout.route)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add workout")
            }
        },
        pageContent = { innerPadding ->
            // Surface for the page content
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Display the Schedule Screen Content
                ScheduleScreenContent(workoutsForWeek, onDelete)
            }
        }
    )
}

*/
/**
 * Represents the Schedule Screen content, displaying each workout scheduled for the week.
 * @param workoutsForWeek is the list of workouts with exercises for the week
 * @param onDelete is the callback for deleting a workout
 *//*

@Composable
fun ScheduleScreenContent(
    workoutsForWeek: List<WorkoutWithExercises>,
    onDelete: (String) -> Unit
) {
    // List of days in order
    val dayOrder = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    // Sorting workouts based on the order of days
    val sortedWorkouts = workoutsForWeek.sortedBy { dayOrder.indexOf(it.dayOfWeek) }

    // LazyColumn for displaying workout cards
    LazyColumn {
        items(sortedWorkouts) { workout ->
            // Display each Workout Card
            WorkoutCard(workout, onDelete)
        }
    }
}

*/
/**
 * Represents a stylized workout card that displays workout details.
 * @param workout is the WorkoutWithExercises object to display
 * @param onDelete is the callback for deleting a workout
 *//*

@Composable
fun WorkoutCard(
    workout: WorkoutWithExercises,
    onDelete: (String) -> Unit
) {
    // Check if there is at least one exercise in the workout
    workout.exercises.firstOrNull()?.let { firstExercise ->


        // Mutable state for handling dropdown menu visibility
        var isMenuVisible by remember { mutableStateOf(false) }

        // Card with rounded corners
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Column for organizing workout details
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Row for workout day and three-dot button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Workout day
                    Text(text = workout.dayOfWeek, fontWeight = FontWeight.Bold)

                    // Three-dot button
                    Box {
                        IconButton(
                            onClick = { isMenuVisible = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = isMenuVisible,
                            onDismissRequest = { isMenuVisible = false },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(8.dp)
                        ) {
                            // Dropdown menu item for deleting the workout
                            DropdownMenuItem(
                                onClick = {
                                    isMenuVisible = false
                                    onDelete(workout.dayOfWeek)
                                }
                            ) {
                                Text("Delete Workout")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Workout focus
                Text(text = "Focus: ${workout.focus}")
                Spacer(modifier = Modifier.height(16.dp))

                // Display the image
                Image(
                    painter = painter,
                    contentDescription = "Exercise Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(225.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Text details for each exercise
                Text(text = "Exercises:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                workout.exercises.forEach { exercise ->
                    // Display details for each exercise
                    Text(
                        text = "${exercise.name} - ${exercise.sets} sets | ${exercise.reps} reps | ${exercise.weight} kg"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}*/
