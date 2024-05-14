package uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.showToast

@Composable
fun EditScheduleScreen(
    navController: NavController,
    scheduleId: String,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    var dayOfWeek by remember { mutableStateOf("Day") }
    var startTime by remember { mutableStateOf(0L) }
    var endTime by remember { mutableStateOf(0L) }
    var eventType by remember { mutableStateOf("Session Type") }
    var selectedDecks by remember { mutableStateOf<List<Deck?>>(emptyList()) }
    var description by remember { mutableStateOf("") }
    var isRepeatingSession by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    val schedulesState by userViewModel.schedule.observeAsState()
    val decksState by flashcardViewModel.decks.collectAsState()
    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        userViewModel.getScheduleDetails(scheduleId)
        flashcardViewModel.getUserDecks()
    }

    LaunchedEffect(schedulesState, decksState) {
        schedulesState?.let { schedule ->
            dayOfWeek = schedule.dayOfWeek
            startTime = schedule.startTime!!
            endTime = schedule.endTime!!
            description = schedule.description
            eventType = schedule.focus
            isRepeatingSession = schedule.repeat
            selectedDecks = schedule.decks
        }

        decksState.let { decks ->
            if (decks.isNotEmpty()) {
                selectedDecks = decks.filter { deck ->
                    schedulesState?.decks?.any { it!!.id == deck.id } == true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = stringResource(R.string.editSchedule),
                navController = navController
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            schedulesState?.let { it ->
                DayOfWeekInput(
                    dayOfWeek = it.dayOfWeek,
                    updateDayOfWeek = { dayOfWeek = it }
                )
            }

            DescriptionInput(
                description = description,
                onDescriptionChanged = { description = it }
            )

            schedulesState?.let { it ->
                EventTypeInput(
                    sessionType = it.focus,
                    onSessionTypeSelected = { eventType = it },
                )
            }

            // Start time picker
            schedulesState?.let { it ->
                StartTimePicker(
                    startTime = it.startTime!!,
                    onSelectedTime = { startTime = it }
                )
            }

            // End time picker
            schedulesState?.let { it ->
                EndTimePicker(
                    endTime = it.endTime!!,
                    onSelectedTime = { endTime = it }
                )
            }

            Column(
                modifier = Modifier
                    .clickable { showDialog.value = true }
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(1.dp)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.selectDecks),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showDialog.value = true }
                            .align(Alignment.Center)
                    )
                }
            }

            if (selectedDecks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Selected Decks:",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
                selectedDecks.forEach { deck ->
                    Text(
                        text = "- ${deck!!.name}",
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
            }

            if (showDialog.value) {
                DecksCheckboxDialog(
                    decks = decksState.sortedBy { it.name },
                    selectedDecks = selectedDecks,
                    onDeckSelected = { deck, isChecked ->
                        selectedDecks =
                            if (isChecked) selectedDecks + deck else selectedDecks - deck
                    }
                ) { showDialog.value = false }
            }

            RepeatingSessionToggle(
                isRepeating = isRepeatingSession,
                onToggle = { isRepeatingSession = it },
            )

            Button(
                onClick = {
                    if (startTime > endTime) {
                        showToast(context, "End time must be after start time")
                    } else if (dayOfWeek.isEmpty() || startTime == 0L || endTime == 0L || eventType.isEmpty() || description.isEmpty()) {
                        showToast(context, "Fill all fields in.")
                    } else {
                        userViewModel.updateSchedule(
                            scheduleId = scheduleId,
                            schedule = Schedule(
                                startTime = startTime,
                                endTime = endTime,
                                description = description,
                                decks = selectedDecks,
                                repeat = isRepeatingSession,
                                dayOfWeek = dayOfWeek,
                                id = scheduleId
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.saveSchedule))
            }
        }
    }
}
