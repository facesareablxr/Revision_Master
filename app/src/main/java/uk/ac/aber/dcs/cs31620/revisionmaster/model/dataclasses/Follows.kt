package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

/**
 * This is the data class to build the following and followers list
 */
data class Follows(
    var userID: String = "",
    val followerList: List<String>? = null,
    val followingList: List<String>? = null,
)
