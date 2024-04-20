package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

data class TestResult(
    val correct: Int,
    val incorrect: Int,
    val elapsedTime: Long
)