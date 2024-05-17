package uk.ac.aber.dcs.cs31620.revisionmaster.model.dataclasses.user

/**
 * This is the sealed response class, used only for the addition of users, as when I tried to
 * remove it - the whole thing broke.
 */
sealed class Response<out T> {
    // State for when an operation is in progress
    object Loading: Response<Nothing>()
    // Data class representing a successful operation
    data class Success<out T>(val data: T?): Response<T>()
    // Data class representing an unsuccessful operation
    data class Failure(val e: Exception): Response<Nothing>()
}