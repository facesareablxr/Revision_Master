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
    val interval: Int? = 0,
    val reviewDate: Long? = 0,
    val correct: Int? = 0,
    val incorrect: Int? = 0

)