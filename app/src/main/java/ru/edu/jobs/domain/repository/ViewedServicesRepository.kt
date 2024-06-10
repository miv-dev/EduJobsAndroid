package ru.edu.jobs.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Service

interface ViewedServicesRepository {

    val servicesFlow: Flow<LoadState<List<Service>>>

    fun viewService(serviceId: Int)

    fun update()

}


