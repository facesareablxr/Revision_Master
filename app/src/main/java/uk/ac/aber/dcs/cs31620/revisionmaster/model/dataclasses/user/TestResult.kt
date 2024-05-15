package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import java.util.UUID

/**
 * Data class representing the result of a test taken on a deck.
 * @param testId: Unique identifier for the test result, generated using UUID.
 * @param deckId: Identifier for the deck on which the test was taken.
 * @param correct: Number of correctly answered questions in the test.
 * @param incorrect: Number of incorrectly answered questions in the test.
 * @param elapsedTime: Time elapsed during the test (in milliseconds).
 * @param date: Date when the test was taken, represented as a string.
 */
data class TestResult(
    val testId: String = UUID.randomUUID().toString(),
    val deckId: String = "",
    val correct: Int = 0,
    val incorrect: Int = 0,
    val elapsedTime: Long = 0L,
    val date: String = ""
)
