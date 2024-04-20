package uk.ac.aber.dcs.cs31620.revisionmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.chats.ChatsScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore.ExploreScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.home.HomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.AddDeckScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.AddFlashcardScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.DeckDetailsScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.EditDeckScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.EditFlashcardScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.LibraryScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.ForgotPassScreenTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.LoginTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.SignUpTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.WelcomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.EditProfileTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.ProfileScreenTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.FlashcardSelfTestScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.FlashcardViewerTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.MatchingGame
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.SummaryScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.theme.RevisionMasterTheme

/**
 *
 */

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val viewModel: UserViewModel by viewModels()

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
                        val user by viewModel.user.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.getUserData()
                        }
                        user?.let { viewModel.updateStreakIfNeeded(user!!) }
                        BuildNavigationGraph(Screen.Home.route, viewModel)

                        LaunchedEffect(Unit) {
                            viewModel.getUserData()
                        }

                        BuildNavigationGraph(Screen.Home.route, viewModel)
                    } else {

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
        composable(Screen.ForgotDetails.route) { ForgotPassScreenTopLevel(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Chats.route) { ChatsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreenTopLevel(navController) }
        composable(Screen.EditProfile.route) { EditProfileTopLevel(navController) }
        composable(Screen.Library.route) { LibraryScreen(navController) }
        composable(Screen.AddDeck.route) { AddDeckScreen(navController) }
        composable(Screen.DeckDetails.route + "/{deckId}") { backStackEntry ->
            DeckDetailsScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }
        composable(Screen.AddFlashcards.route + "/{deckId}") { backStackEntry ->
            AddFlashcardScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }
        composable(Screen.Explore.route) { ExploreScreen(navController) }

        composable(Screen.ViewFlashcards.route + "/{deckId}") { backStackEntry ->
            FlashcardViewerTopLevel(navController, backStackEntry.arguments?.getString("deckId")!!)
        }

        composable(Screen.TestYourself.route + "/{deckId}") { backStackEntry ->
            FlashcardSelfTestScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }

        composable(Screen.MatchGame.route + "/{deckId}") { backStackEntry ->
            MatchingGame(navController, backStackEntry.arguments?.getString("deckId")!!)
        }

        composable(Screen.FillInBlanks.route + "/{deckId}") { backStackEntry ->
            FlashcardViewerTopLevel(navController, backStackEntry.arguments?.getString("deckId")!!)
        }

        composable(Screen.EditDeck.route + "/{deckId}") { backStackEntry ->
            EditDeckScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }
        composable(Screen.EditFlashcards.route + "/{flashcardId}" + "/{deckId}") { backStackEntry ->
            EditFlashcardScreen(
                navController,
                backStackEntry.arguments?.getString("flashcardId")!!,
                backStackEntry.arguments?.getString("deckId")!!
            )
        }
        composable(Screen.Summary.route + "/{correctMatches}" + "/{incorrectMatches}" + "/{deckId}") { backstackEntry ->
            SummaryScreen(
                backstackEntry.arguments?.getInt("correctMatches")!!,
                backstackEntry.arguments?.getInt("incorrectMatches")!!,
                backstackEntry.arguments?.getString("deckId")!!, navController
            )
        }
    }
}



