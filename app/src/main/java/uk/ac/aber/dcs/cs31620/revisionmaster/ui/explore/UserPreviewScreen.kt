package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.FlashcardViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.ProfilePicture
import java.util.Locale

/**
 * @param username Username of the user to preview
 * @param navController Navigation controller for navigation actions
 * @param userViewModel View model for user-related data
 * @param flashcardViewModel View model for flashcard-related data
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserPreviewScreen(
    username: String,
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    // Collect user and user ID from view models
    val user by userViewModel.userByUsername.observeAsState()
    val userId by userViewModel.userId.collectAsState()

    // Collect public decks from flashcard view model
    val publicDecks by flashcardViewModel.publicDecks.observeAsState()

    var isFollowing by remember { mutableStateOf(false) }
    // Collect followers and following lists from the view model
    val userFollows by userViewModel.userFollows.collectAsState()

    // Fetch necessary data when the composable launches
    LaunchedEffect(Unit) {
        userViewModel.getUserFollows() // Fetch user follows data
        userViewModel.getUserByUsername(username) // Fetch user data by username
        userViewModel.fetchUserIdByUsername(username) // Fetch user ID by username
        flashcardViewModel.fetchPublicDecks() // Fetch public decks
    }

    // Check if current user is following the previewed user
    isFollowing = userFollows?.following?.contains(username) == true


    Scaffold(
        topBar = { SmallTopAppBar(navController = navController, title = username) },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    user?.let { user ->
                        // Display profile picture
                        if (user.profilePictureUrl != null){
                            ProfilePicture(user.profilePictureUrl)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                ) {
                                    GlideImage(
                                        model = R.drawable.profile_image_placeholder,
                                        contentDescription = null,
                                        modifier = Modifier.wrapContentSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        // Display username and full name
                        Text(
                            text = username,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        user.institution?.let { institution ->
                            // Display the institution of the user
                            Institution(institution)
                        }
                    }
                    Button(
                        modifier = Modifier.size(112.dp, 48.dp), // Size of follow/unfollow button
                        onClick = {
                            isFollowing = if (isFollowing) {
                                userViewModel.unfollowUser(username) // Unfollow user
                                false
                            } else {
                                userViewModel.followUser(username) // Follow user
                                true
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(if (isFollowing) R.string.unfollow else R.string.follow) // Text based on follow/unfollow state
                        )
                    }
                }

                Divider(modifier = Modifier.padding(8.dp)) // Divider between sections
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    // Display the user's public decks
                    publicDecks?.let { decks ->
                        val userDecks = decks.filter { it.ownerId == userId }
                        PublicDecksColumn(decks = userDecks, navController = navController) // Display public decks
                    }
                }
            }
        }
    )
}

/**
 * Displays a column of public decks.
 *
 * @param decks List of public decks to display
 * @param navController Navigation controller for navigation actions
 */
@Composable
private fun PublicDecksColumn(decks: List<Deck>, navController: NavController) {
    Text(
        text = stringResource(R.string.publicDecks), // Title for public decks section
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(8.dp),
        textAlign = TextAlign.Start
    )
    if (decks.isNotEmpty()) {
        Column {
            decks.forEach { deck ->
                DeckCard(deck = deck, navController = navController) // Display each deck item
            }
        }
    } else {
        Text(
            text = stringResource(R.string.noDecks), // Text to display when there are no public decks
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        )
    }
}

/**
 * Displays the user's institution.
 *
 * @param institution The institution of the user
 */
@Composable
fun Institution(institution: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trailing icon
        Icon(Icons.Filled.School, contentDescription = "School")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            // Will automatically set to N/A if there isn't an institution set up
            text = institution,
            style = MaterialTheme.typography.bodyLarge
        )
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
    deck: Deck,
    navController: NavController
) {
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

