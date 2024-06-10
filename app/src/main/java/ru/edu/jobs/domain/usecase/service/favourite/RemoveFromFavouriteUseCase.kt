package ru.edu.jobs.domain.usecase.service.favourite

import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import javax.inject.Inject

class RemoveFromFavouriteUseCase @Inject constructor(
    private val favouriteServicesRepository: FavouriteServicesRepository
) {

    suspend operator fun invoke(serviceId: Int) =
        favouriteServicesRepository.removeFromFavourite(serviceId)

}