package ru.edu.jobs.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.mappers.toService
import ru.edu.jobs.data.mappers.toServiceDto
import ru.edu.jobs.data.remote.ApiResponse
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.repository.MyServicesRepository
import ru.edu.jobs.domain.repository.ServiceRepository
import ru.edu.jobs.domain.repository.UserRepository
import ru.edu.jobs.ext.mergeWith
import javax.inject.Inject

class MyServicesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val errorService: ErrorService,
) : MyServicesRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _services = mutableListOf<Service>()
    private val services: List<Service>
        get() = _services

    init {
        scope.launch {
            userRepository.userChangedFlow.collect {
                if (it) {
                    _services.clear()
                    update()
                }
            }
        }


    }

    private val _cacheUpdateFlow = MutableSharedFlow<Unit>(replay = 1)
    private val refreshedServicesFlow = MutableSharedFlow<LoadState<List<Service>>>()

    private val loadedListFlow = flow {
        _cacheUpdateFlow.emit(Unit)
        _cacheUpdateFlow.collect {
            if (services.isNotEmpty()) {
                emit(LoadState.Updating(services))
            } else {
                emit(LoadState.Loading)
            }
            runCatching {
                val list = apiService.getUserServices().map { it.toService() }
                _services.clear()
                _services.addAll(list)
                serviceRepository.cacheServices(list)
            }.onSuccess {
                emit(LoadState.Success(services))
            }.onFailure {
                emit(LoadState.Error(errorService.getError(it)))
            }
        }
    }

    override val servicesFlow: Flow<LoadState<List<Service>>> = loadedListFlow
        .mergeWith(refreshedServicesFlow)
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = LoadState.Loading
        )

    override suspend fun deleteService(service: Service): Response<Unit> {
        return when (val response = apiService.deleteService(service.id)) {
            is ApiResponse.Success -> {
                scope.launch {
                    _services.remove(service)
                    refreshedServicesFlow.emit(LoadState.Success(services))
                }
                Response.Success(Unit)
            }

            is ApiResponse.ApiException -> Response.Error(errorService.getError(response.response))


            is ApiResponse.UnhandledException -> Response.Error(errorService.getError(response.exception))
        }
    }


    override suspend fun addServices(service: Service): Response<Unit> {
        val response = if (service.id == -1) {
            apiService.addService(service.toServiceDto())
        } else {
            apiService.updateService(service.toServiceDto())
        }
        return when (response) {
            is ApiResponse.Success -> {
                update()
                Response.Success(Unit)
            }

            is ApiResponse.ApiException -> Response.Error(errorService.getError(response.response))


            is ApiResponse.UnhandledException -> Response.Error(errorService.getError(response.exception))
        }
    }

    override fun update() {
        scope.launch {
            _cacheUpdateFlow.emit(Unit)
        }
    }
}