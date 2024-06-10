package ru.edu.jobs.presentation.search

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Service

interface SearchComponent {

    val model: StateFlow<SearchStore.State>

    fun onSearch(query: String)

    fun onBackClicked()

     fun viewService(service: Service)

}