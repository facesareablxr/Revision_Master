package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.filters.SearchType
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.NonMainTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.components.MainPageNavigationBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.util.Locale

/**
 * Composable function for the Explore screen, which allows users to search for and explore
 * public decks and users.
 *
 * @param navController: NavController for navigation within the app.
 * @param userViewModel: ViewModel for user-related data.
 * @param flashcardViewModel: ViewModel for flashcard-related data.
 */
@Composable
fun ExploreScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel(),
) {
    // Get the current user's ID using Firebase Authentication
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Mutable state for search query and search type
    var searchType by remember { mutableStateOf(SearchType.USERS) }
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

    // Collect user and deck data as Composable states
    val usersState by userViewModel.users.collectAsState(emptyList())
    val decksState by flashcardViewModel.publicDecks.observeAsState(initial = null)

    // Fetch user data and public decks when the screen is launched
    LaunchedEffect(Unit) {
        userViewModel.getUserData()
        flashcardViewModel.fetchPublicDecks()
        userViewModel.getAllUsers()
    }

    // Filter decks and users based on search query
    val filteredDecks = if (searchQuery.isEmpty()) decksState else {
        decksState?.filter { deck ->
            deck.name.contains(searchQuery, ignoreCase = true) ||
                    deck.description.contains(searchQuery, ignoreCase = true) ||
                    deck.subject.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredUsers = if (searchQuery.isEmpty()) usersState else {
        usersState.filter { user ->
            (user.username.contains(searchQuery, ignoreCase = true) ||
                    user.institution?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }

    // Scaffold composable for the screen layout
    Scaffold(
        topBar = { NonMainTopAppBar(title = stringResource(id = R.string.explore)) },
        content = { innerPadding ->
            // Column layout for displaying content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search bar
                SearchBar(searchQuery = searchQuery, onSearchQueryChanged = setSearchQuery)

                // Filter chips for switching between users and decks
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

                // Display search results
                if (filteredDecks != null) {
                    SearchResultList(
                        searchType = searchType,
                        filteredDecks = filteredDecks,
                        filteredUsers = filteredUsers,
                        userViewModel = userViewModel,
                        navController = navController,
                        currentUserId = userId!!
                    )
                }
            }
        },
        bottomBar = { MainPageNavigationBar(navController = navController) }
    )
}

/**
 * Composable function for displaying search results in a LazyColumn.
 *
 * @param searchType: Type of search (users or decks).
 * @param filteredDecks: List of filtered decks.
 * @param filteredUsers: List of filtered users.
 * @param userViewModel: ViewModel for user-related data.
 * @param navController: NavController for navigation within the app.
 * @param currentUserId: ID of the current user.
 */
@Composable
fun SearchResultList(
    searchType: SearchType,
    filteredDecks: List<Deck>,
    filteredUsers: List<User>,
    userViewModel: UserViewModel,
    navController: NavController,
    currentUserId: String
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
                    navController = navController
                )

                SearchType.DECKS -> DeckCard(
                    deck = item as? Deck ?: return@items,
                    navController = navController,
                    currentUserId = currentUserId
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Composable function for displaying a deck card.
 *
 * @param currentUserId: ID of the current user.
 * @param deck: Deck object representing the data for a particular deck.
 * @param navController: NavController for navigating to deck details screen.
 */
@SuppressLint("DefaultLocale")
@Composable
fun DeckCard(
    currentUserId: String,
    deck: Deck,
    navController: NavController
) {
    // Display deck card only if the current user is not the owner of the deck
    if (deck.ownerId != currentUserId) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Display deck name
                        Text(
                            text = deck.name,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(bottom = 4.dp)
                        )
                        // Display deck description
                        Text(
                            text = deck.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    // Button for previewing the deck
                    Button(
                        onClick = { navController.navigate(Screen.PreviewDeck.route + "/${deck.id}") },
                        modifier = Modifier.size(100.dp, 48.dp)
                    ) {
                        Text(text = stringResource(R.string.previewDeck))
                    }
                }
                // Display deck subject and average difficulty if available
                Row(
                    modifier = Modifier.padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Display deck subject
                    OutlinedButton(onClick = {}) {
                        Text(
                            text = deck.subject,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    // Display average difficulty if available
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
}

/**
 * Composable function for displaying a user card.
 *
 * @param user: User object representing the data for a particular user.
 * @param userViewModel: ViewModel for user-related data.
 * @param navController: NavController for navigating to user details screen.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserCard(
    user: User,
    userViewModel: UserViewModel,
    navController: NavController
) {
    var isFollowing by remember { mutableStateOf(false) }

    // Collect user follows and current user data as Composable states
    val userFollows by userViewModel.userFollows.collectAsState()
    val currentUser by userViewModel.user.collectAsState()

    // Check if the current user is following the displayed user
    LaunchedEffect(userFollows) {
        userViewModel.getUserFollows()
        userViewModel.getUserData()
        isFollowing = userFollows?.following?.contains(user.username) == true
    }

    // Display user card only if the displayed user is not the current user
    if (currentUser!!.username != user.username) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { navController.navigate(Screen.PreviewUser.route + "/${user.username}") }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (modifier = Modifier.padding(4.dp)){
                    // Display user profile picture if available
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(64.dp)
                    ) {
                        if (user.profilePictureUrl != null) {
                            GlideImage(
                                model = Uri.parse(user.profilePictureUrl),
                                contentDescription = null,
                                modifier = Modifier.wrapContentSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            GlideImage(
                                model = R.drawable.profile_image_placeholder,
                                contentDescription = null,
                                modifier = Modifier.wrapContentSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                // Display user information
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f) // Added weight to expand this column
                ) {
                    // Display username
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Display user full name
                    Row {
                        Text(
                            text = user.firstName + " " + user.lastName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // Display user institution if available
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
                }
                // Display follow button
                Button(
                    modifier = Modifier.height(48.dp)
                        .padding(4.dp),
                    onClick = {
                        isFollowing = if (isFollowing) {
                            userViewModel.unfollowUser(user.username)
                            false
                        } else {
                            userViewModel.followUser(user.username)
                            true
                        }
                    }
                ) {
                    Text(
                        text = stringResource(if (isFollowing) R.string.unfollow else R.string.follow)
                    )
                }
            }
        }
    }
}

/**
 * Composable function for displaying the search bar.
 *
 * @param searchQuery: Current search query.
 * @param onSearchQueryChanged: Callback for handling changes to the search query.
 */
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    // Outlined text field for entering search queries
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        )
    )
}