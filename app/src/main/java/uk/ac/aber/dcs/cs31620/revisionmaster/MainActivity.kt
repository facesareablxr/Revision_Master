package uk.ac.aber.dcs.cs31620.revisionmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.chats.ChatsScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.home.HomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.ForgotPassScreenTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.LoginTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.SignUpTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.WelcomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.ProfileScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.theme.RevisionMasterTheme

/**
 *
 */
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            RevisionMasterTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // User is authenticated, navigate to Home
                        BuildNavigationGraph(Screen.Home.route, UserViewModel())
                    } else {
                        // User is not authenticated, show WelcomeScreen
                        BuildNavigationGraph(Screen.Welcome.route, UserViewModel())
                    }
                }
            }
        }
    }
}

/**
 *
 */
@Composable
fun BuildNavigationGraph(destination: String, userViewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = destination
    ) {
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Login.route) { LoginTopLevel(navController) }
        composable(Screen.SignUp.route) { SignUpTopLevel(navController, userViewModel) }
        composable(Screen.ForgotDetails.route) { ForgotPassScreenTopLevel(navController)}
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Chats.route) { ChatsScreen(navController) }
        composable(Screen.Profile.route){ ProfileScreen(navController) }

    }
}
