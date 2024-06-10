package ru.edu.jobs.presentation.profile.edit

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Errors
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.usecase.auth.GetUserUseCase
import ru.edu.jobs.domain.usecase.user.UpdateUserUseCase
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.profile.edit.EditProfileStore.Intent
import ru.edu.jobs.presentation.profile.edit.EditProfileStore.Label
import ru.edu.jobs.presentation.profile.edit.EditProfileStore.State
import javax.inject.Inject

interface EditProfileStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class OnChangeLastName(val lastName: String) : Intent
        data class OnChangeFirstName(val firstName: String) : Intent
        data class OnChangePhone(val phone: String) : Intent
        data class OnChangeType(val type: Role) : Intent
        data class OnChangeAvatar(val avatar: String) : Intent
        data class OnChangeUsername(val username: String) : Intent
        data class OnChangeEmail(val email: String) : Intent
        data object OnSaveClick : Intent
    }

    data class State(
        val oldUser: User? = null,
        val username: String = "",
        val usernameError: String? = null,
        val email: String = "",
        val emailError: String? = null,
        val type: Role? = null,
        val avatar: String? = null,
        val phone: String = "",
        val phoneError: String? = null,
        val firstName: String = "",
        val lastName: String = "",
        val updateState: UpdateState = UpdateState.Idle
    ) {
        sealed interface UpdateState {
            data object Idle : UpdateState
            data object Loading : UpdateState
            data object Success : UpdateState
            data class Error(val error: String) : UpdateState
        }
    }


    sealed interface Label {
        data object OnBackClicked : Label
    }
}

class EditProfileStoreFactory @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val storeFactory: StoreFactory
) {

    fun create(): EditProfileStore =
        object : EditProfileStore, Store<Intent, State, Label> by storeFactory.create(
            name = "EditProfileStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object LoadUser : Action
    }

    private sealed interface Msg {
        data class SetOldUser(val user: User) : Msg
        data class ChangeLastName(val lastName: String) : Msg
        data class ChangeFirstName(val firstName: String) : Msg
        data class ChangePhone(val phone: String) : Msg
        data class ChangeType(val type: Role?) : Msg
        data class ChangeAvatar(val avatar: String) : Msg
        data class ChangeUsername(val username: String) : Msg
        data class ChangeEmail(val email: String) : Msg
        data class ChangeEmailError(val error: String?) : Msg
        data class ChangeUsernameError(val error: String?) : Msg
        data class ChangePhoneError(val error: String?) : Msg
        data class ChangeUpdateState(val state: State.UpdateState) : Msg
        data object ClearErrors : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadUser)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.OnChangeLastName -> dispatch(Msg.ChangeLastName(intent.lastName))
                is Intent.OnChangeFirstName -> dispatch(Msg.ChangeFirstName(intent.firstName))
                is Intent.OnChangePhone -> dispatch(Msg.ChangePhone(intent.phone))
                is Intent.OnChangeType -> dispatch(Msg.ChangeType(intent.type))
                is Intent.OnChangeAvatar -> dispatch(Msg.ChangeAvatar(intent.avatar))
                is Intent.OnChangeUsername -> dispatch(Msg.ChangeUsername(intent.username))
                is Intent.OnChangeEmail -> dispatch(Msg.ChangeEmail(intent.email))
                Intent.OnSaveClick -> {
                    val state = getState()
                    scope.launch {
                        state.oldUser?.let {
                            dispatch(Msg.ChangeUpdateState(State.UpdateState.Loading))
                            val response = updateUserUseCase(
                                it.copy(
                                    profile = it.profile.copy(
                                        avatar = state.avatar,
                                        firstName = state.firstName,
                                        lastName = state.lastName,
                                        phone = state.phone
                                    ),
                                    role = state.type,
                                    username = state.username,
                                    email = state.email
                                )
                            )
                            when (response) {

                                is Response.Error -> {

                                    val error = response.error
                                    if (error is Errors.FieldsError) {
                                        error.fields.forEach { (key, value) ->
                                            when (key) {
                                                "username" -> dispatch(Msg.ChangeUsernameError(value))
                                                "email" -> dispatch(Msg.ChangeEmailError(value))
                                                "phone" -> dispatch(Msg.ChangePhoneError(value))

                                            }
                                        }
                                    }
                                    dispatch(
                                        Msg.ChangeUpdateState(
                                            State.UpdateState.Error(
                                                errorToString(
                                                    error
                                                )
                                            )
                                        )
                                    )
                                }

                                is Response.Success -> {
                                    dispatch(
                                        Msg.ChangeUpdateState(
                                            State.UpdateState.Success
                                        )
                                    )
                                    delay(1000)
                                    dispatch(
                                        Msg.ChangeUpdateState(
                                            State.UpdateState.Idle
                                        )
                                    )
                                }

                            }
                        }
                    }
                }
            }

        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                Action.LoadUser -> {
                    scope.launch {
                        val user = getUserUseCase() ?: return@launch
                        dispatch(Msg.SetOldUser(user))
                        dispatch(Msg.ChangeLastName(user.profile.lastName ?: ""))
                        dispatch(Msg.ChangeFirstName(user.profile.firstName ?: ""))
                        dispatch(Msg.ChangePhone(user.profile.phone ?: ""))
                        dispatch(Msg.ChangeType(user.role))
                        dispatch(Msg.ChangeAvatar(user.profile.avatar ?: ""))
                        dispatch(Msg.ChangeUsername(user.username))
                        dispatch(Msg.ChangeEmail(user.email))
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ChangeAvatar -> copy(avatar = msg.avatar)
                is Msg.ChangeEmail -> copy(email = msg.email)
                is Msg.ChangeFirstName -> copy(firstName = msg.firstName)
                is Msg.ChangeLastName -> copy(lastName = msg.lastName)
                is Msg.ChangePhone -> copy(phone = msg.phone)
                is Msg.ChangeType -> copy(type = msg.type)
                is Msg.ChangeUsername -> copy(username = msg.username)
                is Msg.SetOldUser -> copy(oldUser = msg.user)
                is Msg.ChangeEmailError -> copy(emailError = msg.error)
                is Msg.ChangePhoneError -> copy(phoneError = msg.error)
                is Msg.ChangeUsernameError -> copy(usernameError = msg.error)
                Msg.ClearErrors -> copy(emailError = null, phoneError = null, usernameError = null)
                is Msg.ChangeUpdateState -> copy(updateState = msg.state)
            }
    }
}
