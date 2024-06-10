package ru.edu.jobs.presentation.register

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Errors
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.usecase.auth.RegisterUseCase
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.register.RegisterStore.Intent
import ru.edu.jobs.presentation.register.RegisterStore.Label
import ru.edu.jobs.presentation.register.RegisterStore.State
import javax.inject.Inject

interface RegisterStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack : Intent
        data object ClickLogin : Intent
        data class UsernameChanged(val username: String) : Intent
        data class EmailChanged(val email: String) : Intent
        data class UserTypeChanged(val type: Role) : Intent
        data class PasswordChanged(val password: String) : Intent
        data class PasswordConfirmationChanged(val passwordConfirmation: String) : Intent
        data object RegisterClicked : Intent

    }

    data class State(

        val username: String = "",
        val usernameError: String? = null,
        val email: String = "",
        val emailError: String? = null,
        val usertype: Role = Role.Specialist,
        val password: String = "",
        val passwordError: String? = null,

        val passwordConfirmation: String = "",
        val passwordConfirmationError: String? = null,

        val registerState: RegisterState = RegisterState.Idle
    ) {
        sealed interface RegisterState {
            data object Idle : RegisterState
            data object Loading : RegisterState
            data object Success : RegisterState
            data class Error(val error: String) : RegisterState
        }
    }

    sealed interface Label {
        data object ClickBack : Label
        data object ClickLogin : Label
    }
}

class RegisterStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val registerUseCase: RegisterUseCase
) {

    fun create(): RegisterStore =
        object : RegisterStore, Store<Intent, State, Label> by storeFactory.create(
            name = "RegisterStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}


    private sealed interface Msg {
        data class UsernameChanged(val username: String) : Msg
        data class UserTypeChanged(val type: Role) : Msg
        data class EmailChanged(val email: String) : Msg
        data class PasswordChanged(val password: String) : Msg
        data class PasswordConfirmationChanged(val passwordConfirmation: String) : Msg
        data class RegisterStateChanged(val registerState: State.RegisterState) : Msg
        data class UsernameErrorChanged(val error: String?) : Msg
        data class EmailErrorChanged(val error: String?) : Msg
        data class PasswordErrorChanged(val error: String?) : Msg
        data class PasswordConfirmationErrorChanged(val error: String?) : Msg
        data object ClearErrors: Msg
    }


    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.EmailChanged -> dispatch(Msg.EmailChanged(intent.email))
                is Intent.PasswordChanged -> dispatch(Msg.PasswordChanged(intent.password))
                is Intent.PasswordConfirmationChanged -> dispatch(
                    Msg.PasswordConfirmationChanged(
                        intent.passwordConfirmation
                    )
                )

                is Intent.UsernameChanged -> dispatch(Msg.UsernameChanged(intent.username))

                Intent.RegisterClicked -> {
                    scope.launch {
                        val state = getState()
                        dispatch(Msg.ClearErrors)
                        when (val response = registerUseCase(
                            state.username,
                            state.email,
                            state.usertype,
                            state.password,
                            state.passwordConfirmation
                        )) {
                            is Response.Error -> {

                                val error = response.error
                                if (error is Errors.FieldsError) {
                                    error.fields.forEach { (key, value) ->
                                        when (key) {
                                            "username" -> dispatch(Msg.UsernameErrorChanged(value))
                                            "email" -> dispatch(Msg.EmailErrorChanged(value))
                                            "password" -> dispatch(Msg.PasswordErrorChanged(value))
                                            "password2" -> dispatch(Msg.PasswordConfirmationErrorChanged(value))
                                        }
                                    }
                                }
                                dispatch(
                                    Msg.RegisterStateChanged(
                                        State.RegisterState.Error(
                                            errorToString(
                                                error
                                            )
                                        )
                                    )
                                )
                            }

                            is Response.Success -> dispatch(Msg.RegisterStateChanged(State.RegisterState.Success))
                        }

                    }
                }

                Intent.ClickBack -> publish(Label.ClickBack)

                Intent.ClickLogin -> publish(Label.ClickLogin)
                is Intent.UserTypeChanged -> dispatch(Msg.UserTypeChanged(intent.type))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.EmailChanged -> copy(email = msg.email)
                is Msg.PasswordChanged -> copy(password = msg.password)
                is Msg.PasswordConfirmationChanged -> copy(passwordConfirmation = msg.passwordConfirmation)
                is Msg.UsernameChanged -> copy(username = msg.username)
                is Msg.RegisterStateChanged -> copy(registerState = msg.registerState)
                is Msg.EmailErrorChanged -> copy(emailError = msg.error)
                is Msg.PasswordConfirmationErrorChanged -> copy(passwordConfirmationError = msg.error)
                is Msg.PasswordErrorChanged -> copy(passwordError = msg.error)
                is Msg.UsernameErrorChanged -> copy(usernameError = msg.error)
                Msg.ClearErrors -> copy(
                    emailError = null,
                    passwordError = null,
                    passwordConfirmationError = null,
                    usernameError = null
                )

                is Msg.UserTypeChanged -> copy(usertype = msg.type)
            }
    }
}
