package ru.edu.jobs.domain.usecase.service.my

import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.repository.MyServicesRepository
import javax.inject.Inject

class AddMyServiceUseCase @Inject constructor(
    private val repository: MyServicesRepository
) {
    suspend operator fun invoke(service: Service) = repository.addServices(service)
}