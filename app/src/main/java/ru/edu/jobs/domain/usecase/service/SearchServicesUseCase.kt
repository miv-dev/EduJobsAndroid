package ru.edu.jobs.domain.usecase.service

import ru.edu.jobs.domain.repository.ServiceRepository
import javax.inject.Inject

class SearchServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(query: String) = repository.searchServices(query)
}