package uk.ac.aber.dcs.cs31620.revisionmaster.ui.util

import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Difficulty
import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck.Flashcard
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.logging.Logger
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Class responsible for spaced repetition logic for flashcards.
 */
class SpacedRepetition {

    /**
     * Calculates the next review date for a flashcard based on perceived difficulty.
     *
     * @param flashcard The flashcard for which to calculate the next review date.
     * @param perceivedDifficulty The perceived difficulty of reviewing the flashcard.
     * @return The updated flashcard with the next review date.
     * @throws IllegalArgumentException if the perceived difficulty is invalid.
     */
    fun calculateNextReview(flashcard: Flashcard, perceivedDifficulty: Difficulty): Flashcard {
        validateDifficultyInput(perceivedDifficulty)

        val easinessFactor = calculateEasinessFactor(flashcard.difficulty.weight, perceivedDifficulty.weight)
        val repetitions = calculateRepetitions(perceivedDifficulty, flashcard.repetition)
        val interval = calculateInterval(repetitions, easinessFactor)

        // Default to today if nextReview is empty or invalid
        val today = LocalDate.now()
        val nextReviewDate = flashcard.nextReview?.let {
            try {
                LocalDate.parse(it) // Try parsing existing value
            } catch (e: DateTimeParseException) {
                today // Use today if parsing fails
            }
        } ?: today // Use today if nextReview is null

        val newNextReviewDate = nextReviewDate.plusDays(interval.toLong())

        val updatedCard = flashcard.copy(
            repetition = repetitions,
            nextReview = newNextReviewDate.toString() // Ensure it's a string
        )
        log.info(updatedCard.toString())
        return updatedCard
    }

    /**
     * Checks if a flashcard is due for review based on its next review date.
     *
     * @param flashcard The flashcard to check.
     * @return true if the flashcard is due for review, false otherwise.
     */
    fun isDueForReview(flashcard: Flashcard): Boolean {
        if (flashcard.nextReview.isNullOrEmpty()) {
            // If nextReview is null or empty, the flashcard is not due for review
            return false
        }
        val now = LocalDate.now()
        val nextReviewDate = try {
            LocalDate.parse(flashcard.nextReview)
        } catch (e: DateTimeParseException) {
            // Handle parsing exception by returning false
            return false
        }
        return now >= nextReviewDate
    }


    /**
     * Validates the perceived difficulty input.
     *
     * @param difficulty The perceived difficulty of reviewing a flashcard.
     * @throws IllegalArgumentException if the provided difficulty value is invalid.
     */
    private fun validateDifficultyInput(difficulty: Difficulty) {
        log.info("Input difficulty: $difficulty")
        if (difficulty == Difficulty.NONE) {
            throw IllegalArgumentException("Provided difficulty value is invalid: NONE")
        }
    }

    /**
     * Calculates the easiness factor based on the previous and perceived difficulties.
     *
     * @param previousDifficulty The difficulty of the flashcard before the current review.
     * @param perceivedDifficulty The perceived difficulty of the current review.
     * @return The calculated easiness factor.
     */
    private fun calculateEasinessFactor(previousDifficulty: Double, perceivedDifficulty: Double): Double {
        // Adapting SM-2 algorithm to use difficulty weights instead of quality
        val quality = max(0.0, 5.0 - (perceivedDifficulty - previousDifficulty))
        // Scale to SM-2 quality (0-5)
        return max(1.3, previousDifficulty + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02))
    }

    /**
     * Calculates the number of repetitions based on the perceived difficulty and current repetitions.
     *
     * @param perceivedDifficulty The perceived difficulty of reviewing a flashcard.
     * @param cardRepetitions The number of repetitions the flashcard has undergone.
     * @return The updated number of repetitions.
     */
    private fun calculateRepetitions(perceivedDifficulty: Difficulty, cardRepetitions: Int): Int {
        // Reset repetitions if perceived difficulty is high
        return if (perceivedDifficulty == Difficulty.HARD) 0 else cardRepetitions + 1
    }

    /**
     * Calculates the review interval based on repetitions and easiness factor.
     *
     * @param repetitions The number of repetitions the flashcard has undergone.
     * @param easinessFactor The calculated easiness factor.
     * @return The review interval.
     */
    private fun calculateInterval(repetitions: Int, easinessFactor: Double): Int {
        return when {
            repetitions <= 1 -> 1
            repetitions == 2 -> 6
            else -> (6 * easinessFactor).roundToInt() // Base interval adjusted by easiness
        }
    }

    /**
     * Calculates the next review date based on the review interval.
     *
     * @param interval The review interval.
     * @return The next review date.
     */
    private fun calculateNextReviewDate(interval: Int): LocalDate {
        val now = LocalDate.now()
        return now.plusDays(interval.toLong())
    }

    /**
     * Logger for logging spaced repetition operations.
     */
    private companion object {
        private val log: Logger = Logger.getLogger(SpacedRepetition::class.java.name)
    }
}
