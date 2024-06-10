package ru.edu.jobs.domain.usecase.user

import ru.edu.jobs.domain.repository.UserRepository
import javax.inject.Inject

class GetUserFlowUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke() = userRepository.userFlow
}