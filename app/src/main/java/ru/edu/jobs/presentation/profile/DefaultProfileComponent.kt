package ru.edu.jobs.presentation.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultProfileComponent @AssistedInject constructor(
    private val profileStoreFactory: ProfileStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,
    @Assisted("onEditProfile") private val onEditProfile: () -> Unit,
    @Assisted("onChangeUniversity") private val onChangeUniversity: () -> Unit,
) : ProfileComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { profileStoreFactory.create() }

    override val model: StateFlow<ProfileStore.State>
        get() = store.stateFlow

    override fun logout() {
        store.accept(ProfileStore.Intent.Logout)
    }

    override fun changeUniversity() {
        onChangeUniversity()
    }

    override fun editProfile() {
        onEditProfile()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
            @Assisted("onEditProfile") onEditProfile: () -> Unit,
            @Assisted("onChangeUniversity") onChangeUniversity: () -> Unit,
            ): DefaultProfileComponent
    }
}