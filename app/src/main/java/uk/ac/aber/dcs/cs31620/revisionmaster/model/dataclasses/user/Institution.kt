package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user.User

/**
 * Data class for institution, going to be used later on for searching for users
 */
data class Institution(
    val institution: String = "",
    val students: List<User>? = null
)
