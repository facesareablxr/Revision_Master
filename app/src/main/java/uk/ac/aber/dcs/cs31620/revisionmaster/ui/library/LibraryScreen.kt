package uk.ac.aber.dcs.cs31620.revisionmaster.ui.library

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen


/**
 * This composable function represents the Library screen.
 * @param navController: NavHostController to navigate between composables.
 * @param flashcardViewModel: ViewModel for managing flashcard data.
 * @param userViewModel: ViewModel for managing user data.
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    flashcardViewModel: FlashcardViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    // Collecting the state of decks from the ViewModel
    val decksState by flashcardViewModel.decks.collectAsState(initial = emptyList())

    // State for managing the search query
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    // Filtering decks based on the search query
    val filteredDecks = if (searchQuery.isEmpty()) decksState else {
        decksState.filter { deck ->
            deck.name.contains(searchQuery, ignoreCase = true) ||
                    deck.description.contains(searchQuery, ignoreCase = true) ||
                    deck.subject.contains(searchQuery, ignoreCase = true)
        }
    }

    // Fetching user data and user decks
    LaunchedEffect(Unit) {
        userViewModel.getUserData() // Fetch user data
    }

    flashcardViewModel.getUserDecks()

    // Building the UI using Scaffold
    Scaffold(
        topBar = {
            Column {
                // App bar with title and search bar
                NonMainTopAppBar(stringResource(R.string.library))
                SearchBar(searchQuery, setSearchQuery)
            }
        },
        bottomBar = { MainPageNavigationBar(navController) },
        floatingActionButton = {
            // Floating action button to add a new deck
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddDeck.route) }
            ) {
                Icon(imageVector = Icons.Default.Add, stringResource(id = R.string.addData))
            }
        }
    ) {
        // Displaying the deck list or no data message based on filteredDecks
        if (filteredDecks.isNotEmpty()) {
            DeckList(filteredDecks, navController)
        } else {
            NoDataMessage()
        }
    }
}


/**
 * This composable function represents a search bar.
 * @param searchQuery: Current search query.
 * @param onSearchQueryChanged: Callback function to update the search query.
 */
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}

/**
 * This composable function represents a list of decks.
 * @param decks: List of decks to display.
 * @param navController: NavHostController for navigation.
 */
@Composable
fun DeckList(
    decks: List<Deck>,
    navController: NavHostController
) {
    LazyColumn {
        items(decks) { deck ->
            DeckItem(deck, navController)
        }
    }
}

/**
 * @param deck: Deck object representing the data for a particular deck.
 * @param navController: NavHostController for navigating to deck details screen.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DeckItem(
    deck: Deck,
    navController: NavHostController
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
                /* While capitalise is depreciated, it works fine for this, displays the average difficulty
                of the deck */
                OutlinedButton(onClick = { }) {
                    Text(
                        text = deck.averageDifficulty.toString().lowercase().capitalize(),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
