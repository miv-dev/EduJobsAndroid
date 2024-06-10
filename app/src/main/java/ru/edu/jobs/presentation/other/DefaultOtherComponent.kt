package ru.edu.jobs.presentation.other

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.ParsedService

class DefaultOtherComponent @AssistedInject constructor(
    private val otherStoreFactory: OtherStoreFactory,
    @Assisted("onDetail") private val onDetail: (ParsedService) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : OtherComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { otherStoreFactory.create() }



    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<OtherStore.State>
        get() = store.stateFlow

    override fun onDetailClicked(service: ParsedService) {

        onDetail(service)
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onDetail") onDetail: (ParsedService) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultOtherComponent
    }
}