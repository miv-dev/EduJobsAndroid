package ru.edu.jobs.domain.usecase.user

import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) = userRepository.updateUser(user)

}