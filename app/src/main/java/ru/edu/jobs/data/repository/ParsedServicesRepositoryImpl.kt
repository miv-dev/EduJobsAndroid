package ru.edu.jobs.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.edu.jobs.data.dto_model.ParsedServiceDto
import ru.edu.jobs.data.local.ErrorService
import ru.edu.jobs.data.remote.ApiResponse
import ru.edu.jobs.data.remote.ApiService
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.ParsedService
import ru.edu.jobs.domain.entity.Site
import ru.edu.jobs.domain.repository.ParsedServicesRepository
import java.util.UUID
import javax.inject.Inject

class ParsedServicesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val errorService: ErrorService

): ParsedServicesRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _services = mutableListOf<ParsedService>()
    private val services: List<ParsedService>
        get() = _services.toList()

    private val _cacheUpdateFlow = MutableSharedFlow<Unit>(replay = 1)

    private val loadedListFlow = flow {
        _cacheUpdateFlow.emit(Unit)
        _cacheUpdateFlow.collect {
            if (services.isNotEmpty()) {
                emit(LoadState.Updating(services))
            } else {
                emit(LoadState.Loading)
            }
            when (val result = apiService.getParsedService()) {
                is ApiResponse.ApiException -> {
                    emit(LoadState.Error(errorService.getError(result.response)))
                }

                is ApiResponse.Success -> {
                    val list = result.data.map { it.toParsedService() }
                    _services.clear()
                    _services.addAll(list.shuffled())
                    emit(LoadState.Success(services))
                }

                is ApiResponse.UnhandledException -> {
                    emit(LoadState.Error(errorService.getError(result.exception)))
                }
            }
        }
    }

    override fun getParsedServiceById(uuid: UUID): ParsedService {
        return services.first { it.uuid == uuid }
    }

    override val servicesFlow: Flow<LoadState<List<ParsedService>>> = loadedListFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = LoadState.Loading
        )


}

private fun ParsedServiceDto.toParsedService(): ParsedService {
    return ParsedService(
        title = title,
        description = description,
        price = price,
        site = when (site) {
            "habr" -> Site.Habr
            "freelance" -> Site.Freelance
            else -> Site.Other
        },
        url = url
    )
}
