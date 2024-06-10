package ru.edu.jobs.domain.usecase.parsed

import ru.edu.jobs.domain.repository.ParsedServicesRepository
import javax.inject.Inject

class GetParsedServicesUseCase @Inject constructor(
    private val repository: ParsedServicesRepository
) {
    operator fun invoke() = repository.servicesFlow
}