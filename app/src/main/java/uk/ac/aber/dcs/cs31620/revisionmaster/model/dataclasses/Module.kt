package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.UUID

data class Module(
    val moduleId:  String = UUID.randomUUID().toString(),
    val moduleName: String,
    val subjectName: String,
    val ownerName: String
)
