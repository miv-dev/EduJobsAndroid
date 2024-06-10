package ru.edu.jobs.presentation.add_service

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Errors
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.usecase.auth.GetUserUseCase
import ru.edu.jobs.domain.usecase.service.GetServiceByIdUseCase
import ru.edu.jobs.domain.usecase.service.my.AddMyServiceUseCase
import ru.edu.jobs.presentation.add_service.AddServiceStore.Intent
import ru.edu.jobs.presentation.add_service.AddServiceStore.Label
import ru.edu.jobs.presentation.add_service.AddServiceStore.State
import ru.edu.jobs.presentation.extensions.errorToString
import java.time.LocalDate
import javax.inject.Inject

interface AddServiceStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ChangeServiceName(val name: String) : Intent
        data class ChangeServiceDescription(val description: String) : Intent
        data class ChangeServiceDeadline(val deadline: LocalDate?) : Intent
        data object SubmitService : Intent
    }

    data class State(
        val openReason: OpenReason,
        val user: User? = null,
        val name: String = "",
        val nameError: String? = null,
        val description: String = "",
        val descriptionError: String? = null,
        val deadline: LocalDate? = null,
        val sendState: SendState = SendState.Idle,
    ) {
        sealed interface SendState {
            data object Idle : SendState
            data object Loading : SendState
            data object Success : SendState
            data class Error(val error: String) : SendState
        }
    }

    sealed interface Label {
    }
}

class AddServiceStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getUserUseCase: GetUserUseCase,
    private val getServiceByIdUseCase: GetServiceByIdUseCase,
    private val addServiceUseCase: AddMyServiceUseCase
) {

    fun create(openReason: OpenReason): AddServiceStore =
        object : AddServiceStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddServiceStore",
            initialState = State(openReason),
            bootstrapper = BootstrapperImpl(openReason),
            executorFactory = { ExecutorImpl(openReason) },
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class GetService(val id: Int) : Action
        data class SetUser(val user: User?) : Action
    }

    private sealed interface Msg {
        data class SetUser(val user: User?) : Msg
        data class SetSendState(val sendState: State.SendState) : Msg
        data class SetServiceNameError(val error: String?) : Msg
        data class SetServiceName(val name: String) : Msg
        data class SetServiceDescriptionError(val error: String?) : Msg
        data class SetServiceDescription(val description: String) : Msg
        data class SetServiceDeadline(val deadline: LocalDate?) : Msg
        data object ClearErrors : Msg
    }

    private inner class BootstrapperImpl(val openReason: OpenReason) :
        CoroutineBootstrapper<Action>() {
        override fun invoke() {
            if (openReason is OpenReason.EditService) {
                dispatch(Action.GetService(openReason.id))
            }
            scope.launch {
                val user = getUserUseCase()
                dispatch(Action.SetUser(user))
            }
        }
    }

    private inner class ExecutorImpl(private val openReason: OpenReason) :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangeServiceName -> {
                    dispatch(Msg.SetServiceName(intent.name))
                    dispatch(Msg.SetServiceNameError(null))
                }
                is Intent.ChangeServiceDescription -> {
                    dispatch(Msg.SetServiceDescription(intent.description))
                    dispatch(Msg.SetServiceDescriptionError(null))
                }
                is Intent.ChangeServiceDeadline -> dispatch(Msg.SetServiceDeadline(intent.deadline))
                is Intent.SubmitService -> {
                    val state = getState()
                    if (state.user == null) return
                    val serviceId = when (openReason) {
                        OpenReason.AddService -> -1
                        is OpenReason.EditService -> openReason.id
                    }

                    val service = Service(
                        id = serviceId,
                        name = state.name,
                        description = state.description,
                        deadline = state.deadline,
                        dateCreated = null,
                        user = state.user
                    )

                    scope.launch {
                        dispatch(Msg.SetSendState(State.SendState.Loading))
                        when (val result = addServiceUseCase(service)) {
                            is Response.Success -> {
                                dispatch(Msg.SetSendState(State.SendState.Success))
                            }

                            is Response.Error -> {
                                if (result.error is Errors.FieldsError) {
                                    result.error.fields.forEach {
                                        when (it.key) {
                                            "service_name" -> dispatch(Msg.SetServiceNameError(it.value))
                                            "service_details" -> dispatch(Msg.SetServiceDescriptionError(it.value))
                                        }
                                    }
                                }

                                dispatch(Msg.SetSendState(State.SendState.Error(errorToString(result.error))))
                                delay(2000)
                                dispatch(Msg.SetSendState(State.SendState.Idle))
                            }
                        }
                    }

                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.SetUser -> {
                    dispatch(Msg.SetUser(action.user))
                }

                is Action.GetService -> {
                    scope.launch {
                        val service = getServiceByIdUseCase(action.id)
                        dispatch(Msg.SetServiceName(service.name))
                        dispatch(Msg.SetServiceDescription(service.description))
                        dispatch(Msg.SetServiceDeadline(service.deadline))

                    }

                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SetUser -> copy(user = msg.user)
                is Msg.SetServiceName -> copy(name = msg.name)
                is Msg.SetServiceDescription -> copy(description = msg.description)
                is Msg.SetServiceDeadline -> copy(deadline = msg.deadline)
                is Msg.SetSendState -> copy(sendState = msg.sendState)
                is Msg.SetServiceDescriptionError -> copy(descriptionError = msg.error)
                is Msg.SetServiceNameError -> copy(nameError = msg.error)
                Msg.ClearErrors -> copy(nameError = null, descriptionError = null)
            }
    }
}
