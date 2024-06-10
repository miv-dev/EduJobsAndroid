package ru.edu.jobs.domain.usecase.auth

import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        role: Role,
        password: String,
        passwordConfirmation: String
    ) = repository.register(username, email, role, password, passwordConfirmation)
}