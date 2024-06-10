package ru.edu.jobs.presentation.login

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Errors
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.usecase.auth.LoginUseCase
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.login.LoginStore.Intent
import ru.edu.jobs.presentation.login.LoginStore.Label
import ru.edu.jobs.presentation.login.LoginStore.State
import javax.inject.Inject

interface LoginStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class UsernameChanged(val username: String) : Intent
        data class PasswordChanged(val password: String) : Intent
        data object OnSubmitClicked : Intent
        data object OnBackClicked : Intent
        data object OnRegisterClicked : Intent
    }

    data class State(
        val username: String = "",
        val password: String = "",
        val usernameError: String? = null,
        val passwordError: String? = null,
        val loginState: LoginState = LoginState.Idle
    ) {
        sealed interface LoginState {
            data object Idle : LoginState
            data object Loading : LoginState
            data object Success : LoginState
            data class Error(val error: String) : LoginState
        }
    }

    sealed interface Label {
        data object OnBackClicked : Label
        data object OnRegisterClicked : Label
    }
}

class LoginStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val loginUseCase: LoginUseCase
) {

    fun create(): LoginStore =
        object : LoginStore, Store<Intent, State, Label> by storeFactory.create(
            name = "LoginStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
    }

    private sealed interface Msg {
        data class UsernameChanged(val username: String) : Msg
        data class PasswordChanged(val password: String) : Msg
        data class LoginStateChanged(val loginState: State.LoginState) : Msg
        data class UsernameError(val error: String?) : Msg
        data class PasswordError(val error: String?) : Msg
    }


    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            val state = getState()
            when (intent) {
                Intent.OnSubmitClicked -> {
                    scope.launch {
                        dispatch(Msg.LoginStateChanged(State.LoginState.Loading))

                        when (val response = loginUseCase(state.username, state.password)) {
                            is Response.Error -> {
                                val error = response.error
                                if (error is Errors.FieldsError) {
                                    error.fields.forEach { (key, value) ->
                                        when (key) {
                                            "username" -> dispatch(Msg.UsernameError(value))
                                            "password" -> dispatch(Msg.PasswordError(value))
                                        }
                                    }
                                }
                                dispatch(Msg.LoginStateChanged(State.LoginState.Error(errorToString(error))))
                            }

                            is Response.Success -> dispatch(Msg.LoginStateChanged(State.LoginState.Success))
                        }

                    }
                }

                is Intent.PasswordChanged -> {
                    dispatch(Msg.PasswordChanged(intent.password))
                    dispatch(Msg.PasswordError(null))
                    dispatch(Msg.LoginStateChanged(State.LoginState.Idle))
                }

                is Intent.UsernameChanged -> {
                    dispatch(Msg.UsernameChanged(intent.username))
                    dispatch(Msg.UsernameError(null))
                    dispatch(Msg.LoginStateChanged(State.LoginState.Idle))
                }

                Intent.OnBackClicked -> publish(Label.OnBackClicked)
                Intent.OnRegisterClicked -> publish(Label.OnRegisterClicked)
            }
        }

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.LoginStateChanged -> copy(loginState = msg.loginState)
                is Msg.PasswordChanged -> copy(password = msg.password)
                is Msg.UsernameChanged -> copy(username = msg.username)
                is Msg.PasswordError -> copy(passwordError = msg.error)
                is Msg.UsernameError -> copy(usernameError = msg.error)
            }
    }
}
