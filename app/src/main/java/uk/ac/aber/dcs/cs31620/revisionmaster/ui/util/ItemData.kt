package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This is just a data class to allow for better management of the item data
 */
data class ItemData(
    val icon: ImageVector,
    val label: String,
    val route: String
)