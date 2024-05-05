package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.SearchType
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.SearchBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExploreScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    var searchType by remember { mutableStateOf(SearchType.USERS) }
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    // Collect all decks and users
    val decksState by flashcardViewModel.publicDecks.collectAsState(initial = emptyList())
    val usersState by userViewModel.users.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        flashcardViewModel.getAllPublicDecks()
        userViewModel.getAllUsers()
    }

    // Filter decks and users based on search query
    val filteredDecks = if (searchQuery.isEmpty()) decksState else {
        decksState.filter { deck ->
            deck.name.contains(searchQuery, ignoreCase = true) ||
                    deck.description.contains(searchQuery, ignoreCase = true) ||
                    deck.subject.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredUsers = if (searchQuery.isEmpty()) usersState else {
        usersState.filter { user ->
            user.username.contains(searchQuery, ignoreCase = true) ||
                    user.institution?.contains(searchQuery, ignoreCase = true) ?: false
        }
    }

    Scaffold(
        topBar = { NonMainTopAppBar(title = stringResource(id = R.string.explore)) },
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.padding(32.dp))
                // Search bar
                SearchBar(searchQuery = searchQuery, onSearchQueryChanged = setSearchQuery)

                // Filter chips
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)) {
                    FilterChip(
                        label = { Text(stringResource(R.string.users)) },
                        selected = searchType == SearchType.USERS,
                        onClick = { searchType = SearchType.USERS }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        label = { Text(stringResource(R.string.decks)) },
                        selected = searchType == SearchType.DECKS,
                        onClick = { searchType = SearchType.DECKS }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display search result
                SearchResultList(
                    searchType = searchType,
                    filteredDecks = filteredDecks,
                    filteredUsers = filteredUsers
                )
            }
        },
        bottomBar = { MainPageNavigationBar(navController = navController) }
    )
}

@Composable
fun SearchResultList(
    searchType: SearchType,
    filteredDecks: List<Deck>,
    filteredUsers: List<User>
) {
    LazyColumn {
        items(when (searchType) {
            SearchType.USERS -> filteredUsers
            SearchType.DECKS -> filteredDecks
        }) { item ->
            when (searchType) {
                SearchType.USERS -> UserCard(user = item as? User ?: return@items)
                SearchType.DECKS -> DeckCard(deck = item as? Deck ?: return@items)
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {}
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.username, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun DeckCard(deck: Deck) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Deck: ${deck.name}", style = MaterialTheme.typography.headlineMedium)
        }
    }
}