package ru.edu.jobs.domain.usecase.auth

import ru.edu.jobs.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.logout()
}