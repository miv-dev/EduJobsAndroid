package ru.edu.jobs.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import ru.edu.jobs.data.dto_model.TokenPair
import ru.edu.jobs.data.local.TokenService
import javax.inject.Inject

class AuthHttpClient @Inject constructor(
    private val tokenService: TokenService
) {
    private val _onUnauthorizedFlow = MutableStateFlow(Unit)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val authorizePlugin = createClientPlugin("auth header") {
        onRequest { request, _ ->
            headers {
                if (request.url.encodedPath.contains("login") || request.url.encodedPath.contains("register") || request.url.encodedPath.contains("welcome") ) {
                    if (contains(HttpHeaders.Authorization)) {
                        request.headers.remove(HttpHeaders.Authorization)
                    }
                } else {
                    if (contains(HttpHeaders.Authorization)) {
                        request.headers.remove(HttpHeaders.Authorization)
                    }
                    val token = tokenService.loadAccessToken() ?: ""
                    Log.d("HTTP call tokens", token)
                    request.headers.append(HttpHeaders.Authorization, "Bearer $token")
                }


            }

        }
    }

    val httpClient: HttpClient = HttpClient(Android) {

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP call", message)
                }
            }
            level = LogLevel.ALL
        }
        install(Auth) {
            bearer {
                refreshTokens {
                    val refresh = tokenService.loadRefreshToken()
                    refresh?.let {
                        val response = client.post {
                            markAsRefreshTokenRequest()
                            url("/auth/refresh/")
                            setBody(hashMapOf("refresh" to it))
                        }
                        if (response.status == HttpStatusCode.OK) {
                            val token = response.body<TokenPair>()

                            tokenService.setAccessToken(token.accessToken)
                            tokenService.setRefreshToken(token.refreshToken)
                            return@refreshTokens BearerTokens(
                                token.accessToken,
                                token.refreshToken
                            )
                        }

                    }
                    tokenService.setRefreshToken(null)
                    tokenService.setAccessToken(null)
                    _onUnauthorizedFlow.emit(Unit)
                    null
                }
                sendWithoutRequest { true }
            }
        }
        install(authorizePlugin)
    }


    val onUnauthorizedFlow: StateFlow<Unit> = _onUnauthorizedFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = Unit
        )


    companion object {
        private const val BASE_URL = "http://77.232.129.59:8000"
    }
}