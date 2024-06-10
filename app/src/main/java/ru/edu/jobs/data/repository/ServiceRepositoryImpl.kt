package ru.edu.jobs.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.edu.jobs.data.mappers.toService
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : ServiceRepository {
    private val scope = CoroutineScope(Dispatchers.IO)


    private val _services = mutableSetOf<Service>()
    private val services: List<Service>
        get() = _services.toList()


    override suspend fun getServiceById(id: Int): Service =
        services.find { it.id == id } ?: apiService.getService(id).toService()
            .also {
                _services.add(it)
                clearCache()
            }


    override suspend fun searchServices(query: String): List<Service> {
        return apiService.searchServices(query).map { it.toService() }.also {
            _services.addAll(it)
            clearCache()
        }
    }

    override fun cacheServices(services: List<Service>) {

        _services.removeIf { service -> service.id in services.map { it.id } }
        _services.addAll(services)
        clearCache()
    }

    private fun clearCache() {
        while (_services.size + 1 > CACHE_LIST_SIZE) {
            _services.first().also { _services.remove(it) }
        }
    }

    companion object {
        private const val CACHE_LIST_SIZE = 100
        private const val SEARCH_QUERIES_SIZE = 6
    }
}