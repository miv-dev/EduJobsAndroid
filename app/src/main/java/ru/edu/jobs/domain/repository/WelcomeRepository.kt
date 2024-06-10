package ru.edu.jobs.domain.repository

import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Welcome

interface WelcomeRepository {
    suspend fun getWelcome(): Response<Welcome>
}
