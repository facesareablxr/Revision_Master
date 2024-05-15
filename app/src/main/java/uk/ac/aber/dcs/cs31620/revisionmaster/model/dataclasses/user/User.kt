package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

/**
 * Data class representing a user.
 * @param username: The username of the user.
 * @param firstName: The first name of the user.
 * @param lastName: The last name of the user.
 * @param email: The email address of the user.
 * @param password: The password of the user. This is nullable for security reasons.
 * @param profilePictureUrl: The URL of the user's profile picture.
 * @param institution: The institution associated with the user.
 * @param updatedUsername: The updated username, if the user has changed it.
 * @param lastLoginDate: The date of the user's last login.
 * @param currentStreak: The current streak of the user.
 */
data class User(
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String? = "",
    var profilePictureUrl: String? = null,
    val institution: String? = null,
    val updatedUsername: String? = null,
    var lastLoginDate: String? = null,
    var currentStreak: Int = 0
)
