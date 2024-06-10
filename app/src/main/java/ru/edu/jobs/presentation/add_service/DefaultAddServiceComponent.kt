package ru.edu.jobs.presentation.add_service

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class DefaultAddServiceComponent @AssistedInject constructor(
    private val addServiceStoreFactory: AddServiceStoreFactory,
    @Assisted("openReason") val openReason: OpenReason,
    @Assisted("onBack") private val onBack: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext


) : AddServiceComponent, ComponentContext by componentContext {

    val store = instanceKeeper.getStore { addServiceStoreFactory.create(openReason) }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBack") onBack: () -> Unit,
            @Assisted("openReason") openReason: OpenReason,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultAddServiceComponent
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddServiceStore.State> = store.stateFlow
    override fun onNameChanged(string: String) {
        store.accept(AddServiceStore.Intent.ChangeServiceName(string))
    }

    override fun onDescriptionChanged(string: String) {
        store.accept(AddServiceStore.Intent.ChangeServiceDescription(string))
    }

    override fun onDeadlineChanged(date: LocalDate?) {
        store.accept(AddServiceStore.Intent.ChangeServiceDeadline(date))
    }

    override fun onCreateClick() {
        store.accept(AddServiceStore.Intent.SubmitService)
    }

    override fun onBackClick() {
        onBack()
    }
}