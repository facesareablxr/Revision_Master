package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

import java.util.Date

/**
 * This is the user class, potentially separated at a later date, but unsure.
 */
data class User(
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",

    var email: String = "",
    var password: String? = "",

    val profilePictureUrl: String? = null,

    val institution: String? = null,

    val updatedUsername: String? = null,

    var lastLoginDate: Date? = null,
    var currentStreak: Int = 0

)
