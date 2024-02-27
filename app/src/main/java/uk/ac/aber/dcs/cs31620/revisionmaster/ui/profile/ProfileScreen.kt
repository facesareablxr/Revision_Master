package uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.SmallTopAppBar
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

@Composable
fun ProfileScreen(navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser != null) {
        // Display profile content for logged-in user
        ProfileContent(currentUser, onSignOut = {
            // Sign out and navigate back to welcome screen
            auth.signOut()
            navController.navigate(Screen.Welcome.route)
        },
            navController)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ProfileContent(user: FirebaseUser, onSignOut: () -> Unit, navController: NavHostController) {
    Scaffold(
        modifier = Modifier.padding(horizontal = 16.dp),
        topBar = {
            SmallTopAppBar(
                title = stringResource(R.string.profile), navController = navController
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello ${user.displayName}")
            // other profile content will go here!

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.sign_out))
            }
        }
    }
}