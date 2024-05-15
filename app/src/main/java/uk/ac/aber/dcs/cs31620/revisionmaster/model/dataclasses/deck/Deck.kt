package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck

import java.util.UUID

/**
 * Data class representing a deck of flashcards.
 * @param id: Unique identifier for the deck, generated using UUID.
 * @param name: The name of the deck.
 * @param subject: The subject or topic of the deck.
 * @param description: Description of the deck.
 * @param public: Boolean indicating whether the deck is public or private.
 * @param ownerId: Identifier of the user who owns the deck.
 * @param averageDifficulty: Average difficulty level of the flashcards in the deck.
 * @param mastery: Mastery level of the deck (e.g., percentage of mastered flashcards).
 */
data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val subject: String = "",
    val description: String = "",
    val public: Boolean = false,
    val ownerId: String = "",
    var averageDifficulty: Difficulty? = null,
    var mastery: Int? = null
)
