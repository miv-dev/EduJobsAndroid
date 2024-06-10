package ru.edu.jobs.data.local

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable
import ru.edu.jobs.domain.entity.Errors
import javax.inject.Inject

class ErrorService @Inject constructor() {

    fun getError(exception: Throwable): Errors {
        return when (exception) {
            is java.net.UnknownHostException -> Errors.Network
            is java.net.SocketTimeoutException -> Errors.Network
            is java.net.ConnectException -> Errors.Network
            is java.net.ProtocolException -> Errors.Network
            is java.net.MalformedURLException -> Errors.Network
            is java.net.URISyntaxException -> Errors.Network
            else -> {
                Errors.Unknown
            }
        }
    }

    suspend fun getError(httpResponse: HttpResponse): Errors {
        return try {
            val error = httpResponse.body<GeneralError>()
            Errors.ServerError(error.detail)
        } catch (e: Exception) {
            return try {
                httpResponse.body<String>().let {
                    Errors.FieldsError(parseFieldsError(it))
                }
            } catch (e: Exception) {
                Errors.Unknown
            }
        }
    }

}

@Serializable
data class GeneralError(val detail: String)

fun parseFieldsError(error: String): Map<String, String> {
    val fields = error.substringAfter("{").substringBefore("}").split(",")
    val map = mutableMapOf<String, String>()
    fields.forEach {
        val key = it.substringBefore(":").replace("\"", "").trim()
        val value = it.substringAfter(":").replace("\"", "").trim()

        map[key] = value.replace("[", "").replace("]", "")
    }
    return map
}