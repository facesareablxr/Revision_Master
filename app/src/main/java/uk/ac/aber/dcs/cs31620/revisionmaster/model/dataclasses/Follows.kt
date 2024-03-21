package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

data class Follows(
    var user: String = "",

    val followerList: List<String>? = null,
    val followingList: List<String>? = null,

)
