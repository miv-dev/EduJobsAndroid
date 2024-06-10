package ru.edu.jobs.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.usecase.service.SearchServicesUseCase
import ru.edu.jobs.domain.usecase.service.viewed.ViewServiceUseCase
import ru.edu.jobs.presentation.search.SearchStore.Intent
import ru.edu.jobs.presentation.search.SearchStore.Label
import ru.edu.jobs.presentation.search.SearchStore.State
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object Back : Intent
        data class ChangeQuery(val query: String) : Intent
        data class ViewService(val service: Service) : Intent
    }

    data class State(
        val query: String = "",
        val searchState: SearchState = SearchState.Loading

    ) {
        sealed interface SearchState {
            data object Loading : SearchState
            data object Empty : SearchState
            data class Loaded(val services: List<Service>) : SearchState
            data class Error(val error: Throwable) : SearchState
        }
    }

    sealed interface Label {
        data object OnBack : Label
        data class OnServiceClick(val service: Service) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val searchServicesUseCase: SearchServicesUseCase,
    private val viewServiceUseCase: ViewServiceUseCase,
    private val storeFactory: StoreFactory
) {

    fun create(query: String?): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(query),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class Load(val query: String) : Action
    }

    private sealed interface Msg {
        data class SetQuery(val query: String) : Msg
        data class SetSearchState(val searchState: State.SearchState) : Msg

    }

    private class BootstrapperImpl(val query: String?) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.Load(query ?: ""))
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        private var cancelJob: Job? = null
        private fun search(query: String) {
            cancelJob?.cancel()
            dispatch(Msg.SetSearchState(State.SearchState.Loading))
            cancelJob = scope.launch {
                runCatching {
                    searchServicesUseCase(query)
                }.onSuccess { services ->
                    if (services.isEmpty()) {
                        dispatch(Msg.SetSearchState(State.SearchState.Empty))

                    } else {
                        dispatch(Msg.SetSearchState(State.SearchState.Loaded(services)))
                    }
                }.onFailure { error ->
                    dispatch(Msg.SetSearchState(State.SearchState.Error(error)))
                }
            }
        }


        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangeQuery -> {
                    dispatch(Msg.SetQuery(intent.query))
                    search(intent.query)
                }

                is Intent.Back -> {
                    publish(Label.OnBack)
                }

                is Intent.ViewService -> {
                    viewServiceUseCase(intent.service.id)

                    publish(Label.OnServiceClick(intent.service))

                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.Load -> {
                    dispatch(Msg.SetQuery(action.query))
                    search(action.query)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetQuery -> copy(query = msg.query)
                is Msg.SetSearchState -> copy(searchState = msg.searchState)
            }
    }
}
