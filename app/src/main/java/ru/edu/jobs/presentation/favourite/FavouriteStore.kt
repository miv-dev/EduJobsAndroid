package ru.edu.jobs.presentation.favourite

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.domain.usecase.service.favourite.GetFavouriteServicesUseCase
import ru.edu.jobs.presentation.favourite.FavouriteStore.Intent
import ru.edu.jobs.presentation.favourite.FavouriteStore.Label
import ru.edu.jobs.presentation.favourite.FavouriteStore.State
import javax.inject.Inject

interface FavouriteStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ServiceItemClick(val service: ServiceShort) : Intent

    }

    data class State(
        val services: List<ServiceShort>
    )

    sealed interface Label {
        data class ServiceItemClick(val service: ServiceShort) : Label
    }
}

class FavouriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavouriteServicesUseCase: GetFavouriteServicesUseCase,
) {


    fun create(): FavouriteStore =
        object : FavouriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavouriteStore",
            initialState = State(listOf()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavouriteServicesLoaded(val services: List<ServiceShort>) : Action

    }

    private sealed interface Msg {
        data class FavouriteServicesLoaded(val services: List<ServiceShort>) : Msg


    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getFavouriteServicesUseCase().collect {
                    dispatch(Action.FavouriteServicesLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {

                is Intent.ServiceItemClick -> publish(Label.ServiceItemClick(intent.service))

            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.FavouriteServicesLoaded -> {
                    dispatch(Msg.FavouriteServicesLoaded(action.services))
                }
            }
        }

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.FavouriteServicesLoaded -> copy(services = msg.services)
        }
    }
}
