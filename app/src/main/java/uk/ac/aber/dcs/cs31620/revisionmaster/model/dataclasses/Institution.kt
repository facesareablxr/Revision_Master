package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

data class Institution(
    val institution: String = "",
    val students: List<User>? = null
)
