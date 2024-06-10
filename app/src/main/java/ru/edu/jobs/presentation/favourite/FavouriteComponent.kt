package ru.edu.jobs.presentation.favourite

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.ServiceShort

interface FavouriteComponent {
    val model: StateFlow<FavouriteStore.State>

    fun onServiceItemClick(serviceShort: ServiceShort)
}