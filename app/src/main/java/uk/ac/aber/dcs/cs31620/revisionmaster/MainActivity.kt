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
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore.DeckPreviewScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore.ExploreScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore.UserPreviewScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.home.HomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.AddDeckScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.AddFlashcardScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.DeckDetailsScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.EditDeckScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.EditFlashcardScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.LibraryScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.library.TestResultsScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.ForgotPassScreenTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.LoginTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.SignUpTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.login.WelcomeScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.EditProfileScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.FollowersPage
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.FollowingScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.profile.ProfileScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.FlashcardSelfTestScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.FlashcardViewerTopLevel
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.ReviewScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.revision.SummaryScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule.AddScheduleScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule.DayScheduleScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule.EditScheduleScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.schedule.ScheduleScreen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.theme.RevisionMasterTheme

/**
 * MainActivity class responsible for initializing the app and setting up the navigation.
 */
class MainActivity : ComponentActivity() {

    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth

    // UserViewModel instance using by viewModels() delegate
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Set the content of the activity
        setContent {
            // Apply the app theme
            RevisionMasterTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Check if user is logged in
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // Collect user data from ViewModel
                        val user by userViewModel.user.collectAsState()

                        // Fetch user data when the composable is first launched
                        LaunchedEffect(Unit) {
                            userViewModel.getUserData()
                            userViewModel.updateStreakIfNeeded()
                        }

                        // Build navigation graph for authenticated user
                        BuildNavigationGraph(Screen.Home.route, userViewModel)

                        // Build navigation graph for authenticated user
                        BuildNavigationGraph(Screen.Home.route, userViewModel)
                    } else {
                        // Build navigation graph for unauthenticated user
                        BuildNavigationGraph(Screen.Welcome.route, userViewModel)
                    }
                }
            }
        }
    }
}

/**
 * Composable function responsible for building the navigation graph.
 * @param destination: Starting destination of the navigation graph.
 * @param userViewModel: UserViewModel instance.
 */
@Composable
fun BuildNavigationGraph(destination: String, userViewModel: UserViewModel) {
    // Navigation controller instance
    val navController = rememberNavController()
    // Define the navigation graph
    NavHost(
        navController = navController,
        startDestination = destination
    ) {
        // Define each composable screen and its route
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Login.route) { LoginTopLevel(navController) }
        composable(Screen.SignUp.route) { SignUpTopLevel(navController, userViewModel) }
        composable(Screen.ForgotDetails.route) { ForgotPassScreenTopLevel(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
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
        composable(Screen.Summary.route + "/{correctMatches}/{incorrectMatches}/{elapsedSeconds}/{deckId}") { backstackEntry ->
            val correctMatches =
                backstackEntry.arguments?.getString("correctMatches")?.toIntOrNull() ?: 0
            val incorrectMatches =
                backstackEntry.arguments?.getString("incorrectMatches")?.toIntOrNull() ?: 0
            val elapsed =
                backstackEntry.arguments?.getString("elapsedSeconds")?.toLongOrNull() ?: 0L
            val deckId = backstackEntry.arguments?.getString("deckId") ?: ""

            SummaryScreen(correctMatches, incorrectMatches, elapsed, deckId, navController)
        }
        composable(Screen.TestResults.route + "/{deckId}") { backStackEntry ->
            TestResultsScreen(backStackEntry.arguments?.getString("deckId")!!, navController)
        }
        composable(Screen.Review.route + "/{deckId}") { backStackEntry ->
            ReviewScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }
        composable(Screen.PreviewDeck.route + "/{deckId}") { backStackEntry ->
            DeckPreviewScreen(navController, backStackEntry.arguments?.getString("deckId")!!)
        }
        composable(Screen.AddSchedule.route) { AddScheduleScreen(navController) }
        composable(Screen.WeekSchedule.route) { ScheduleScreen(navController) }
        composable(Screen.DaySchedule.route + "/{day}") { backstackEntry ->
            DayScheduleScreen(
                backstackEntry.arguments?.getString("day")!!,
                navController = navController
            )
        }
        composable(Screen.EditSchedule.route + "/{scheduleId}") { backstackEntry ->
            EditScheduleScreen(
                navController = navController,
                scheduleId = backstackEntry.arguments?.getString("scheduleId")!!
            )
        }
        composable(Screen.Followers.route ) { FollowersPage(navController = navController)}
        composable(Screen.Following.route){ FollowingScreen( navController = navController)}
        composable(Screen.PreviewUser.route + "/{username}" ){ backstackEntry ->
            UserPreviewScreen(
                navController = navController,
                username = backstackEntry.arguments?.getString("username")!!
            )
        }
    }
}
