package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck

/**
 * Enum class representing the difficulty levels of flashcards.
 * @param label: The label or name of the difficulty level.
 * @param weight: The weighting factor assigned to the difficulty level.
 */
enum class Difficulty(val label: String, val weight: Double) {
    NONE ("None", 0.0),
    EASY("Easy", 1.0),
    MEDIUM("Medium", 2.0),
    HARD("Hard", 3.0)
}
