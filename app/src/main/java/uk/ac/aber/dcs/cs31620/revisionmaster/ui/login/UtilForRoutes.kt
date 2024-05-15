package uk.ac.aber.dcs.cs31620.revisionmaster.ui.login

import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen

/**
 * Navigates to the home screen, clearing the back stack up to the home screen.
 *
 * @param navController Reference to the navigation controller.
 */
fun goToHome(
    navController: NavController
) {
    navController.navigate(Screen.Home.route) {
        // This block is executed when the navigation happens
        popUpTo(Screen.Home.route) // Clear the back stack up to the home screen
    }
}