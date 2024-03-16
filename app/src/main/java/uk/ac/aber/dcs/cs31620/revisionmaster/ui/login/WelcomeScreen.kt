package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

/**
 *
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Image cropped to fit the screen
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = stringResource(R.string.welcome),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        WelcomeContent(navController)
    }
}

/**
 * This is all of the content for the welcome screen including the text and buttons
 */
@Composable
fun WelcomeContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text in black, overriding the theme of the application for readability
        CompositionLocalProvider(LocalContentColor provides Color.Black) {
            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Welcome text in black, overriding the theme of the application for readability
        CompositionLocalProvider(LocalContentColor provides Color.Black) {
            Text(
                text = stringResource(R.string.welcomeSubtitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Buttons stacked vertically
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button for sign-in, will take you into the login route via LoginScreen.kt
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.signIn))
            }
            // Button for sign-up, will take you into the sign-up route via SignUpScreen.kt
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