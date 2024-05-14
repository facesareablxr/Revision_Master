package uk.ac.aber.dcs.cs31620.revisionmaster.model.util

fun calculateMastery(correctAnswers: Int, totalQuestions: Int): Float {
    println("Correct Answers: $correctAnswers, Total Questions: $totalQuestions")

    if (totalQuestions == 0) {
        println("Total questions is 0, returning mastery as 0.")
        return 0f // Avoid division by zero
    }

    val percentageCorrect = (correctAnswers.toFloat() / totalQuestions) * 100
    println("Percentage Correct: $percentageCorrect")

    val mastery = when {        percentageCorrect >= 90f -> 100f  // Proficient or Mastery
        percentageCorrect >= 75f -> 75f + (percentageCorrect - 75f) * 0.5f // Scale within Proficient
        percentageCorrect >= 50f -> 50f + (percentageCorrect - 50f) * 0.5f // Scale within Intermediate
        else -> percentageCorrect * 0.5f   // Scale within Novice
    }.coerceIn(0f, 100f) // Ensure mastery stays within 0-100 range

    println("Final Mastery: $mastery")
    return mastery
}
