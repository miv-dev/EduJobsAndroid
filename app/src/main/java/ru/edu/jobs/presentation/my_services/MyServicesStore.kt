package ru.edu.jobs.presentation.my_services

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.usecase.service.my.DeleteMyServiceUseCase
import ru.edu.jobs.domain.usecase.service.my.GetMyServicesUseCase
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.my_services.MyServicesStore.Intent
import ru.edu.jobs.presentation.my_services.MyServicesStore.Label
import ru.edu.jobs.presentation.my_services.MyServicesStore.State
import javax.inject.Inject

interface MyServicesStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class onDeleteService(val service: Service) : Intent

    }

    data class State(
        val servicesState: LoadState<List<Service>>,
        val deleteState: DeleteState? = null,
    ) {
        sealed class DeleteState(open val id: Int) {
            data class Deleted(override val id: Int) : DeleteState(id)
            data class Deleting(override val id: Int) : DeleteState(id)
            data class Error(override val id: Int, val message: String) : DeleteState(id)
        }
    }

    sealed interface Label {
    }
}

class MyServicesStoreFactory @Inject constructor(
    private val getMyServicesUseCase: GetMyServicesUseCase,
    private val deleteMyServiceUseCase: DeleteMyServiceUseCase,
    private val storeFactory: StoreFactory
) {

    fun create(): MyServicesStore =
        object : MyServicesStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MyServicesStore",
            initialState = State(LoadState.Loading),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ChangeServiceState(val state: LoadState<List<Service>>) : Action
    }

    private sealed interface Msg {
        data class ChangeServiceState(val state: LoadState<List<Service>>) : Msg
        data class ChangeDeleteState(val state: State.DeleteState?) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getMyServicesUseCase().collect {
                    dispatch(Action.ChangeServiceState(it))
                }
            }

        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.onDeleteService -> {
                    scope.launch {
                        Msg.ChangeDeleteState(
                            State.DeleteState.Deleting(
                                intent.service.id
                            )
                        )
                        when (val result = deleteMyServiceUseCase(intent.service)) {
                            is Response.Error -> {
                                dispatch(
                                    Msg.ChangeDeleteState(
                                        State.DeleteState.Error(
                                            intent.service.id,
                                            errorToString(
                                                result.error
                                            )
                                        )
                                    )
                                )
                                delay(1000)
                                dispatch(
                                    Msg.ChangeDeleteState(
                                        null
                                    )
                                )
                            }

                            is Response.Success -> {

                                dispatch(
                                    Msg.ChangeDeleteState(
                                        State.DeleteState.Deleted(
                                            intent.service.id
                                        )
                                    )
                                )
                                delay(1000)
                                dispatch(
                                    Msg.ChangeDeleteState(
                                        null
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.ChangeServiceState -> dispatch(Msg.ChangeServiceState(action.state))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ChangeServiceState -> copy(servicesState = msg.state)
                is Msg.ChangeDeleteState -> copy(deleteState = msg.state)
            }
    }
}
