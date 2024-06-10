package ru.edu.jobs.domain.usecase.service.favourite

import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import javax.inject.Inject

class AddToFavouriteUseCase @Inject constructor(
    private val favouriteServicesRepository: FavouriteServicesRepository
) {
    suspend operator fun invoke(service: Service) =
        favouriteServicesRepository.addToFavourite(service)

    suspend operator fun invoke(service: ServiceShort) =
        favouriteServicesRepository.addToFavourite(service)

}