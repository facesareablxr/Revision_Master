package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Image cropped to fit the screen
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Welcome Screen Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        WelcomeContent(navController)
    }
}

@Composable
fun WelcomeContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text in the center, larger size
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )

        // Subtitle "to revision master"
        Text(
            text = stringResource(R.string.welcomeSubtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Buttons stacked vertically with different colors
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.signIn))
            }
            Button(
                onClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.signup))
            }
        }
    }
}