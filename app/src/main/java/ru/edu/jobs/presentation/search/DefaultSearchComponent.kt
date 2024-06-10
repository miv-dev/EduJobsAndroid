package ru.edu.jobs.presentation.search

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
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.presentation.extensions.componentScope

class DefaultSearchComponent @AssistedInject constructor(
    private val searchStoreFactory: SearchStoreFactory,
    @Assisted("query") query: String?,
    @Assisted("onBack") private val onBack: () -> Unit,
    @Assisted("onServiceClick") private val onServiceClick: (Service) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : SearchComponent, ComponentContext by componentContext{
    private val store = instanceKeeper.getStore { searchStoreFactory.create(query) }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    is SearchStore.Label.OnBack -> onBack()
                    is SearchStore.Label.OnServiceClick -> onServiceClick(it.service)
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchStore.State> = store.stateFlow

    override fun onSearch(query: String) {
        store.accept(SearchStore.Intent.ChangeQuery(query))
    }

    override fun onBackClicked() {
        store.accept(SearchStore.Intent.Back)
    }

    override fun viewService(service: Service) {
        store.accept(SearchStore.Intent.ViewService(service))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("query") query: String?,
            @Assisted("onBack") onBack: () -> Unit,
            @Assisted("onServiceClick") onServiceClick: (Service) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultSearchComponent
    }
}