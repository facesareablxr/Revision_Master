package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun ExploreScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel(),
) {
    val currentUser by userViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUserData()
    }

    var searchType by remember { mutableStateOf(SearchType.USERS) }
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    // Collect all decks and users
    val decksState by flashcardViewModel.publicDecks.collectAsState(initial = emptyList())
    val usersState by userViewModel.users.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        flashcardViewModel.getAllPublicDecks()
        userViewModel.getAllUsers()
    }

    val followingList by userViewModel.following.collectAsState()

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
            user.username != currentUser?.username && !followingList.contains(user.username) &&
                    (user.username.contains(searchQuery, ignoreCase = true) ||
                            user.institution?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    Scaffold(
        topBar = { NonMainTopAppBar(title = stringResource(id = R.string.explore)) },
        content = { innerPadding ->
            // Column layout for displaying schedules
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SearchBar(searchQuery = searchQuery, onSearchQueryChanged = setSearchQuery)

                // Filter chips
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                ) {
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

                SearchResultList(
                    searchType = searchType,
                    filteredDecks = filteredDecks,
                    filteredUsers = filteredUsers,
                    userViewModel = userViewModel,
                    flashcardViewModel = flashcardViewModel,
                    currentUserId = currentUser?.username ?: "",
                    followingList = followingList
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
    filteredUsers: List<User>,
    userViewModel: UserViewModel,
    flashcardViewModel: FlashcardViewModel,
    currentUserId: String,
    followingList: List<String>
) {
    LazyColumn {
        items(
            when (searchType) {
                SearchType.USERS -> filteredUsers
                SearchType.DECKS -> filteredDecks
            }
        ) { item ->
            when (searchType) {
                SearchType.USERS -> UserCard(
                    user = item as? User ?: return@items,
                    userViewModel = userViewModel,
                    currentUserId = currentUserId,
                    followingList = followingList
                )
                SearchType.DECKS -> DeckCard(deck = item as? Deck ?: return@items)
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    userViewModel: UserViewModel,
    currentUserId: String,
    followingList: List<String>
) {
    // Display the user card if it's not the current user and not followed
    if (user.username != currentUserId && !followingList.contains(user.username)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {}
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Display username
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                // Display first name
                Text(
                    text = user.firstName,
                    style = MaterialTheme.typography.bodyMedium
                )
                // Display institution if available
                user.institution?.let { institution ->
                    Row {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = stringResource(R.string.institution)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = institution,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Button to follow/unfollow user
                Button(
                    onClick = {
                        if (followingList.contains(user.username)) {
                            userViewModel.unfollowUser(user.username)
                        } else {
                            userViewModel.followUser(user.username)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(if (followingList.contains(user.username)) R.string.unfollow else R.string.follow)
                    )
                }
            }
        }
    }
}

@Composable
fun DeckCard(
    deck: Deck,
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Deck: ${deck.name}", style = MaterialTheme.typography.headlineMedium)
            Row {
                if (deck.subject != "Subject") {
                    Text(text = "Subject: ${deck.subject}")
                }
                if (deck.averageDifficulty != null) {
                    Text(text = "Difficulty: ${deck.averageDifficulty}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { flashcardViewModel.downloadDeck(deck.id) }) {
                Text(text = "Download")
            }
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
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}
