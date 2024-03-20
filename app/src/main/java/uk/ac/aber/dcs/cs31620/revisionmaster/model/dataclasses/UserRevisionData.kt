package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

data class UserRevisionData(
    var username: String = "",
    val subjects: List<Subject>? = emptyList(),
    val modules: List<Module>? = emptyList(),
    val classes: List<UserClasses>? = emptyList(),
    val flashcards: List<Flashcard>? = emptyList()
)
