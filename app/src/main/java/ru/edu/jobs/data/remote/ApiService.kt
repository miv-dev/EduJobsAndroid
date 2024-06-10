package ru.edu.jobs.data.remote

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import ru.edu.jobs.data.dto_model.ParsedServiceDto
import ru.edu.jobs.data.dto_model.ServiceDto
import ru.edu.jobs.data.dto_model.UserDto
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.domain.entity.Welcome
import javax.inject.Inject


sealed interface ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>
    data class UnhandledException(val exception: Throwable) : ApiResponse<Nothing>
    data class ApiException(val response: HttpResponse) : ApiResponse<Nothing>
}

class ApiService @Inject constructor(
    private val authHttpClient: AuthHttpClient,
    private val context: Context
) {

    private val httpClient = authHttpClient.httpClient

    suspend fun login(username: String, password: String): HttpResponse {
        val response = httpClient.post(ApiEndpoints.LOGIN) {
            setBody(hashMapOf("username" to username, "password" to password))
        }
        Result
        return response
    }

    suspend fun updateUser(user: UserDto): HttpResponse {

        val formData = formData {
            append("username", user.username)
            append("email", user.email)
            append("department.id", user.department?.id?.toString() ?: "-1")
            append("department.description", user.department?.description.toString())
            append("department.website", user.department?.website.toString())
            append("department.name", user.department?.name.toString())
            user.userType?.let {
                append("user_type", it)
            }


            user.profile?.id?.let {
                if (it != -1) {

                    append("profile.id", it)
                }
            }
            append("profile.first_name", user.profile?.firstName ?: "")
            append("profile.last_name", user.profile?.lastName ?: "")
            append("profile.phone_number", user.profile?.phoneNumber ?: "")
            user.profile?.avatar?.let { uriString ->
                val uri = Uri.parse(uriString)

                if (uriString.isNotEmpty() && !uriString.startsWith("http")) {
                    context.contentResolver.openInputStream(uri)?.let {
                        val fileExtension = getFileExtension(context, uri)
                        append("profile.avatar", it.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/$fileExtension")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"avatar.${fileExtension}\""
                            )
                        })
                    }


                }
            }
        }
        val response = httpClient.submitFormWithBinaryData(
            url = ApiEndpoints.getUpdateUserUrl(user.id),

            formData
        ) {
            method = HttpMethod.Patch
        }
        return response
    }

    suspend fun getParsedService(): ApiResponse<List<ParsedServiceDto>> {
        return try {
            val response = httpClient.get(ApiEndpoints.PARSED_SERVICES)
            return if (response.isSuccessful()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.ApiException(response)
            }
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }
    }

    suspend fun getCurrentUser(): UserDto {
        val response = httpClient.get(ApiEndpoints.CURRENT_USER)

        return response.body()
    }

    suspend fun loadUniversities(): ApiResponse<List<Department>> {
        return try {
            val response = httpClient.get(ApiEndpoints.DEPARTMENTS)
            if (response.isSuccessful()) {
                return ApiResponse.Success(response.body())
            }
            ApiResponse.ApiException(response)
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }
    }


    suspend fun getServices(): List<ServiceDto> {
        val response = httpClient.get(ApiEndpoints.SERVICES)

        return response.body()
    }


    suspend fun viewService(serviceId: Int) {
        httpClient.post(ApiEndpoints.ADD_VIEWED_SERVICES) {
            setBody(hashMapOf("id" to serviceId))
        }
    }

    suspend fun searchServices(query: String): List<ServiceDto> {
        val response = httpClient.get(ApiEndpoints.getSearchUrl(query))

        return response.body()
    }

    suspend fun getViewedServices(): List<ServiceDto> {
        val response = httpClient.get(ApiEndpoints.VIEWED_SERVICES)

        return response.body()
    }

    suspend fun getUserServices(): List<ServiceDto> {
        val response = httpClient.get(ApiEndpoints.USER_SERVICES)

        return response.body()
    }

    suspend fun register(
        username: String,
        email: String,
        type: String,
        password: String,
        passwordConfirmation: String
    ): HttpResponse {
        val response = httpClient.post(ApiEndpoints.REGISTER) {
            setBody(
                hashMapOf(
                    "username" to username,
                    "email" to email,
                    "user_type" to type,
                    "password" to password,
                    "password2" to passwordConfirmation
                )
            )
        }

        return response.body()
    }

    suspend fun getService(id: Int): ServiceDto {
        val response = httpClient.get(ApiEndpoints.getServiceUrl(id))

        return response.body()
    }


    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    suspend fun updateService(service: ServiceDto): ApiResponse<ServiceDto> {
        return try {
            val response = httpClient.request(ApiEndpoints.getServiceUrl(service.id)) {
                method = HttpMethod.Patch
                setBody(service)
            }
            if (response.isSuccessful()) {
                return ApiResponse.Success(response.body())
            }
            ApiResponse.ApiException(response)
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }


    }

    suspend fun addService(service: ServiceDto): ApiResponse<ServiceDto> {
        return try {
            val response = httpClient.request(ApiEndpoints.SERVICES) {
                method = HttpMethod.Post
                setBody(service)
            }
            if (response.isSuccessful()) {
                return ApiResponse.Success(response.body())
            }
            ApiResponse.ApiException(response)
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }
    }

    suspend fun deleteService(id: Int): ApiResponse<Unit> {
        return try {
            val response = httpClient.delete(ApiEndpoints.getServiceUrl(id))
            if (response.isSuccessful()) {
                return ApiResponse.Success(Unit)
            }
            ApiResponse.ApiException(response)
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }
    }

    suspend fun getWelcome(): ApiResponse<Welcome> {
        return try {
            val response = httpClient.get(ApiEndpoints.WELCOME)
            if (response.isSuccessful()) {
                return ApiResponse.Success(response.body<List<Welcome>>().first())
            }
            ApiResponse.ApiException(response)
        } catch (e: Exception) {
            ApiResponse.UnhandledException(e)
        }

    }


}

fun HttpResponse.isSuccessful(): Boolean {
    return status.value in 200..299
}