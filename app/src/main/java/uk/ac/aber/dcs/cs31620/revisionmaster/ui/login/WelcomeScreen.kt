package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text in the center
        Text(
            text = "Welcome to the App!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(8.dp)
        )

        // Buttons stacked vertically
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
            ) {
                Text(text = "Sign In")
            }
            Button(
                onClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}