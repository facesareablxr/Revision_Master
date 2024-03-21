package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen


/**
 * This is the profile screen where the user will be able to see all of their information, from here
 * they will be able to either edit their details or go back to the home screen.
 * @author Lauren Davis
 */

/**
 * The top-level composable for the profile screen.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@Composable
fun ProfileScreenTopLevel(navController: NavController) {
    ProfileScreen(navController)
}

/**
 * The main composable for the profile screen.
 *
 * @param onBackClick The function to call when the back button is clicked.
 * @param navigator The navigation controller used to navigate between screens.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navigator: NavController,
    viewModel: UserViewModel = viewModel() // Get ViewModel instance
) {
    val user by viewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navigator.navigate(Screen.EditProfile.route) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (user == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileContent(user!!, viewModel)
            }
        }
    }
}

/**
 * This composable displays the user's profile information.
 *
 * @param user The user object containing user data.
 * @param userViewModel The user view model instance.
 */
@Composable
fun ProfileContent(user: User, userViewModel: UserViewModel) {

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
                Text(text = user.username, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Display the email
        EmailDisplay(user)
        Spacer(modifier = Modifier.height(8.dp))
        // Display the institution of the user
        InstitutionDisplay(user)
        Spacer(modifier = Modifier.height(8.dp))
        // Display the following and follower count for the user
        FollowingFollowersCount(user)
        Spacer(modifier = Modifier.height(8.dp))

        // Column for the display of other options such as Settings and Login, more may be added if needed
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Divider()
            // List items for Settings and Logout
            ListItem(
                text = "Settings",
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                onClick = { /* Handle settings click */ }
            )
            Divider()
            ListItem(
                text = "Logout",
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                }, // Use ExitToApp icon for logout
                onClick = { userViewModel.signOut() }
            )
            Divider()
        }
    }
}

/**
 * Creates a clickable list item with text and an icon.
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
 * Displays the user's following and followers count.
 *
 * @param user The user object containing the following and followers count.
 */
@Composable
private fun FollowingFollowersCount(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CountBox(text = "Following", count = 0)
        CountBox(text = "Followers", count = 0)
    }
}

/**
 * Creates a box with rounded corners that displays a count.
 *
 * @param text The text to display above the count.
 * @param count The numerical count to display.
 */
@Composable
private fun CountBox(text: String, count: Int) {
    Surface(
        modifier = Modifier
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
 * Displays the user's institution.
 *
 * @param user The user object containing the institution information.
 */
@Composable
private fun InstitutionDisplay(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trailing icon
        Icon(Icons.Filled.School, contentDescription = "School")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            // Will automatically set to N/A if there isn't an institution set up
            text = user.institution ?: "",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Displays the user's email address.
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
        // Trailing icon
        Icon(Icons.Filled.Email, contentDescription = "Email")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = user.email, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePicture(
    imagePath: String?,
) {
    val defaultImageRes = R.drawable.profile_image_placeholder

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = if (!imagePath.isNullOrEmpty()) Uri.parse(imagePath) else defaultImageRes,
            contentDescription = stringResource(R.string.profilePicture),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}