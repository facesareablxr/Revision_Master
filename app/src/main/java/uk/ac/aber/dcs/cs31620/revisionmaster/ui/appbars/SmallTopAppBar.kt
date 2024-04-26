package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R

/**
 * Composable for a small top app bar. It displays a title passed through its parameter and includes a back button.
 * @author Lauren Davis
 * @param navController is the NavController for navigation control
 * @param title is the title text to be displayed in the app bar
 */
@Composable
fun SmallTopAppBar(
    navController: NavController,
    title: String
) {
    // Building a small top app bar with a title and a back button
    TopAppBar(
        title = {
            Text(title)  // Display the passed title
        },
        navigationIcon = {
            // Back button to navigate back to the previous screen
            IconButton(
                onClick = {
                    navController.navigateUp() // Navigate back to the previous screen
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.goBack) // Description for the back button
                )
            }
        }
    )
}
