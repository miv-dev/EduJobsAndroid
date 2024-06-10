package ru.edu.jobs.presentation.detail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.edu.jobs.presentation.extensions.componentScope

class DefaultDetailComponent @AssistedInject constructor(
    private val detailStoreFactory: DetailStoreFactory,
    @Assisted("onBack") onBack: () -> Unit,
    @Assisted("onEdit") private val onEdit: (Int) -> Unit,
    @Assisted("openBy") openBy: OpenBy,
    @Assisted("componentContext") componentContext: ComponentContext
) : DetailComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { detailStoreFactory.create(openBy) }
    private val scope = componentScope()


    init {
        scope.launch {
            lifecycle.doOnResume {
                store.accept(DetailStore.Intent.UpdateService)
            }

            store.labels.collect {
                when (it) {
                    DetailStore.Label.Back -> onBack()
                    is DetailStore.Label.Edit -> onEdit(it.id)
                }
            }
        }
    }

    override fun onEditClicked() {
        store.accept(DetailStore.Intent.EditClicked)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<DetailStore.State> = store.stateFlow
    override fun onBackClicked() {
        store.accept(DetailStore.Intent.BackClicked)
    }

    override fun onFavoriteClicked(status: Boolean){
        store.accept(DetailStore.Intent.ChangeIsFavourite(status))
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBack") onBack: () -> Unit,
            @Assisted("onEdit") onEdit: (Int) -> Unit,
            @Assisted("openBy") openBy: OpenBy,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultDetailComponent
    }
}