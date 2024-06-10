package ru.edu.jobs.domain.usecase.welcome

import ru.edu.jobs.domain.repository.WelcomeRepository
import javax.inject.Inject

class GetWelcomeUseCase @Inject constructor(
    private val welcomeRepository: WelcomeRepository
) {

    suspend operator fun invoke() = welcomeRepository.getWelcome()

}