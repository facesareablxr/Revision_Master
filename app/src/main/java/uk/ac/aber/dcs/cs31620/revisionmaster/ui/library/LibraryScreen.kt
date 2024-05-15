package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.filters.FilterType
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.util.Locale

/**
 * This composable function represents the Library screen.
 *
 * @param navController: NavController to navigate between composables.
 * @param flashcardViewModel: ViewModel for managing flashcard data.
 * @param userViewModel: ViewModel for managing user data.
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    // Collecting the decks from ViewModel
    val decks by flashcardViewModel.decks.collectAsState(initial = emptyList())
    // Mutable state for search query
    var searchQuery by remember { mutableStateOf("") }
    // Mutable state for selected filter option
    var selectedFilter by remember { mutableStateOf(FilterType.None) }
    // Mutable state for selected difficulty
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }

    // Fetching user data and user decks when this composable is launched
    LaunchedEffect(Unit) {
        userViewModel.getUserData()
        flashcardViewModel.getUserDecks()
    }

    // Filtering decks based on search query, filter option, and difficulty
    val filteredDecks = remember(decks, searchQuery, selectedFilter, selectedDifficulty) {
        derivedStateOf {
            decks.filter { deck ->
                val matchesSearchQuery = deck.subject.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    FilterType.Subject -> deck.subject.equals(searchQuery, ignoreCase = true)
                    FilterType.Difficulty -> deck.averageDifficulty == selectedDifficulty
                    FilterType.None -> true
                }
                matchesSearchQuery && matchesFilter
            }
        }
    }

    // Scaffold representing the overall layout structure
    Scaffold(
        topBar = { NonMainTopAppBar(title = stringResource(R.string.library)) },
        content = { innerPadding ->
            // Column layout for displaying schedules
            Column(
                modifier = Modifier.padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Search bar with filter options
                SearchBarWithFilter(
                    decks = decks,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = { searchQuery = it },
                    onFilterSelected = { selectedFilter = it },
                    onDifficultySelected = { selectedDifficulty = it }
                )
                // Displaying deck list or no data message based on filtered decks
                if (filteredDecks.value.isNotEmpty()) {
                    DeckList(filteredDecks.value, navController)
                } else {
                    NoDataMessage()
                }
            }
        },
        floatingActionButton = {
            // Floating action button to navigate to add deck screen
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddDeck.route) },
                content = { Icon(Icons.Default.Add, stringResource(id = R.string.addData)) }
            )
        },
        bottomBar = { MainPageNavigationBar(navController) },
    )
}

/**
 * This composable function represents the search bar and the filter options
 *
 * @param decks: List of decks to display.
 * @param searchQuery: Search query entered by the user.
 * @param onSearchQueryChanged: Callback to handle search query changes.
 * @param onFilterSelected: Callback to handle filter option selection.
 * @param onDifficultySelected: Callback to handle difficulty selection.
 */
@Composable
fun SearchBarWithFilter(
    decks: List<Deck>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (FilterType) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit
) {
    // Mutable state for dropdown menu expansion
    var expanded by remember { mutableStateOf(false) }

    // Extracting distinct subjects and difficulties from decks
    val subjects = decks.map { it.subject }.distinct()
    val difficulties = decks.mapNotNull { it.averageDifficulty }.distinct()

    // Row containing search bar and filter options
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = stringResource(id = R.string.search)) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            )
        )
        Spacer(modifier = Modifier.padding(4.dp))
        // Filter button with background
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        }
        Column {
            // Dropdown menu for filter options
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Clear filters option
                Text(
                    "Filter",
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.SemiBold
                )
                DropdownMenuItem(
                    onClick = {
                        onFilterSelected(FilterType.None)
                        onSearchQueryChanged("") // Clear the search query
                        onDifficultySelected(Difficulty.NONE) // Clear the selected difficulty
                        expanded = false
                    },
                    text = { Text(text = "Clear Filters", maxLines = 1) }
                )
                HorizontalDivider()
                // Subject filter options
                Text(
                    "Subject",
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.SemiBold
                )
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        onClick = {
                            onFilterSelected(FilterType.Subject)
                            onSearchQueryChanged(subject) // Update search query with selected subject
                            expanded = false
                        },
                        text = { Text(text = subject, maxLines = 1) }
                    )
                }
                HorizontalDivider()
                // Difficulty filter options
                Text(
                    "Difficulty",
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.SemiBold
                )
                difficulties.forEach { difficulty ->
                    DropdownMenuItem(
                        onClick = {
                            onDifficultySelected(
                                Difficulty.valueOf(
                                difficulty.toString()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }))
                            expanded = false
                        },
                        text = {
                            Text(
                                text = difficulty.toString().lowercase(Locale.getDefault())
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * This composable function represents a list of decks.
 *
 * @param decks: List of decks to display.
 * @param navController: NavController for navigation.
 */
@Composable
fun DeckList(
    decks: List<Deck>,
    navController: NavController
) {
    LazyColumn {
        items(decks) { deck ->
            DeckItem(deck, navController)
        }
    }
}

/**
 * This composable represents the individual decks in a card.
 *
 * @param deck: Deck object representing the data for a particular deck.
 * @param navController: NavController for navigating to deck details screen.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DeckItem(
    deck: Deck,
    navController: NavController
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                navController.navigate(Screen.DeckDetails.route + "/${deck.id}")
            },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Displays the deck name
            Text(
                text = deck.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Displays the deck description
            Text(
                text = deck.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Displays the subject assigned to the deck
                OutlinedButton(onClick = {}) {
                    Text(
                        text = deck.subject,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                if (deck.averageDifficulty != null) {
                    OutlinedButton(onClick = { }) {
                        Text(
                            text = deck.averageDifficulty.toString().lowercase()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

/**
 * Composable function to display a message when there is no data available.
 */
@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Surface to display the no data message in the box
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            // No data message
            Text(
                text = stringResource(id = R.string.addData),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}