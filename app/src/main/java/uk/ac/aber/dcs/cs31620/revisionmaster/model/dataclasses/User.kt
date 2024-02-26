package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses


data class User(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val newPassword: String =""
)
