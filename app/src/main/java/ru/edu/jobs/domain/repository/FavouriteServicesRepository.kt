package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort

interface FavouriteServicesRepository {
    val favouriteServices: Flow<List<ServiceShort>>

    fun observeIsFavourite(serviceId: Int): Flow<Boolean>

    suspend fun addToFavourite(service: Service)

    suspend fun addToFavourite(service: ServiceShort)

    suspend fun removeFromFavourite(serviceId: Int)
}