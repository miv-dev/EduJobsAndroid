package ru.edu.jobs.domain.usecase.auth

import kotlinx.coroutines.flow.firstOrNull
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? = userRepository.userFlow.firstOrNull()
}