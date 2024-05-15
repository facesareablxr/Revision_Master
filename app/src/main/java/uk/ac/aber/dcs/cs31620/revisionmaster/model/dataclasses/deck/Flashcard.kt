package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck

import java.util.UUID

/**
 * Data class representing a flashcard.
 * @param id: Unique identifier for the flashcard, generated using UUID.
 * @param question: The question on the flashcard.
 * @param answer: The answer to the question on the flashcard.
 * @param difficulty: The difficulty level of the flashcard.
 * @param imageUri: The URI of an image associated with the flashcard (if any).
 * @param repetition: The number of times the flashcard has been reviewed.
 * @param nextReview: The date of the next review for the flashcard (if applicable).
 */
data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    val question: String = "",
    val answer: String = "",
    val difficulty: Difficulty = Difficulty.EASY,
    var imageUri: String? = null,
    val repetition: Int = 0,
    var nextReview: String? = null
)
