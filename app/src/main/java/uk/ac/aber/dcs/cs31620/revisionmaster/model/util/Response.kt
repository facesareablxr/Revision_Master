package uk.ac.aber.dcs.cs31620.revisionmaster.model.util

/**
 * This is the sealed response class, used in the interactions with the database
 */
sealed class Response<out T> {
    // State for when an operation is in progress
    object Loading: Response<Nothing>()
    // Data class representing a successful operation
    data class Success<out T>(val data: T?): Response<T>()
    // Data class representing an unsuccessful operation
    data class Failure(val e: Exception): Response<Nothing>()
}