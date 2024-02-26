package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.R

/**
 * Composable for a small top app bar. It displays a title passed through its parameter and includes a back button.
 *
 * @param navController is the NavController for navigation control
 * @param title is the title text to be displayed in the app bar
 */
@Composable
fun SmallTopAppBar(navController: NavController, title: String) {
    TopAppBar(
        title = {
            Text(title)  // Display the passed title
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp() // Navigate back to the previous screen
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.goBack) // Description for the back button
                )
            }
        }
    )
}

@Composable
private fun HandleBackButton(navController: NavHostController) {
    // When back button is pressed we will navigate up the Compose
    // hierarchy. navigateUp will pop the Compose navigation back stack automatically.
    val backCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                navController.navigateUp()
            }
        }

    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }

}