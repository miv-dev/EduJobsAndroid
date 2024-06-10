package ru.edu.jobs.domain.usecase.service.favourite

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import javax.inject.Inject

class GetFavouriteServicesUseCase @Inject constructor(
    private val favouriteServicesRepository: FavouriteServicesRepository
){
    operator fun invoke(): Flow<List<ServiceShort>> = favouriteServicesRepository.favouriteServices
}