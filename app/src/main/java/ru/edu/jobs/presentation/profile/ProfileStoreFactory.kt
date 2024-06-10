package ru.edu.jobs.presentation.profile

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.usecase.auth.LogoutUseCase
import ru.edu.jobs.domain.usecase.user.GetUserFlowUseCase
import ru.edu.jobs.presentation.profile.ProfileStore.Intent
import ru.edu.jobs.presentation.profile.ProfileStore.Label
import ru.edu.jobs.presentation.profile.ProfileStore.State
import javax.inject.Inject

class ProfileStoreFactory @Inject constructor(
    private val getUserFlowUseCase: GetUserFlowUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val storeFactory: StoreFactory
) {

    fun create(): ProfileStore =
        object : ProfileStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(
                user = null,
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class GetUser(val user: User?) : Action
    }

    private sealed interface Msg {
        data class GetUser(val user: User) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getUserFlowUseCase().collect{
                    dispatch(Action.GetUser(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when(intent){
                Intent.Logout -> logoutUseCase()
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.GetUser -> {
                    val user = action.user
                    if (user != null) {
                        dispatch(Msg.GetUser(user))
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.GetUser -> copy(user = msg.user)
            }
    }
}
