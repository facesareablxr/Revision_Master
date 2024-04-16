package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

/**
 * This is the dataclass for the exam, which is made up of a subject, difficulty, and exam type
 */
data class Exam(
    val subject: String,
    val difficulty: Difficulty,
    val examType: ExamType
)