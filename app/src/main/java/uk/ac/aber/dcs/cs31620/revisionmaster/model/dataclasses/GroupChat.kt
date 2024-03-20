package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

data class GroupChat(
    val groupName : String = "",
    val groupMembers: List<User>? = null
)
