package ru.edu.jobs.presentation.main

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.usecase.service.viewed.GetViewedServicesUseCase
import ru.edu.jobs.domain.usecase.service.viewed.UpdateViewedServicesUseCase
import ru.edu.jobs.presentation.main.MainStore.Intent
import ru.edu.jobs.presentation.main.MainStore.Label
import ru.edu.jobs.presentation.main.MainStore.State
import javax.inject.Inject

interface MainStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object UpdateLastViewed: Intent
    }


    data class State(
        val servicesState: LoadState<List<Service>> = LoadState.Loading,
    )

    sealed interface Label {
    }
}

class MainStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getViewedServicesUseCase: GetViewedServicesUseCase,
    private val updateViewedServicesUseCase: UpdateViewedServicesUseCase,
) {

    fun create(): MainStore =
        object : MainStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class LoadServices(val servicesLoadState: LoadState<List<Service>>) : Action
    }

    private sealed interface Msg {
        data class LoadServices(val servicesLoadState: LoadState<List<Service>>) : Msg

    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {

                getViewedServicesUseCase().collect { state ->
                    dispatch(Action.LoadServices(state))
                }

            }

        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {


        override fun executeIntent(intent: Intent, getState: () -> State) {
            when(intent){
                Intent.UpdateLastViewed -> updateViewedServicesUseCase()
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.LoadServices -> {
                    dispatch(Msg.LoadServices(action.servicesLoadState))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.LoadServices -> copy(servicesState = msg.servicesLoadState)
            }
    }
}
