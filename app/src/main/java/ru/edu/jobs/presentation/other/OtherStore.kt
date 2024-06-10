package ru.edu.jobs.presentation.other

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.ParsedService
import ru.edu.jobs.domain.usecase.parsed.GetParsedServicesUseCase
import javax.inject.Inject

interface OtherStore : Store<OtherStore.Intent,OtherStore.State, OtherStore.Label> {

    sealed interface Intent {
    }


    data class State(
        val parsedServicesState: LoadState<List<ParsedService>> = LoadState.Loading,
    )

    sealed interface Label {
    }
}

class OtherStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getParsedServicesUseCase: GetParsedServicesUseCase
) {

    fun create(): OtherStore =
        object : OtherStore, Store<OtherStore.Intent, OtherStore.State, OtherStore.Label> by storeFactory.create(
            name = "OtherStore",
            initialState = OtherStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class LoadParsedServices(val parsedServicesLoadState: LoadState<List<ParsedService>>) : Action
    }

    private sealed interface Msg {
        data class LoadParsedServices(val parsedServicesLoadState: LoadState<List<ParsedService>>) : Msg

    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                launch {
                    getParsedServicesUseCase().collect { state ->
                        dispatch(Action.LoadParsedServices(state))
                    }
                }

            }

        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<OtherStore.Intent, Action, OtherStore.State, Msg, OtherStore.Label>() {



        override fun executeAction(action: Action, getState: () -> OtherStore.State) {
            when (action) {


                is Action.LoadParsedServices -> {
                    dispatch(Msg.LoadParsedServices(action.parsedServicesLoadState))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<OtherStore.State, Msg> {
        override fun OtherStore.State.reduce(msg: Msg): OtherStore.State =
            when (msg) {
                is Msg.LoadParsedServices -> copy(parsedServicesState = msg.parsedServicesLoadState)
            }
    }
}
