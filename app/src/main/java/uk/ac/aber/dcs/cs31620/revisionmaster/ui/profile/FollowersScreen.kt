package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

/**
 * @Composable for the followers screen
 * @param navController The navigation controller
 * @param userViewModel The view model for managing user data
 */
@Composable
fun FollowersPage(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // Collect followers and following lists from the view model
    val userFollows by userViewModel.userFollows.collectAsState()
    // Launch an effect to fetch user follows data when userFollows changes
    LaunchedEffect(userFollows) {
        userViewModel.getUserFollows()
    }
    // Set up the scaffold with top app bar and content
    Scaffold(
        topBar = {
            // Display a small top app bar with title "Followers"
            SmallTopAppBar(
                title = stringResource(R.string.followers),
                navController = navController
            )
        },
        content = { innerPadding ->
            // Retrieve followers list from the collected state
            val followersList = userFollows?.followers
            // Check if followers list is null or empty
            if (followersList.isNullOrEmpty()) {
                // Display a message if followers list is empty
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.noFollowers),
                        textAlign = TextAlign.Center
                    )
                    // https://www.flaticon.com/free-icons/lonely title="lonely icons" Lonely icons created by smalllikeart - Flaticon
                    val imagePainter = rememberAsyncImagePainter(R.drawable.lonely)
                    Image(
                        painter = imagePainter,
                        contentDescription = stringResource(R.string.noFollowers),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Display followers using LazyColumn if list is not empty
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Iterate through each follower in the followers list
                    items(followersList) { follower ->
                        // Fetch user data for the follower
                        userViewModel.getUserByUsername(follower)
                        // Observe changes to user data
                        val user by userViewModel.userByUsername.observeAsState()
                        // If user data is available, display user card
                        user?.let { user ->
                            FollowerCard(user = user, userViewModel = userViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    )
}

/**
 * Composable function for displaying a card representing a follower user.
 *
 * @param user: User object containing user data.
 * @param userViewModel: UserViewModel for managing user data.
 * @param navController: NavController for navigation within the app.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FollowerCard(
    user: User,
    userViewModel: UserViewModel,
    navController: NavController
) {
    // Mutable state for tracking whether the user is being followed
    var isFollowing by remember { mutableStateOf(false) }

    // Launch effect to check if the user is being followed
    LaunchedEffect(Unit) {
        isFollowing = userViewModel.userFollows.value?.following?.contains(user.username) == true
    }

    // Card to display user information and follow/unfollow button
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {navController.navigate(Screen.PreviewUser.route + "/${user.username}")}
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display user profile picture
            user.profilePictureUrl?.let { url ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(64.dp)
                        .padding(4.dp)
                ) {
                    GlideImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // Display user information
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyMedium
                )
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
            // Spacer for layout
            Spacer(modifier = Modifier.height(8.dp))
            // Follow/Unfollow button
            Button(
                modifier = Modifier.size(112.dp, 48.dp),
                onClick = {
                    isFollowing = if (isFollowing) {
                        // Unfollow user if already following
                        userViewModel.unfollowUser(user.username)
                        false
                    } else {
                        // Follow user if not already following
                        userViewModel.followUser(user.username)
                        true
                    }
                }
            ) {
                // Text on the follow/unfollow button
                Text(
                    text = stringResource(if (isFollowing) R.string.unfollow else R.string.follow)
                )
            }
        }
    }
}
