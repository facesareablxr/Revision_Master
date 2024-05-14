package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import java.util.UUID

data class TestResult(
    val testId: String = UUID.randomUUID().toString(),
    val deckId: String = "",
    val correct: Int = 0,
    val incorrect: Int = 0,
    val elapsedTime: Long = 0L,
    val date: String = ""
)