package ru.edu.jobs.presentation.main

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Service

interface MainComponent {
    val model: StateFlow<MainStore.State>

    fun onDetailClicked(service: Service)
    fun onSearchClicked(query: String?)
}