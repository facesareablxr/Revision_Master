package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

/**
 * Enum class for the difficulty, including its label and weighting
 */
enum class Difficulty(val label: String, val weight: Double) {
    EASY("Easy", 1.0),
    MEDIUM("Medium", 2.0),
    HARD("Hard", 3.0)
}