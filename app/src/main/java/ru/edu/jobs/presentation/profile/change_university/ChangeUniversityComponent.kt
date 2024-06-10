package ru.edu.jobs.presentation.profile.change_university

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Department

interface ChangeUniversityComponent {
    fun onUniversitySelected(department: Department?)
    fun onBackClicked()
    fun onSaveClicked()
    val model: StateFlow<ChangeUniversityStore.State>

}