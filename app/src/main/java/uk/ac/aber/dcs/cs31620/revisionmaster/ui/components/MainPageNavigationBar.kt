package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.IconGroup
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.navigation.screens

/**
 * This composable represents the main page navigation bar, offering options for home, schedule, and saved exercises.
 * The design is modeled after the FAA application, with adaptations for five navigation options, in replacement for
 * a navigation drawer.
 *
 * @param navController is the NavController for navigation
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun MainPageNavigationBar(navController: NavController) {
    // Map of icons for each screen
    val icons = mapOf(
        Screen.Home to IconGroup(
            filledIcon = Icons.Filled.Home,
            outlineIcon = Icons.Outlined.Home,
            label = stringResource(id = R.string.home)
        ),
        Screen.Schedule to IconGroup(
            filledIcon = Icons.Filled.CalendarMonth,
            outlineIcon = Icons.Outlined.CalendarMonth,
            label = stringResource(id = R.string.schedule)
        ),
        Screen.Create to IconGroup(
            filledIcon = Icons.Filled.AddCircle,
            outlineIcon = Icons.Outlined.AddCircleOutline,
            label = stringResource(id = R.string.create)
        ),
        Screen.Chats to IconGroup(
            filledIcon = Icons.Filled.ChatBubble,
            outlineIcon = Icons.Outlined.ChatBubbleOutline,
            label = stringResource(id = R.string.chats)
        ),
        Screen.Library to IconGroup(
            filledIcon = Icons.Filled.Bookmark,
            outlineIcon = Icons.Outlined.BookmarkBorder,
            label = stringResource(id = R.string.library)
        )
    )

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Navigation bar design and declaration
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.surface),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Iterate through screens and create navigation items
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val labelText = icons[screen]!!.label

            // Adjusted onClick for the Create screen to show bottom overlay
            val onClickAction: () -> Unit = {
                if (screen == Screen.Create) {
                    showBottomSheet = true // Show the bottom sheet when Create is clicked
                } else {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            NavigationBarItem(
                // Manages the icon changes
                icon = {
                    Icon(
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
                onClick = onClickAction // Applying the adjusted onClick action
            )
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp) // Added vertical padding
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // Aligned vertically
                    ) {
                        Text(text = stringResource(R.string.create), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showBottomSheet = false }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.create))
                        }
                    }
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(4.dp)
                    )
                    OptionItem(Icons.Default.AutoAwesomeMotion, stringResource(R.string.flashcards))
                    OptionItem(Icons.Default.LibraryAdd, stringResource(R.string.studySet))
                    OptionItem(Icons.Default.Group, stringResource(R.string.group))

                    Spacer(modifier = Modifier.padding(12.dp))
                }
            },
            modifier = Modifier.background(Color.Transparent), // Adjust as needed
            scrimColor = Color(0x99000000), // Semi-transparent black color for scrim
            onDismissRequest = { showBottomSheet = false }
        )
    }
}

@Composable
fun OptionItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text)
    }
}