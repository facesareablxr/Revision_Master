package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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


@Composable
fun ConfirmationAppBar(navController: NavController, title: String) {
    var showDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.goBack)
                )
            }
        }
    )

    if (showDialog) {
        ConfirmationDialog(
            onKeepGoing = { showDialog = false },
            onQuit = {
                navController.navigateUp()
                showDialog = false
            }
        )
    }
}

@Composable
fun ConfirmationDialog(onKeepGoing: () -> Unit, onQuit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onKeepGoing,
        title = {
            Text(text = "Are you sure you want to quit?")
        },
        text = {
            Text("Your progress will not be saved.")
        },
        confirmButton = {
            TextButton(onKeepGoing) {
                Text("Keep Going")
            }
        },
        dismissButton = {
            TextButton(onQuit) {
                Text("Quit")
            }
        }
    )
}
