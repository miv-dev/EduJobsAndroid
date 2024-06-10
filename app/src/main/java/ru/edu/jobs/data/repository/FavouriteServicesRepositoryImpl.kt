package ru.edu.jobs.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.edu.jobs.data.local.db.FavouriteServicesDao
import ru.edu.jobs.data.mappers.toDbModel
import ru.edu.jobs.data.mappers.toEntities
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.domain.repository.FavouriteServicesRepository
import ru.edu.jobs.domain.repository.UserRepository
import javax.inject.Inject

class FavouriteServicesRepositoryImpl @Inject constructor(
    private val favouriteServicesDao: FavouriteServicesDao,
    private val userRepository: UserRepository
) : FavouriteServicesRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            userRepository.userChangedFlow.collect {
                if (it) {
                    favouriteServicesDao.removeAll()
                }
            }
        }


    }

    override val favouriteServices: Flow<List<ServiceShort>>
        get() = favouriteServicesDao.getFavouriteServices()
            .map { it.toEntities() }

    override fun observeIsFavourite(serviceId: Int): Flow<Boolean> =
        favouriteServicesDao.observeIsFavourite(serviceId)

    override suspend fun addToFavourite(service: Service) {
        favouriteServicesDao.addToFavourite(service.toDbModel())
    }

    override suspend fun addToFavourite(service: ServiceShort) {
        favouriteServicesDao.addToFavourite(service.toDbModel())
    }

    override suspend fun removeFromFavourite(serviceId: Int) {
        favouriteServicesDao.removeFromFavourite(serviceId)
    }
}