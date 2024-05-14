package uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation

/**
 * This is the list of screens in the program and the routes that NavController should take
 */
sealed class Screen(val route: String) {
    // Main login routes
    object Welcome : Screen ("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotDetails : Screen ("forgot")

    // Home screen and Nav Bar screens
    object Home : Screen("home")
    object Library : Screen("library")
    object Explore : Screen("explore")
    object Chats : Screen("chats")

    // Profile routes
    object Profile : Screen("profile")
    object EditProfile : Screen("editProfile")

    // Add/Edit deck screens
    object AddDeck : Screen("addDeck")
    object EditDeck : Screen("editDeck")

    // Deck detail screen
    object DeckDetails : Screen("deckDetails")

    // Add/Edit flashcard screens
    object AddFlashcards : Screen("addFlashcards")
    object EditFlashcards : Screen("editFlashcards")
    object Exam : Screen("exam")

    // Revision screens
    object ViewFlashcards : Screen("viewFlashcards")
    object TestYourself : Screen("testYourself")
    object Summary : Screen("summary")
    object TestResults : Screen("testResults")

    // Schedule screens
    object AddSchedule: Screen("addSchedule")
    object WeekSchedule: Screen("weekSchedule")
    object DaySchedule: Screen("daySchedule")
    object EditSchedule: Screen("editSchedule")
}

/**
 * This is the list of screens, this is used in the navigation bar at the bottom of the page
 */
val screens = listOf(
    Screen.Home,
    Screen.WeekSchedule,
    Screen.Library,
    Screen.Explore

)