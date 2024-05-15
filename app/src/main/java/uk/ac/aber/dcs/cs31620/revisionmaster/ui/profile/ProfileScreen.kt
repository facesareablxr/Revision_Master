package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.ProfilePicture

/**
 * ProfileScreen composable function.
 *
 * @param navController The NavController to handle navigation within the app.
 * @param userViewModel The UserViewModel to manage user data.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // Collect user data from the view model
    val user by userViewModel.user.collectAsState(initial = null)

    // Fetch user data when the composable is launched
    LaunchedEffect(Unit) {
        userViewModel.getUserData()
    }

    // Scaffold for the profile screen UI
    Scaffold(
        topBar = {
            // Top app bar with navigation icon and actions
            TopAppBar(
                title = {},
                navigationIcon = {
                    // Navigation icon to navigate back
                    IconButton(onClick = {
                        navController.popBackStack()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.goBack))
                    }
                },
                actions = {
                    // Action button to navigate to edit profile screen
                    IconButton(onClick = { navController.navigate(Screen.EditProfile.route) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.editProfile))
                    }
                }
            )
        }
    ) { innerPadding ->
        // Display user profile content
        if (user == null) {
            // Show loading indicator if user data is not yet available
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Display profile content when user data is available
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                ProfileContent(user!!, userViewModel, navController)
            }
        }
    }
}

/**
 * ProfileContent composable function.
 *
 * @param user The user object containing user data.
 * @param userViewModel The user view model instance.
 * @param navController The NavController to handle navigation within the app.
 */
@Composable
fun ProfileContent(user: User, userViewModel: UserViewModel, navController: NavController) {
    // State for showing delete confirmation dialog
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Collect followers and following lists from the view model
    val userFollows by userViewModel.userFollows.collectAsState()
    LaunchedEffect(userFollows) {
        userViewModel.getUserFollows()
    }

    // Column to display user profile information
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display profile picture
            val profilePictureUrl = user.profilePictureUrl
            ProfilePicture(profilePictureUrl)
            Spacer(modifier = Modifier.width(16.dp))
            // Display username and full name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Display email
        EmailDisplay(user)
        Spacer(modifier = Modifier.height(8.dp))
        // Display institution
        if (user.institution != null) {
            InstitutionDisplay(user)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Display following and followers count
        val followers = userFollows?.followers ?: emptyList()
        val following = userFollows?.following ?: emptyList()
        FollowingFollowersCount(
            followers = followers,
            following = following,
            onFollowingClick = { navController.navigate(Screen.Following.route) },
            onFollowersClick = { navController.navigate(Screen.Followers.route) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Column for other options like Logout and Delete Profile
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Divider()
            // Logout option
            ListItem(
                text = stringResource(R.string.logout),
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.logout)
                    )
                },
                onClick = {
                    userViewModel.signOut()
                    navController.navigate(Screen.Welcome.route)
                }
            )
            Divider()
            // Delete profile option
            ListItem(
                text = stringResource(R.string.deleteProfile),
                icon = { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete)) },
                onClick = { showDeleteConfirmation = true }
            )
            Divider()
        }
        // Delete confirmation dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { stringResource(R.string.deleteTitle) },
                text = { stringResource(R.string.deleteConfirm) },
                confirmButton = {
                    Button(
                        onClick = {
                            userViewModel.deleteProfile()
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

/**
 * ListItem composable function.
 *
 * @param text The text to display in the list item.
 * @param icon The icon to display on the right side of the list item.
 * @param onClick The function to call when the list item is clicked.
 */
@Composable
private fun ListItem(text: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            icon()
        }
    }
}

/**
 * FollowingFollowersCount composable function.
 *
 * @param followers The list of followers.
 * @param following The list of users the user is following.
 * @param onFollowingClick The callback function when the following box is clicked.
 * @param onFollowersClick The callback function when the followers box is clicked.
 */
@Composable
private fun FollowingFollowersCount(
    followers: List<String>,
    following: List<String>,
    onFollowingClick: () -> Unit,
    onFollowersClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ClickableCountBox(
            text = stringResource(R.string.following),
            count = following.size,
            onClick = onFollowingClick
        )
        ClickableCountBox(
            text = stringResource(R.string.followers),
            count = followers.size,
            onClick = onFollowersClick
        )
    }
}

/**
 * ClickableCountBox composable function.
 *
 * @param text The text to display.
 * @param count The count to display.
 * @param onClick The callback function when the box is clicked.
 */
@Composable
private fun ClickableCountBox(text: String, count: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .size(width = 100.dp, height = 50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = count.toString(), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * InstitutionDisplay composable function.
 *
 * @param user The user object containing the institution information.
 */
@Composable
fun InstitutionDisplay(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for institution
        Icon(Icons.Filled.School, contentDescription = "School")
        Spacer(modifier = Modifier.width(8.dp))
        // Display institution name
        Text(
            // Set to N/A if there isn't an institution set up
            text = user.institution!!,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * EmailDisplay composable function.
 *
 * @param user The user object containing the email address.
 */
@Composable
private fun EmailDisplay(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for email
        Icon(Icons.Filled.Email, contentDescription = "Email")
        Spacer(modifier = Modifier.width(8.dp))
        // Display email address
        Text(text = user.email, style = MaterialTheme.typography.bodyLarge)
    }
}
