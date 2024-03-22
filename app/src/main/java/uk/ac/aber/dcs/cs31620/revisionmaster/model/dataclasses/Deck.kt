package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID

data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String ="",
    val subject: String = "",
    val description: String = "",
    val isPublic: Boolean = false,
    val owner: String = "",
    val cards: List<Flashcard> = emptyList()
)