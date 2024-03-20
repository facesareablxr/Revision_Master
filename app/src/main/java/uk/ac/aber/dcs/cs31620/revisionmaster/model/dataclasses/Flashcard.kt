package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID


data class Flashcard(
    val flashcardId: String = UUID.randomUUID().toString(), // Automatically generate a unique ID
    val frontText: String,
    val backText: String,
    val classId: String?,
    val moduleId: String?,
    val subjectName: String?,
    val ownerName: String
)