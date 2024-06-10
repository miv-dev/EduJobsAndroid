package ru.edu.jobs.domain.entity


sealed class LoadState<out T> {
    data object Loading: LoadState<Nothing>()
    data class Updating<T>(val data: T): LoadState<T>()
    data class Success<T>(val data: T): LoadState<T>()
    data class Error(val error: Errors) : LoadState<Nothing>()
}