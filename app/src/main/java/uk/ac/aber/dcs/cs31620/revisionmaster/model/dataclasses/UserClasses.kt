package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID

data class UserClasses(
    val classId: String = UUID.randomUUID().toString(), // Unique identifier for the class
    val className: String,
    val moduleId: String,
    val ownerName: String
)
