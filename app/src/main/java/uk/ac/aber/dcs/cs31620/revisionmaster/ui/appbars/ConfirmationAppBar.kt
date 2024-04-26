package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import uk.ac.aber.dcs.cs31620.revisionmaster.R

/**
 * Composable function for displaying an app bar with a confirmation dialog trigger.
 * @param navController: NavController used for navigating back.
 * @param title: Title displayed in the app bar.
 */
@Composable
fun ConfirmationAppBar(
    navController: NavController,
    title: String
) {
    // State to manage the visibility of the confirmation dialog
    var showDialog by remember { mutableStateOf(false) }

    // Display the top app bar
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            // Display a navigation icon which triggers the confirmation dialog when clicked
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.goBack)
                )
            }
        }
    )

    // Show the confirmation dialog if showDialog is true
    if (showDialog) {
        ConfirmationDialog(
            onKeepGoing = { showDialog = false }, // Callback when "Keep Going" button is clicked
            onQuit = {
                navController.navigateUp() // Navigate up when "Quit" button is clicked
                showDialog = false // Dismiss the dialog
            }
        )
    }
}

/**
 * Composable function for displaying a confirmation dialog.
 * @param onKeepGoing: Callback function invoked when "Keep Going" button is clicked.
 * @param onQuit: Callback function invoked when "Quit" button is clicked.
 */
@Composable
fun ConfirmationDialog(onKeepGoing: () -> Unit, onQuit: () -> Unit) {
    // Display the AlertDialog
    AlertDialog(
        onDismissRequest = onKeepGoing, // Callback invoked when dialog is dismissed
        title = {
            Text(text = "Are you sure you want to quit?") // Title of the dialog
        },
        text = {
            Text("Your progress will not be saved.") // Message displayed in the dialog
        },
        confirmButton = {
            // "Keep Going" button
            TextButton(onKeepGoing) {
                Text("Keep Going")
            }
        },
        dismissButton = {
            // "Quit" button
            TextButton(onQuit) {
                Text("Quit")
            }
        }
    )
}
