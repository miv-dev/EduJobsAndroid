package ru.edu.jobs.data.repository

import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.remote.ApiResponse
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Welcome
import ru.edu.jobs.domain.repository.WelcomeRepository
import javax.inject.Inject

class WelcomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val errorService: ErrorService
): WelcomeRepository {


    override suspend fun getWelcome(): Response<Welcome> {
        return when (val response = apiService.getWelcome()) {
            is ApiResponse.Success -> {
                Response.Success(response.data)
            }

            is ApiResponse.ApiException -> {
                Response.Error(errorService.getError(response.response))
            }

            is ApiResponse.UnhandledException -> {
                Response.Error(errorService.getError(response.exception))
            }
        }
    }
}