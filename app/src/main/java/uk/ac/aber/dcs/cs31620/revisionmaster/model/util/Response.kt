package uk.ac.aber.dcs.cs31620.revisionmaster.model.util

sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(val data: T?): Response<T>()

    data class Failure(val e: Exception): Response<Nothing>()
}