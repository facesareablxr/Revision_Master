package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel

/**
 * This is a non-main page top app bar. It has a title which can be customized.
 * This top app bar is simpler compared to the main page top app bar and doesn't include
 * additional components like streak counter, notification bell, or profile circle.
 *
 *  @author Lauren Davis [lad48]
 */

/**
 * Composable function for displaying the top app bar for non-main screens.
 * @param title: Title to be displayed in the app bar.
 * @param scrollBehavior: Scroll behavior for the top app bar.
 * @param viewModel: View model for user data.
 */
@Composable
fun NonMainTopAppBar(
    title: String, // Title to be displayed in the app bar
    scrollBehavior: TopAppBarScrollBehavior? = null, // Scroll behavior for the top app bar
    viewModel: UserViewModel = viewModel() // View model for user data
) {
    val context = LocalContext.current // Accessing the current context

    // Observing user data from the view model
    val user by viewModel.user.collectAsState(initial = null)

    // Fetching user data when the composable is first launched
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    if (user != null) {
        // Building the top app bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title displayed in the center
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                }
            },
            // Setting colors for the app bar
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            // Setting scroll behavior for the app bar
            scrollBehavior = scrollBehavior
        )
    }
}
