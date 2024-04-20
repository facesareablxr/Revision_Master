package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

/**
 * Data class for message, used in chat interface
 */
data class Message(
    val content: String,
    val senderId: String,
    val timestamp: Long,
)