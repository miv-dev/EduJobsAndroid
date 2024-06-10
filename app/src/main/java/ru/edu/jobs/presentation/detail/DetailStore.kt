package ru.edu.jobs.presentation.detail

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.ParsedService
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.usecase.parsed.GetParsedServiceByIdUseCase
import ru.edu.jobs.domain.usecase.service.GetServiceByIdUseCase
import ru.edu.jobs.domain.usecase.service.favourite.AddToFavouriteUseCase
import ru.edu.jobs.domain.usecase.service.favourite.ObserveIsFavouriteUseCase
import ru.edu.jobs.domain.usecase.service.favourite.RemoveFromFavouriteUseCase
import ru.edu.jobs.presentation.detail.DetailStore.Intent
import ru.edu.jobs.presentation.detail.DetailStore.Label
import ru.edu.jobs.presentation.detail.DetailStore.State
import java.util.UUID
import javax.inject.Inject

interface DetailStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object BackClicked : Intent
        data object UpdateService : Intent
        data object EditClicked : Intent

        data class ChangeIsFavourite(val isFavourite: Boolean) : Intent
    }

    data class State(
        val openBy: OpenBy,
        val isFavourite: Boolean,
        val serviceState: ServiceState,
        val parsedService: ParsedService? = null
    ) {
        sealed interface ServiceState {
            data object Loading : ServiceState
            data class Loaded(val service: Service) : ServiceState
            data class Error(val error: Throwable) : ServiceState
        }
    }

    sealed interface Label {
        data object Back : Label
        data class Edit(val id: Int) : Label
    }
}

class DetailStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getServiceByIdUseCase: GetServiceByIdUseCase,
    private val observeIsFavouriteUseCase: ObserveIsFavouriteUseCase,
    private val addToFavouriteUseCase: AddToFavouriteUseCase,
    private val removeFromFavouriteUseCase: RemoveFromFavouriteUseCase,
    private val getParsedServiceByIdUseCase: GetParsedServiceByIdUseCase
) {

    fun create(openBy: OpenBy): DetailStore =
        object : DetailStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailStore",
            initialState = State(
                openBy,
                serviceState = State.ServiceState.Loading,
                isFavourite = false
            ),
            bootstrapper = BootstrapperImpl(openBy),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {

        }

    private sealed interface Action {
        data class LoadService(val id: Int) : Action
        data class LoadParsedService(val uuid: UUID) : Action
        data class ChangeIsFavourite(val isFavourite: Boolean) : Action
    }

    private sealed interface Msg {
        data class ChangeParsedService(val parsedService: ParsedService) : Msg
        data class ChangeServiceState(val serviceState: State.ServiceState) : Msg
        data class ChangeIsFavourite(val isFavourite: Boolean) : Msg

    }

    private inner class BootstrapperImpl(val openBy: OpenBy) :
        CoroutineBootstrapper<Action>() {
        override fun invoke() {
            when (openBy) {
                is OpenBy.Owner -> dispatch(Action.LoadService(openBy.id))
                is OpenBy.Parsed -> dispatch(Action.LoadParsedService(openBy.uuid))
                is OpenBy.Viewer -> {
                    scope.launch {
                        observeIsFavouriteUseCase(openBy.id).collect {
                            dispatch(Action.ChangeIsFavourite(it))
                        }
                    }

                    dispatch(Action.LoadService(openBy.id))
                }
            }


        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            val state = getState()
            when (intent) {
                Intent.BackClicked -> publish(Label.Back)
                is Intent.ChangeIsFavourite -> {
                    scope.launch {
                        val service = (state.serviceState as? State.ServiceState.Loaded)?.service
                        service?.let {
                            if (intent.isFavourite) {
                                addToFavouriteUseCase(service)
                            } else {
                                removeFromFavouriteUseCase(service.id)
                            }
                        }
                    }
                }

                Intent.UpdateService -> {
                    val service = (state.serviceState as? State.ServiceState.Loaded)?.service
                    service?.let {
                        updateService(service.id)
                    }
                }

                Intent.EditClicked -> {
                    when (state.openBy) {
                        is OpenBy.Owner -> publish(Label.Edit(state.openBy.id))
                        is OpenBy.Viewer -> publish(Label.Edit(state.openBy.id))
                        is OpenBy.Parsed -> {}
                    }

                }
            }
        }

        private fun updateService(id: Int) {
            scope.launch {
                dispatch(Msg.ChangeServiceState(State.ServiceState.Loading))
                runCatching {
                    getServiceByIdUseCase(id)
                }.onSuccess {
                    dispatch(Msg.ChangeServiceState(State.ServiceState.Loaded(it)))
                }.onFailure {
                    dispatch(Msg.ChangeServiceState(State.ServiceState.Error(it)))

                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.LoadService -> {
                    updateService(action.id)
                }

                is Action.ChangeIsFavourite -> dispatch(Msg.ChangeIsFavourite(action.isFavourite))
                is Action.LoadParsedService -> {
                    getParsedServiceByIdUseCase(action.uuid)
                        .also { dispatch(Msg.ChangeParsedService(it)) }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ChangeServiceState -> copy(serviceState = msg.serviceState)
                is Msg.ChangeIsFavourite -> copy(isFavourite = msg.isFavourite)
                is Msg.ChangeParsedService -> copy(parsedService = msg.parsedService)
            }
    }
}
