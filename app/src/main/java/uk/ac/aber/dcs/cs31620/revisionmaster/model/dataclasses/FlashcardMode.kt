package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

enum class FlashcardMode(val label: String) {
    VIEW("View"),
    TEST_SELF("Test Self"),
    MATCH_GAME("Match Game"),
    FILL_IN_BLANKS("Fill in the Blanks")
}