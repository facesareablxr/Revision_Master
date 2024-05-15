package uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
 * Composable for a small top app bar with a menu. It displays a title passed through its parameter,
 * a back button, and a menu with additional actions.
 *
 * @param title is the title text to be displayed in the app bar
 * @param navController is the NavController for navigation control
 * @param onEditDeckClick is a lambda function to be invoked when the "Edit Deck" option is clicked
 * @param onDeleteClick is a lambda function to be invoked when the "Delete Deck" option is clicked
 */
@Composable
fun SmallTopAppBarWithMenu(
    title: String,
    navController: NavController,
    onEditDeckClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) } // State for controlling menu visibility

    // Building a small top app bar with title, back button, and menu
    TopAppBar(
        title = { Text(title) }, // Displaying the passed title
        navigationIcon = {
            // Back button to navigate back to the previous screen
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.goBack)
                )
            }
        },
        actions = {
            // Menu icon to show additional options
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.moreOptions))
            }
            // Dropdown menu with additional actions
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                // Menu item for editing deck
                DropdownMenuItem(onClick = {
                    showMenu = false
                    onEditDeckClick()
                }) {
                   stringResource(R.string.editDeck)
                }
                // Menu item for deleting deck
                DropdownMenuItem(onClick = {
                    showMenu = false
                    onDeleteClick()
                }) {
                    stringResource(R.string.deleteDeck)
                }
            }
        }
    )
}