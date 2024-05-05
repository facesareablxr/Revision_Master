package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

/**
 * Data class for message, used in chat interface
 */
data class Message(
    val senderId: String,
    val recipientId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)