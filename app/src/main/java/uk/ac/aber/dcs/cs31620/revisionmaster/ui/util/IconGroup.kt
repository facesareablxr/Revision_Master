package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This is used to map the icons to their navigation buttons in the Navigation bar
 */
data class IconGroup(
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector,
    val label: String
)
