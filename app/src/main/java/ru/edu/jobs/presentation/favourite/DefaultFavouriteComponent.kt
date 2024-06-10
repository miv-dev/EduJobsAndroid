package ru.edu.jobs.presentation.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.presentation.extensions.componentScope

class DefaultFavouriteComponent @AssistedInject constructor(
    private val factory: FavouriteStoreFactory,
    @Assisted("onServiceItemClicked") private val onServiceItemClicked: (ServiceShort) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : FavouriteComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { factory.create() }
    private val scope = componentScope()

    init {

        scope.launch {
            store.labels.collect {
                when (it) {
                    is FavouriteStore.Label.ServiceItemClick -> onServiceItemClicked(it.service)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FavouriteStore.State>
        get() = store.stateFlow



    override fun onServiceItemClick(serviceShort: ServiceShort) {
        store.accept(FavouriteStore.Intent.ServiceItemClick(serviceShort))
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onServiceItemClicked") onServiceItemClicked: (ServiceShort) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultFavouriteComponent
    }

}