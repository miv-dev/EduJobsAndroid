package ru.edu.jobs.data.repository

import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.edu.jobs.data.dto_model.RegisterResponseDto
import ru.edu.jobs.data.dto_model.TokenPair
import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.local.TokenService
import ru.edu.jobs.data.mappers.toUser
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.data.remote.AuthHttpClient
import ru.edu.jobs.domain.entity.AuthState
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.repository.AuthRepository
import ru.edu.jobs.domain.repository.UserRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenService: TokenService,
    private val authHttpClient: AuthHttpClient,
    private val userRepository: UserRepository,
    private val errorService: ErrorService
) : AuthRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)


    init {
        scope.launch {
            authHttpClient.onUnauthorizedFlow.collect {
                _authState.emit(AuthState.Unauthenticated)
            }
        }
    }


    override val authState: StateFlow<AuthState> = _authState

    init {
        fetchCurrentUser()
    }

    override suspend fun login(username: String, password: String): Response<Unit> {
        try {
            val response = apiService.login(username, password)
            if (response.status == HttpStatusCode.OK) {
                val tokens = response.body<TokenPair>()

                tokenService.setAccessToken(tokens.accessToken)
                tokenService.setRefreshToken(tokens.refreshToken)
                fetchCurrentUser()
                return Response.Success(Unit)
            } else {
                errorService.getError(response).let {
                    return Response.Error(it)
                }
            }
        } catch (e: Exception) {
            errorService.getError(e).let {
                return Response.Error(it)
            }
        }
    }


    override suspend fun register(
        username: String,
        email: String,
        role: Role,
        password: String,
        passwordConfirmation: String
    ): Response<Unit> {
        try {
            val userType = when (role) {
                Role.Specialist -> "employer"
                Role.Employer -> "specialist"
                Role.Admin -> throw IllegalArgumentException("Admin role is not supported")
            }
            val response =
                apiService.register(username, email, userType, password, passwordConfirmation)
            if (response.status.value in 200..299) {
                val tokens = response.body<RegisterResponseDto>().tokens

                tokenService.setAccessToken(tokens.accessToken)
                tokenService.setRefreshToken(tokens.refreshToken)
                fetchCurrentUser()
                return Response.Success(Unit)
            } else {
                errorService.getError(response).let {
                    return Response.Error(it)
                }
            }
        } catch (e: Exception) {
            errorService.getError(e).let {
                return Response.Error(it)
            }
        }
    }


    private fun fetchCurrentUser() {
        scope.launch {
            _authState.emit(AuthState.Loading)
            runCatching {
                apiService.getCurrentUser()
            }.onSuccess {
                _authState.emit(AuthState.Authenticated)
                userRepository.refreshUser(it.toUser())
            }.onFailure {
                _authState.emit(AuthState.Unauthenticated)
                userRepository.refreshUser(null)
            }
        }
    }


    override fun logout() {
        scope.launch {
            _authState.emit(AuthState.Unauthenticated)
            tokenService.setAccessToken(null)
            tokenService.setRefreshToken(null)
        }
    }
}