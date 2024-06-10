package ru.edu.jobs.presentation.other

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.ParsedService

interface OtherComponent {
    val model: StateFlow<OtherStore.State>


    fun onDetailClicked(service: ParsedService)
}