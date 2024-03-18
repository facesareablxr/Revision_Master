package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses


data class User(
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",

    var email: String = "",
    var password: String = "",
    val profilePictureUrl: String? = null,
    val institution: String? = null,

    val updatedUsername: String? = null,

    val following: Int = 0,
    val followers: Int = 0,

    val followerList: List<String>? = null,
    val followingList: List<String>? = null

)
