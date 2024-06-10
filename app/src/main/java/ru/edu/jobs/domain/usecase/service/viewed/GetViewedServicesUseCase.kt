package ru.edu.jobs.domain.usecase.service.viewed

import ru.edu.jobs.domain.repository.ViewedServicesRepository
import javax.inject.Inject

class GetViewedServicesUseCase @Inject constructor(
    private val repository: ViewedServicesRepository
) {
    operator fun invoke() = repository.servicesFlow
}