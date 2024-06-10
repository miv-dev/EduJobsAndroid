package ru.edu.jobs.domain.usecase.service

import ru.edu.jobs.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServiceByIdUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(id: Int) = repository.getServiceById(id)
}