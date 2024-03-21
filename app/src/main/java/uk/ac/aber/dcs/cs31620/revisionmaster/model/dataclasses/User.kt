package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

import java.util.Date


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
