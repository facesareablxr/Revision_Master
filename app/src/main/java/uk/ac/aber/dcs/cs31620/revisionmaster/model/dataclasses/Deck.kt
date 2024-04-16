package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID

/**
 * Data class for the deck
 */
data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val subject: String = "",
    val description: String = "",
    val isPublic: Boolean = false,
    val ownerId: String = "",
    var cards: MutableList<Flashcard> = mutableListOf(),
    var averageDifficulty: Difficulty? = Difficulty.EASY,
    val mastery: Int = 0
)