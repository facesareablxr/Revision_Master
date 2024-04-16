package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

/**
 * Enum class for the difficulty, including its label and weighting
 */
enum class Difficulty(val label: String, val weight: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3)
}