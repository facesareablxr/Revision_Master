package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import java.time.LocalTime

/**
 * Composable function for displaying the top app bar of the main page.
 * @param navController: Navigation controller for handling navigation.
 * @param scrollBehavior: Scroll behavior for the top app bar.
 * @param userViewModel: View model for user data.
 */
@Composable
fun MainPageTopAppBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    userViewModel: UserViewModel = viewModel()
) {
    val currentTime = LocalTime.now() // Getting the current time
    val greeting = getGreeting(currentTime) // Getting the appropriate greeting based on the time of day

    // Observing user data from the view model
    val user by userViewModel.user.collectAsState()
    val currentStreak = user?.currentStreak

    // Fetching user data when the composable is first launched
    LaunchedEffect(Unit) {
        userViewModel.getUserData()
    }

    if (user != null) {
        // Building the top app bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Greeting on the left side of the app bar
                    Text(
                        text = greeting,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Showing streak counter if available
                        if (currentStreak != null) {
                            StreakCounter(currentStreak)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // Showing profile circle
                        ProfileCircle(navController = navController, user!!)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            },
            // Setting colors for the app bar
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            // Setting scroll behavior for the app bar
            scrollBehavior = scrollBehavior
        )
    }
}

/**
 * Composable function for displaying a notification bell icon with badge.
 * @param notifications: The count of unread notifications.
 */
@Composable
private fun NotificationsBell(notifications: Int) {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Filled.Notifications, // Placeholder icon
            contentDescription = "Notifications"
        )
        // Showing badge if there are notifications
        if (notifications > 0) {
            Badge(modifier = Modifier.offset(x = 8.dp, y = (-8).dp)) // Position badge on top right
        }
    }
}

/**
 * Composable function for displaying a streak counter.
 * @param steakCounter: The count of the user's streak.
 */
@Composable
private fun StreakCounter(steakCounter: Int) {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = "Streak"
        )
    }
    // Displaying the streak count
    Text(
        text = "$steakCounter",
        fontWeight = FontWeight.SemiBold
    )
}

/**
 * Composable function for getting the greeting based on the current time of day.
 * @param currentTime: The current time.
 * @return The appropriate greeting.
 */
@Composable
private fun getGreeting(currentTime: LocalTime): String {
    return when (currentTime) {
        in LocalTime.of(0, 0)..LocalTime.of(12, 0) -> stringResource(R.string.morning)
        in LocalTime.of(12, 0)..LocalTime.of(18, 0) -> stringResource(R.string.afternoon)
        else -> stringResource(R.string.evening)
    }
}

/**
 * Composable function for displaying the profile circle, which triggers navigation to the profile screen on click.
 * @param navController: Navigation controller for handling navigation.
 * @param user: User object containing user information.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileCircle(
    navController: NavController,
    user: User
) {
    val defaultImageRes = R.drawable.profile_image_placeholder // Default profile image resource

    // Creating a clickable circular profile image
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(8.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigate(Screen.Profile.route) // Navigating to profile screen on click
            },
        contentAlignment = Alignment.Center
    ) {
        if (user.profilePictureUrl != null) {
            // Display placeholder image or loaded image
                GlideImage(
                    model = Uri.parse(user.profilePictureUrl),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
            GlideImage(
                model = defaultImageRes,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
