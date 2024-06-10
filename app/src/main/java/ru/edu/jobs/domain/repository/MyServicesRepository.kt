package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Service

interface MyServicesRepository {

    val servicesFlow: Flow<LoadState<List<Service>>>

    suspend fun addServices(service: Service): Response<Unit>

    fun update()

    suspend fun deleteService(service: Service): Response<Unit>
}