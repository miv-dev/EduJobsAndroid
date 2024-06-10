package ru.edu.jobs.presentation.my_services

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Service

interface MyServicesComponent {

    val model: StateFlow<MyServicesStore.State>

    fun onAddClick()

    fun openDetail(service: Service)

    fun delete(service: Service)

    fun update()
}