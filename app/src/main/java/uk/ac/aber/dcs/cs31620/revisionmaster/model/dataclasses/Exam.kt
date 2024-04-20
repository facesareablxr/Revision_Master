package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

/**
 * This is the dataclass for the exam, which is made up of a subject, difficulty, and exam type
 */
sealed class ExamQuestion {
    data class MultipleChoice(val question: String, val options: List<String>, val correctAnswer: String) : ExamQuestion()
    data class FillInTheBlank(val question: String, val correctAnswer: String) : ExamQuestion()
}