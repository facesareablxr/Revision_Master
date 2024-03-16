package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses

data class Flashcard(
    val question: String?,
    val answer : String?,
    val subject: String?,
    val module: String?,
    val lecture: String?
){
    constructor(): this(null,null,null,null,null)
}
