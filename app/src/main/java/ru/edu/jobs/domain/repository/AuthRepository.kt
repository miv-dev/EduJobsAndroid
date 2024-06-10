package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.AuthState
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Role

interface AuthRepository {

    val authState: StateFlow<AuthState>

    suspend fun login(username: String, password: String): Response<Unit>

    suspend fun register(
        username: String, email: String, role: Role,
        password: String, passwordConfirmation: String
    ): Response<Unit>

    fun logout()
}