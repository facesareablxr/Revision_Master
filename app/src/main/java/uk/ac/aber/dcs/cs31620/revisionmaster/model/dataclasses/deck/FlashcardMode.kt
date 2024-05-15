package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.deck

/**
 * Enum class representing different modes for interacting with flashcards.
 * @param label: The label or name of the flashcard mode.
 */
enum class FlashcardMode(val label: String) {
    VIEW("View"),           // Mode for viewing flashcards
    TEST_SELF("Test Self"), // Mode for testing oneself with flashcards
    REVIEW("Review")        // Mode for reviewing flashcards
}
