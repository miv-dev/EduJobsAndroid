package ru.edu.jobs.domain.usecase.parsed

import ru.edu.jobs.domain.repository.ParsedServicesRepository
import java.util.UUID
import javax.inject.Inject

class GetParsedServiceByIdUseCase @Inject constructor(
    private val repository: ParsedServicesRepository
){
    operator fun invoke(id: UUID) = repository.getParsedServiceById(id)
}