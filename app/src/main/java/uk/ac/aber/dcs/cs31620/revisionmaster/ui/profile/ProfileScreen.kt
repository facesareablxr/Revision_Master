package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.User
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

@Composable
fun ProfileScreenTopLevel(navController: NavController) {
    ProfileScreen(onBackClick = { Screen.Home.route }, navController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    navigator: NavController // Pass navigator as a parameter
) {
    val viewModel: UserViewModel = viewModel()
    val user by viewModel.user.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
@Composable
fun ProfileContent(user: User, userViewModel: UserViewModel) {

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePicture(user.profilePictureUrl)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        EmailDisplay(user)
        Spacer(modifier = Modifier.height(8.dp))
        InstitutionDisplay(user)
        Spacer(modifier = Modifier.height(8.dp))
        FollowingFollowersCount(user)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(onClick = { /* Handle settings click */ }) {
                Text("Settings")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {userViewModel.signOut() }) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun FollowingFollowersCount(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CountBox(text = "Following", count = user.following)
        CountBox(text = "Followers", count = user.followers)
    }
}

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
            Text(text = text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = count.toString(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
private fun InstitutionDisplay(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.School, contentDescription = "School")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.institution ?: "N/A",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmailDisplay(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Email, contentDescription = "Email")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ProfilePicture(profilePictureUrl: String?) {
    val defaultProfilePicture = painterResource(id = R.drawable.profile_image_placeholder)
    val imageModifier = Modifier
        .size(120.dp)
        .clip(CircleShape)

    Image(
        painter =  defaultProfilePicture,
        contentDescription = "Profile Picture",
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
}