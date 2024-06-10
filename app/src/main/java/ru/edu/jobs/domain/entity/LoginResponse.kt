package ru.edu.jobs.domain.entity


sealed interface Response<out T> {
    data class Error(val error: Errors) : Response<Nothing>

    data class Success<T>(val data: T) : Response<T>
}
