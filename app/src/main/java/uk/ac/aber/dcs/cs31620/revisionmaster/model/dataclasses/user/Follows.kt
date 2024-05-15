package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

/**
 * Represents the relationship between a user and their followers and users they are following.
 * @param user The user who the followers and following lists are for.
 * @param followers List of usernames representing the followers of the user.
 * @param following List of usernames representing the users being followed by the user.
 */
data class Follows (
    val user: User? = null,
    val followers: List<String>? = null,
    val following: List<String>? = null
)
