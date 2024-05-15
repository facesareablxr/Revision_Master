package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Deck
import java.util.UUID

/**
 * Data class representing a schedule for studying.
 * @param id: Unique identifier for the schedule, generated using UUID.
 * @param dayOfWeek: The day of the week for the schedule.
 * @param startTime: The start time of the schedule (in milliseconds since epoch).
 * @param endTime: The end time of the schedule (in milliseconds since epoch).
 * @param focus: The focus of the study session.
 * @param description: Additional description for the schedule.
 * @param repeat: Boolean indicating whether the schedule repeats.
 * @param decks: List of decks associated with the schedule. Can be null or empty.
 */
data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val dayOfWeek: String = "",
    val startTime: Long? = null,
    val endTime: Long? = null,
    val focus: String = "",
    val description: String = "",
    val repeat: Boolean = false,
    var decks: List<Deck?> = emptyList()
)
