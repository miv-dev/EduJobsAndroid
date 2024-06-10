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
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.repository.ServiceRepository
import ru.edu.jobs.domain.repository.UserRepository
import ru.edu.jobs.domain.repository.ViewedServicesRepository
import javax.inject.Inject

class ViewedServicesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val serviceRepository: ServiceRepository,
    private val userRepository: UserRepository,
    private val errorService: ErrorService
) : ViewedServicesRepository {

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

    private val loadedListFlow = flow {
        _cacheUpdateFlow.emit(Unit)
        _cacheUpdateFlow.collect {
            if (services.isNotEmpty()) {
                emit(LoadState.Updating(services))
            } else {
                emit(LoadState.Loading)
            }
            runCatching {
                val list = apiService.getViewedServices().map { it.toService() }
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
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = LoadState.Loading
        )


    override fun viewService(serviceId: Int) {
        scope.launch {
            runCatching {
                apiService.viewService(serviceId)
            }.onSuccess {
                _cacheUpdateFlow.emit(Unit)
            }
        }
    }

    override fun update() {
        scope.launch {
            _cacheUpdateFlow.emit(Unit)
        }
    }
}

