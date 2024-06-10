package ru.edu.jobs.presentation.extensions

import ru.edu.jobs.domain.entity.Errors

fun errorToString(error: Errors): String {
    return when (error) {
        Errors.AccessDenied -> "Credentials isn't correct"
        is Errors.FieldsError -> "Fields format error"
        Errors.NotFound, Errors.Network -> "Network Error"

        Errors.ServiceUnavailable -> "Server is not respond"
        is Errors.ServerError -> error.message
        Errors.Unknown -> "Unknown Error"
    }
}