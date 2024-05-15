package uk.ac.aber.dcs.cs31620.revisionmaster.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.Schedule
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.formatTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable function for the Home screen.
 *
 * @param navController NavController for navigation.
 * @param userViewModel ViewModel for user-related operations.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collect user's study schedules
    val schedulesState by userViewModel.schedules.observeAsState(initial = null)
    // Fetch user's study schedules
    LaunchedEffect(Unit) {
        userViewModel.getSchedules()
    }

    // Collect suggested decks state
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())
    // Fetch suggested decks
    LaunchedEffect(Unit) {
        flashcardViewModel.getUserDecks()
    }

    // Pick 5 random decks
    val randomDecks = if (decksState.size >= 5) {
        decksState.shuffled().take(5)
    } else {
        decksState
    }

    // Top-level scaffold
    TopLevelScaffold(
        navController = navController
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Two Cards in a Row
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    // Card for revising
                    CardWithIcon(
                        icon = Icons.Default.Book,
                        text = stringResource(R.string.revise),
                        subtext = stringResource(R.string.testYourself),
                        onClick = {
                            navController.popBackStack()
                            navController.navigate(Screen.Library.route)
                        }
                    )
                    // Card for adding new material
                    CardWithIcon(
                        icon = Icons.Default.LibraryAdd,
                        text = stringResource(R.string.newMaterial),
                        subtext = stringResource(R.string.createNewDeck),
                        onClick = {
                            navController.popBackStack()
                            navController.navigate(Screen.AddDeck.route)
                        }
                    )
                }

                // Suggested Decks
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    // Carousel with suggested decks
                    if (randomDecks.isNotEmpty()) {
                        CarouselWithPager(
                            title = stringResource(R.string.suggestedDecks),
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            decks = randomDecks,
                            onClick = {
                                navController.popBackStack()
                                navController.navigate(Screen.Library.route)
                            },
                            navController = navController
                        )
                    } else {
                        // Prompt to create a new deck
                        CreateDeckPrompt(onCreateDeckClick = { navController.navigate(Screen.AddDeck.route) })
                    }
                }
                // User's Study Schedule
                schedulesState?.let {
                    CardWithScheduleAndButton(
                        navController,
                        schedules = it
                    )
                }
            }
        }
    }
}

/**
 * Composable function for the card with icon.
 *
 * @param icon ImageVector icon for the card.
 * @param text Text displayed on the card.
 * @param subtext Subtext displayed on the card.
 * @param onClick Callback for clicking on the card.
 */
@Composable
fun CardWithIcon(
    icon: ImageVector,
    text: String,
    subtext: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(150.dp)
        ) {
            // Icon
            Icon(imageVector = icon, contentDescription = stringResource(R.string.icon))
            Spacer(modifier = Modifier.height(8.dp))
            // Text and subtext
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = subtext, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Composable function for the carousel with pager.
 *
 * @param title Title of the carousel.
 * @param icon ImageVector icon for the carousel.
 * @param decks List of suggested decks.
 * @param onClick Callback for clicking on the carousel.
 * @param navController NavController
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselWithPager(
    title: String,
    icon: ImageVector,
    decks: List<Deck>,
    onClick: () -> Unit,
    navController: NavController
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick.invoke() }
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.padding(horizontal = 115.dp))
            // Icon for navigation
            Icon(imageVector = icon, contentDescription = stringResource(R.string.next))
        }
        // Horizontal Pager for decks
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = rememberPagerState(pageCount = { decks.size }),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { page ->
            CardWithCarouselItem(decks[page], navController = navController)
        }
    }
}

/**
 * Composable function for the create deck prompt.
 *
 * @param onCreateDeckClick Callback for clicking on creating a new deck.
 */
@Composable
fun CreateDeckPrompt(
    onCreateDeckClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.suggestedDecks),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Prompt text
        Text(
            text = stringResource(id = R.string.addSuggestedDeck),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to create a new deck
        Button(
            onClick = onCreateDeckClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.createDeck))
        }
    }
}

/**
 * Composable function for the card with carousel item.
 *
 * @param deck The suggested deck.
 * @param navController NavController for navigation.
 * @param flashcardViewModel ViewModel for flashcard operations. Default is viewModel().
 */
@Composable
fun CardWithCarouselItem(
    deck: Deck,
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Fetch flashcards for the deck
    LaunchedEffect(Unit) {
        flashcardViewModel.getFlashcardsForDeck(deck.id)
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.DeckDetails.route + "/${deck.id}") },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Deck name and description
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            // Info Section with Labels
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                if (deck.subject != "Subject") {
                    // Subject label
                    InfoLabel(
                        text = stringResource(R.string.subject),
                        value = deck.subject
                    )
                    Spacer(Modifier.weight(1f))
                }
                // Difficulty label
                if (deck.averageDifficulty != null) {
                    InfoLabel(
                        text = stringResource(R.string.difficulty),
                        value = deck.averageDifficulty.toString().lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    )
                }
            }
        }
    }
}

/**
 * Composable function for the info label.
 *
 * @param text Label text.
 * @param value Value text.
 */
@Composable
fun InfoLabel(text: String, value: String) {
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Composable function for displaying a card with schedule and button.
 *
 * @param navController NavController for navigation.
 * @param schedules List of schedules for the week.
 */
@Composable
fun CardWithScheduleAndButton(
    navController: NavController,
    schedules: List<Schedule>
) {
    val sortedSchedules = schedules.sortedBy { it.startTime }
    val currentDayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

    // Filter schedules for the current day of the week
    val schedulesForCurrentDay = sortedSchedules.filter { it.dayOfWeek == currentDayOfWeek }

    val checkedState = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.WeekSchedule.route) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title for the schedule
            Text(
                text = "${currentDayOfWeek}'s Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (schedulesForCurrentDay.isNotEmpty()) {
                // Display each session in the schedule
                schedulesForCurrentDay.forEach { schedule ->
                    SessionCard(schedule, checkedState)
                }
            } else {
                // Prompt to create a new session
                CreateSchedulePrompt { navController.navigate(Screen.AddSchedule.route) }
            }
        }
    }
}

/**
 * Composable function for displaying a session card.
 *
 * @param schedule The schedule data for the session.
 * @param checkedState The state of the checkbox indicating whether the session is checked.
 */
@Composable
private fun SessionCard(
    schedule: Schedule,
    checkedState: MutableState<Boolean>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        // Schedule icon
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = stringResource(R.string.session)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            // Focus and description
            Text(
                text = "${schedule.focus}: ${schedule.description} ",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            // Start time and end time
            Text(
                text = "${schedule.startTime?.let { formatTime(it) }} - ${
                    schedule.endTime?.let {
                        formatTime(
                            it
                        )
                    }
                }",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        // Checkbox for session
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
    }
}

/**
 * Composable function for displaying a prompt to create a new schedule.
 *
 * @param onCreateScheduleClick Callback for clicking on creating a new revision session.
 */
@Composable
fun CreateSchedulePrompt(
    onCreateScheduleClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Prompt text
        Text(
            text = stringResource(R.string.noSessions),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to create a new session
        Button(
            onClick = onCreateScheduleClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.createSession))
        }
    }
}
