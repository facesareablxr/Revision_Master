package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.Deck
import java.util.UUID

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
