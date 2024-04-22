package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID

/**
 * Dataclass for a flashcard
 */
data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    val question: String = "",
    val answer: String = "",
    // This is for the spaced repetition
    val difficulty: Difficulty = Difficulty.EASY,
    val imageUri: String? = null,
    val correct: Int = 0,
    val repetitions: Int = 0
) {
    val mastery: Float
        get() = if (repetitions == 0) 0f else correct / repetitions.toFloat()
}