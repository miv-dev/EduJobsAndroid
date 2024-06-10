package ru.edu.jobs.presentation.profile

import com.arkivanov.mvikotlin.core.store.Store
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.presentation.profile.ProfileStore.Intent
import ru.edu.jobs.presentation.profile.ProfileStore.Label
import ru.edu.jobs.presentation.profile.ProfileStore.State

interface ProfileStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object Logout: Intent
    }

    data class State(
        val user: User?,
    )

    sealed interface Label {
    }
}
