package ru.edu.jobs.data.repository

import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.remote.ApiResponse
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.domain.repository.UniversityRepository
import javax.inject.Inject

class UniversityRepositoryImpl @Inject constructor (
    private val apiService: ApiService,
    private val errorService: ErrorService
): UniversityRepository {
    override suspend fun loadUniversities(): Response<List<Department>> {

        return when(val result = apiService.loadUniversities()){
            is ApiResponse.ApiException -> Response.Error(errorService.getError(result.response))
            is ApiResponse.UnhandledException -> Response.Error(errorService.getError(result.exception))
            is ApiResponse.Success -> Response.Success(result.data)
        }

    }
}