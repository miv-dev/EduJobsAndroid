package ru.edu.jobs.domain.entity

sealed interface Errors {

    data class FieldsError(val fields: Map<String, String>) : Errors
    data class ServerError(val message: String) : Errors
    data object Network : Errors
    data object NotFound : Errors
    data object AccessDenied : Errors
    data object ServiceUnavailable : Errors
    data object Unknown : Errors


}

