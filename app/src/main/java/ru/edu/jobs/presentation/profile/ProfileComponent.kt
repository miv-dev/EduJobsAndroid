package ru.edu.jobs.presentation.profile

import kotlinx.coroutines.flow.StateFlow

interface ProfileComponent {

    val model: StateFlow<ProfileStore.State>

    fun logout()

    fun changeUniversity()

    fun editProfile()
}