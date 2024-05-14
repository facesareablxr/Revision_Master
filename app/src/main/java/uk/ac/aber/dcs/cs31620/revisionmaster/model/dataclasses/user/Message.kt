package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class for message, used in chat interface
 */
data class Message(
    val username: String,
    val message: String,
    val icon: ImageVector
)