package ru.edu.jobs.presentation.add_service

import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface AddServiceComponent {
    val model: StateFlow<AddServiceStore.State>

    fun onNameChanged(string: String)
    fun onDescriptionChanged(string: String)
    fun onDeadlineChanged(date: LocalDate?)

    fun onCreateClick()
    fun onBackClick()
}