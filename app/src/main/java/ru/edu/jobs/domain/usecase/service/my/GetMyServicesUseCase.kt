package ru.edu.jobs.domain.usecase.service.my

import ru.edu.jobs.domain.repository.MyServicesRepository
import javax.inject.Inject

class GetMyServicesUseCase @Inject constructor(
    private val repository: MyServicesRepository
){
    operator fun invoke() = repository.servicesFlow
}