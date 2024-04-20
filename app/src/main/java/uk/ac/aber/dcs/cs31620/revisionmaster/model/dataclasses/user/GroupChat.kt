package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User

/**
 *
 */
data class GroupChat(
    val groupName : String = "",
    val groupMembers: List<User>? = null
)
