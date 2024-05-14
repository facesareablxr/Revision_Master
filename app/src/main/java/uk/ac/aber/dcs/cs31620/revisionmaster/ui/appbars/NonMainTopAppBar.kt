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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Composable function for displaying the top app bar for non-main screens.
 * @param title: Title to be displayed in the app bar.
 * @param scrollBehavior: Scroll behavior for the top app bar.
 */
@Composable
fun NonMainTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
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

