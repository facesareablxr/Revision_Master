package uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.ButtonSpinner
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Composable function for the Add Schedule screen.
 *
 * This screen allows users to add a new study schedule. It provides input fields for the user to
 * specify the day of the week, start and end time, event type, description, and whether the session
 * repeats. Additionally, users can select one or more decks associated with the schedule. The
 * screen validates the input data and saves the schedule if all fields are filled correctly and
 * there are no overlapping sessions.
 *
 * @param navController The NavController for navigating within the app.
 * @param userViewModel The ViewModel for accessing user data.
 * @param flashcardViewModel The ViewModel for accessing flashcard data.
 */
@Composable
fun AddScheduleScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Local state variables for schedule details
    var dayOfWeek by remember { mutableStateOf("Day") } // Selected day of the week
    var startTime by remember { mutableStateOf(0L) } // Selected start time
    var endTime by remember { mutableStateOf(0L) } // Selected end time
    var sessionType by remember { mutableStateOf("Session Type") } // Selected event type
    var selectedDecks by remember { mutableStateOf<List<Deck>>(emptyList()) } // Selected decks
    var description by remember { mutableStateOf("") } // Description of the schedule
    var isRepeatingSession by remember { mutableStateOf(false) } // Flag for repeating session
    val showDialog = remember { mutableStateOf(false) } // Flag to show deck selection dialog

    // Collect user's study schedules
    val schedulesState by userViewModel.schedules.observeAsState(initial = emptyList())

    // Fetch decks from ViewModel
    val decks by flashcardViewModel.decks.collectAsState()

    // Get context
    val context = LocalContext.current as Activity

    // Fetch user's study schedules
    LaunchedEffect(Unit) {
        flashcardViewModel.getUserDecks()
        userViewModel.getSchedules()
    }

    // Scaffold with top app bar and content
    Scaffold(topBar = {
        // Top app bar with the title "Add Schedule"
        SmallTopAppBar(
            title = stringResource(R.string.addSchedule), navController = navController
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp), // Reduced horizontal padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Description input field
            DescriptionInput(
                description = description,
                onDescriptionChanged = { description = it }
            )

            // Dropdown for selecting event type
            EventTypeInput(
                sessionType = sessionType,
                onSessionTypeSelected = { sessionType = it })

            // Dropdown for selecting day of the week
            DayOfWeekInput(
                dayOfWeek = dayOfWeek,
                updateDayOfWeek = { dayOfWeek = it })

            // Start time picker
            StartTimePicker(
                startTime = startTime,
                onSelectedTime = {startTime = it}
            )

            // End time picker
            EndTimePicker(
                endTime = endTime,
                onSelectedTime = {endTime = it}
            )

            // Spacer for aesthetic reasons (making spacing even throughout)
            Spacer(modifier = Modifier.padding(4.dp))

            // Button to select decks
            Column(modifier = Modifier
                .clickable { showDialog.value = true }
                .padding(vertical = 8.dp, horizontal = 16.dp)) {
                Box(
                    modifier = Modifier.border(
                        width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(1.dp)
                    )
                ) {
                    Text(text = stringResource(R.string.selectDecks),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showDialog.value = true }
                            .align(Alignment.Center))
                }
            }

            // Display selected decks
            if (selectedDecks.isNotEmpty()) {
                selectedDecks.forEach { deck ->
                    Text(
                        text = "- ${deck.name}"
                    )
                }
            }

            // Dialog for selecting decks
            if (showDialog.value) {
                // Sorting decks alphabetically
                DecksCheckboxDialog(decks = decks.sortedBy { it.name },
                    selectedDecks = selectedDecks, onDeckSelected = { deck, isChecked ->
                        if (isChecked) {
                            selectedDecks += deck
                        } else {
                            selectedDecks -= deck
                        }
                    }) { showDialog.value = false }
            }

            // Toggle for repeating session
            RepeatingSessionToggle(
                isRepeating = isRepeatingSession,
                onToggle = { isRepeatingSession = it }
            )

            // Spacer for aesthetic reasons (making spacing even throughout)
            Spacer(modifier = Modifier.padding(8.dp))

            // Button to save the schedule
            Button(
                onClick =
                {
                    val overlappingSession = schedulesState.find { existingSchedule ->
                        existingSchedule.dayOfWeek == dayOfWeek && !(endTime <= existingSchedule.startTime!! ||
                                startTime >= existingSchedule.endTime!!)
                    }
                    // Validate start and end time
                    if (startTime > endTime) {
                        showToast(context, "End time must be after start time")
                    } else if (overlappingSession != null) {
                        showToast(context, "Session overlaps with an existing session")
                    } else if (dayOfWeek.isEmpty() || sessionType.isEmpty() || description.isEmpty()) {
                        showToast(context, "Fill all fields in.")
                    } else {
                        // If no overlapping sessions and valid start/end time, proceed to save
                        userViewModel.addSchedule(
                            dayOfWeek = dayOfWeek,
                            startTime = startTime,
                            endTime = endTime,
                            eventType = sessionType,
                            selectedDecks = selectedDecks,
                            description = description,
                            repeating = isRepeatingSession
                        )
                        // Navigate back after saving the schedule
                        navController.popBackStack()
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.saveSchedule))
            }
        }
    }
}

/**
 * Composable function for displaying a dialog to select decks.
 *
 * @param decks The list of available decks to select from.
 * @param selectedDecks The list of decks that are already selected.
 * @param onDeckSelected Callback function for when a deck is selected or unselected.
 * @param onDismiss Callback function for when the dialog is dismissed.
 */
@Composable
fun DecksCheckboxDialog(
    decks: List<Deck>,
    selectedDecks: List<Deck?>,
    onDeckSelected: (Deck, Boolean) -> Unit,
    onDismiss: (List<Deck>) -> Unit
) {
    // State to track the indices of selected decks
    var selectedIndices by remember {
        mutableStateOf(selectedDecks.map { deck -> decks.indexOf(deck) }
        )
    }

    // Dialog to display deck selection options
    AlertDialog(onDismissRequest = { onDismiss(emptyList()) }, title = {
        // Title of the dialog
        Text(
            text = stringResource(id = R.string.selectDecks),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }, confirmButton = {
        // Confirm button
        Button(onClick = {
            // When user confirms, pass the selected decks to onDismiss
            selectedIndices.mapNotNull { index ->
                if (index >= 0 && index < decks.size) {
                    decks[index]
                } else {
                    null
                }
            }
        }) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    }, text = {
        // Content of the dialog
        Column {
            decks.forEachIndexed { index, deck ->
                val isChecked = selectedIndices.contains(index)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox for selecting decks
                    Checkbox(
                        checked = isChecked, onCheckedChange = { checked ->
                            val newIndices = selectedIndices.toMutableList()
                            if (checked) {
                                newIndices.add(index)
                            } else {
                                newIndices.remove(index)
                            }
                            selectedIndices = newIndices
                            // Callback when a deck is selected or unselected
                            onDeckSelected(deck, checked)
                        }, modifier = Modifier.padding(end = 8.dp)
                    )
                    // Display deck name
                    Text(
                        text = deck.name, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    })
}

/**
 * Composable function for inputting description.
 *
 * @param description The current description value.
 * @param onDescriptionChanged Callback function for when the description changes.
 */
@Composable
fun DescriptionInput(
    description: String,
    onDescriptionChanged: (String) -> Unit
) {
    // Retrieve current focus manager
    val focusManager = LocalFocusManager.current
    // State to track if the text field is focused
    var isTextFieldFocused by remember { mutableStateOf(false) }

    // Text field for description input
    OutlinedTextField(value = description,
        onValueChange = { onDescriptionChanged(it) },
        label = { Text(stringResource(R.string.descriptionSchedule)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(FocusRequester())
            .onFocusChanged { isTextFieldFocused = it.isFocused },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(force = true) })
    )
}

/**
 * Composable function for selecting day of the week from a dropdown.
 *
 * @param updateDayOfWeek Callback function to update the selected day of the week.
 */
@Composable
fun DayOfWeekInput(
    dayOfWeek: String,
    updateDayOfWeek: (String) -> Unit
) {
    // Retrieve list of days of the week
    val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
    // Button spinner for selecting day of the week
    ButtonSpinner(
        items = daysOfWeek.toList(),
        modifier = Modifier,
        itemClick = {
            updateDayOfWeek(it)
        },
        label = dayOfWeek
    )
}

/**
 * Composable function for selecting start time using TimePicker.
 *
 * @param startTime Initial start time in milliseconds.
 * @param onSelectedTime Callback function to be invoked when the start time is selected.
 */
@Composable
fun StartTimePicker(
    startTime: Long,
    onSelectedTime: (Long) -> Unit,
) {
    // State to track if the TimePicker dialog should be shown
    var showTimePicker by remember { mutableStateOf(false) }
    // State for the TimePicker state
    val state = rememberTimePickerState()
    // Formatter for displaying time
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Show TimePicker dialog if needed
    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                // Set the selected time when confirmed
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                val selectedTime = cal.timeInMillis
                onSelectedTime(selectedTime)
                showTimePicker = false
            },
        ) {
            // Display TimePicker
            TimePicker(state = state)
        }
    }

    Column(modifier = Modifier
        .clickable { showTimePicker = true }
        .padding(16.dp)) {
        Box(
            modifier = Modifier.border(
                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(1.dp)
            )
        ) {
            // Display selected start time
            Text(text = "Start Time: ${formatter.format(startTime)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { showTimePicker = true }
                    .align(Alignment.Center))
        }
    }
}

/**
 * Composable function for selecting end time using TimePicker.
 *
 * @param endTime Initial end time in milliseconds.
 * @return Selected end time in milliseconds.
 */
@Composable
fun EndTimePicker(
    endTime: Long,
    onSelectedTime: (Long) -> Unit
){
    // State to track if the TimePicker dialog should be shown
    var showTimePicker by remember { mutableStateOf(false) }
    // State for the TimePicker state
    val state = rememberTimePickerState()
    // Formatter for displaying time
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    // Show TimePicker dialog if needed
    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                // Set the selected time when confirmed
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                val selectedTime = cal.timeInMillis
                onSelectedTime(selectedTime)
                showTimePicker = false
            },
        ) {
            // Display TimePicker
            TimePicker(state = state)
        }
    }

    Column(modifier = Modifier
        .clickable { showTimePicker = true }
        .padding(16.dp)) {
        Box(
            modifier = Modifier.border(
                width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(1.dp)
            )
        ) {
            // Display selected start time
            Text(text = "End Time: ${formatter.format(endTime)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { showTimePicker = true }
                    .align(Alignment.Center))
        }
    }
}

/**
 * This was taken from: https://stackoverflow.com/questions/75853449/timepickerdialog-in-jetpack-compose
 * as it was impossible to find Material 3 examples. Full credit to Abhimanyu on Stack Overflow for this.
 * Especially as M3 documentation was not available at time of development.
 * Composable function for displaying a TimePicker dialog.
 *
 * @param title The title of the dialog.
 * @param onCancel Callback function when the dialog is dismissed.
 * @param onConfirm Callback function when the user confirms the selection.
 * @param toggle Function to toggle the visibility of the TimePicker.
 * @param content Composable content of the dialog.
 */
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        // Surface to contain the dialog content
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            // Toggle function to control visibility
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title of the dialog
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                // Content of the dialog
                content()
                // Row for action buttons
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    // Cancel button
                    TextButton(
                        onClick = onCancel
                    ) { Text(stringResource(R.string.cancel)) }
                    // Confirm button
                    TextButton(
                        onClick = onConfirm
                    ) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}

/**
 * Composable function for selecting event type from a dropdown.
 *
 * @param sessionType Initial session type.
 * @param onSessionTypeSelected Callback function to update the selected event type.
 */
@Composable
fun EventTypeInput(
    sessionType : String,
    onSessionTypeSelected: (String) -> Unit
) {
    // Retrieve the list of event types from resources
    val eventTypes = stringArrayResource(id = R.array.event_types)
    // Button spinner for selecting event type
    ButtonSpinner(
        items = eventTypes.toList(), modifier = Modifier, itemClick = {
            // Callback when an event type is selected
            onSessionTypeSelected(it)
        },
        label = sessionType
    )
}

/**
 * Composable function for toggling repeating session.
 *
 * @param onToggle Callback function for toggling repeating session.
 */
@Composable
fun RepeatingSessionToggle(
    isRepeating: Boolean,
    onToggle: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        // Text label for repeating session
        Text(
            text = stringResource(R.string.repeatingSession),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Switch for toggling repeating session
        Switch(
            checked = isRepeating, onCheckedChange = {

                onToggle(it)
            }, modifier = Modifier
        )
    }
}
