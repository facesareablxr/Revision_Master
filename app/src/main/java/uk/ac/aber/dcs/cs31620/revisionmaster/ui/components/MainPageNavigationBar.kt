package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.screens
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.util.IconGroup

/**
 * This composable represents the main page navigation bar, offering options for home, schedule, and saved exercises.
 * The design is modeled after the FAA application, with adaptations for four navigation options, in replacement for
 * a navigation drawer.
 *
 * @param navController is the NavController for navigation
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun MainPageNavigationBar(navController: NavController) {
    // Map of icons for each screen
    val icons = mapOf(
        // Home group
        Screen.Home to IconGroup(
            filledIcon = Icons.Filled.Home,
            outlineIcon = Icons.Outlined.Home,
            label = stringResource(id = R.string.home)
        ),
        // Library group
        Screen.Library to IconGroup(
            filledIcon = Icons.Filled.Bookmark,
            outlineIcon = Icons.Outlined.BookmarkBorder,
            label = stringResource(id = R.string.library)
        ),
        // Explore group
        Screen.Explore to IconGroup(
            filledIcon = Icons.Filled.Search,
            outlineIcon = Icons.Outlined.Search,
            label = stringResource(id = R.string.explore)
        ),
        // Chats group
        Screen.Chats to IconGroup(
            filledIcon = Icons.Filled.ChatBubble,
            outlineIcon = Icons.Outlined.ChatBubbleOutline,
            label = stringResource(id = R.string.chats)
        )
    )

    // Navigation bar design and declaration
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.surface),
    ) {
        // Get the current navigation back stack entry and destination
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Iterate through each screen in the screens list
        screens.forEach { screen ->
            // Check if the current screen is selected
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val labelText = icons[screen]!!.label

            // Display navigation bar item with icon and label
            NavigationBarItem(
                icon = {
                    Icon(
                        // Display filled icon if selected, otherwise display outline icon
                        imageVector = (
                                if (isSelected)
                                    icons[screen]!!.filledIcon
                                else
                                    icons[screen]!!.outlineIcon),
                        contentDescription = labelText
                    )
                },
                label = { Text(labelText) },
                selected = isSelected,
                onClick = {
                    // Navigate to the selected screen
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}