package ru.edu.jobs.domain.usecase.service.favourite

import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import javax.inject.Inject

class ObserveIsFavouriteUseCase @Inject constructor(
    private val favouriteServicesRepository: FavouriteServicesRepository
) {

    operator fun invoke(serviceId: Int) = favouriteServicesRepository.observeIsFavourite(serviceId)

}

