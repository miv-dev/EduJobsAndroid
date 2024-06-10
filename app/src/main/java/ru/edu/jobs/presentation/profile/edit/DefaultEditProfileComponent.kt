package ru.edu.jobs.presentation.profile.edit

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Role

class DefaultEditProfileComponent @AssistedInject constructor(
    private val editProfileStoreFactory: EditProfileStoreFactory,
    @Assisted("onBack") private val onBack: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
): EditProfileComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { editProfileStoreFactory.create() }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<EditProfileStore.State> = store.stateFlow

    override fun onBackClick() {
        onBack()
    }

    override fun onSaveClick() {
        store.accept(EditProfileStore.Intent.OnSaveClick)
    }

    override fun onChangeLastName(lastName: String) {
        store.accept(EditProfileStore.Intent.OnChangeLastName(lastName))
    }
    override fun onChangeFirstName(firstName: String) {
        store.accept(EditProfileStore.Intent.OnChangeFirstName(firstName))
    }

    override fun onChangePhone(phone: String) {
        store.accept(EditProfileStore.Intent.OnChangePhone(phone))
    }

    override fun onChangeType(type: Role) {
        store.accept(EditProfileStore.Intent.OnChangeType(type))
    }

    override fun onChangeEmail(email: String) {
        store.accept(EditProfileStore.Intent.OnChangeEmail(email))
    }

    override fun onChangeUsername(username: String) {
        store.accept(EditProfileStore.Intent.OnChangeUsername(username))
    }

    override fun onChangeAvatar(avatar: String) {
        store.accept(EditProfileStore.Intent.OnChangeAvatar(avatar))
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
            @Assisted("onBack") onBack: () -> Unit
            ): DefaultEditProfileComponent
    }
}