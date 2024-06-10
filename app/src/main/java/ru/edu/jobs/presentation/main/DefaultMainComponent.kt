package ru.edu.jobs.presentation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort

class DefaultMainComponent @AssistedInject constructor(
    private val mainStoreFactory: MainStoreFactory,
    @Assisted("onDetail") private val onDetail: (ServiceShort) -> Unit,
    @Assisted("onSearch") private val onSearch: (String?) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : MainComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { mainStoreFactory.create() }


    init {
//        this.lifecycle.doOnStart {
//            store.accept(MainStore.Intent.UpdateLastViewed)
//        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<MainStore.State>
        get() = store.stateFlow

    override fun onDetailClicked(service: Service) {
        val serviceShort = ServiceShort(
            id = service.id,
            name = service.name,
            description = service.description,

        )
        onDetail(serviceShort)
    }

    override fun onSearchClicked(query: String?) {
        onSearch(query)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onDetail") onDetail: (ServiceShort) -> Unit,
            @Assisted("onSearch") onSearch: (String?) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultMainComponent
    }
}