package uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation

/**
 * This is the list of screens in the program and the routes that NavController should take
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Create: Screen("create")
    object Chats : Screen("chats")
    object Library : Screen("library")
    object Welcome : Screen ("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotDetails : Screen ("forgot")
    object Profile : Screen("profile")
    object EditProfile : Screen("editProfile")
}

/**
 * This is the list of screens, this is used in the navigation bar at the bottom of the page
 */
val screens = listOf(
    Screen.Home,
    Screen.Schedule,
    Screen.Create,
    Screen.Chats,
    Screen.Library
)