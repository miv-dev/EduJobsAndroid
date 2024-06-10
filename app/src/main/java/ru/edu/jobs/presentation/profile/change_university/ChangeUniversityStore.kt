package ru.edu.jobs.presentation.profile.change_university

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.entity.User
import ru.edu.jobs.domain.usecase.auth.GetUserUseCase
import ru.edu.jobs.domain.usecase.user.GetUniversitiesUseCase
import ru.edu.jobs.domain.usecase.user.UpdateUserUseCase
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.profile.change_university.ChangeUniversityStore.Intent
import ru.edu.jobs.presentation.profile.change_university.ChangeUniversityStore.Label
import ru.edu.jobs.presentation.profile.change_university.ChangeUniversityStore.State
import javax.inject.Inject

interface ChangeUniversityStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class UniversitySelected(val department: Department?) : Intent
        data object SaveClick : Intent
        data object BackClick : Intent
    }

    data class State(
        val user: User?,
        val departmentsState: DepartmentsState,
        val selectedDepartment: Department? = null,
        val updateState: UpdateState = UpdateState.Idle
    ) {
        sealed interface UpdateState {
            data object Idle : UpdateState
            data object Loading : UpdateState
            data object Success : UpdateState
            data class Error(val error: String) : UpdateState
        }

        sealed interface DepartmentsState {
            data object Loading : DepartmentsState
            data class Loaded(val departments: List<Department>) : DepartmentsState
            data class Error(val error: String) : DepartmentsState
        }
    }

    sealed interface Label {
        data object BackClick : Label
    }
}

class ChangeUniversityStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getUserUseCase: GetUserUseCase,
    private val getUniversitiesUseCase: GetUniversitiesUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) {

    fun create(): ChangeUniversityStore =
        object : ChangeUniversityStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ChangeUniversityStore",
            initialState = State(null, State.DepartmentsState.Loading, null),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data object LoadUser : Action
        data object LoadUniversities : Action
    }

    private sealed interface Msg {
        data class UserLoaded(val user: User) : Msg
        data class ChangeSelectedUniversity(val department: Department?) : Msg
        data class ChangeState(val state: State.DepartmentsState) : Msg
        data class ChangeUpdateState(val state: State.UpdateState) : Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.LoadUser)
            dispatch(Action.LoadUniversities)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.UniversitySelected -> dispatch(Msg.ChangeSelectedUniversity(intent.department))

                Intent.BackClick -> publish(Label.BackClick)
                Intent.SaveClick -> {
                    scope.launch {

                        val state = getState()
                        val user = state.user?.copy(department = state.selectedDepartment)
                        if (user != null) {
                            dispatch(Msg.ChangeUpdateState(State.UpdateState.Loading))
                            when (val response = updateUserUseCase(user)) {

                                is Response.Error -> {

                                    val error = errorToString(response.error)
                                    dispatch(
                                        Msg.ChangeUpdateState(State.UpdateState.Error(error))
                                    )
                                }

                                is Response.Success -> {
                                    dispatch(
                                        Msg.ChangeUpdateState(
                                            State.UpdateState.Success
                                        )
                                    )
                                    publish(Label.BackClick)

                                }

                            }
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.LoadUser -> {
                    scope.launch {
                        val user = getUserUseCase()
                        if (user == null) {
                            publish(Label.BackClick)
                        } else {
                            dispatch(Msg.ChangeSelectedUniversity(user.department))
                            dispatch(Msg.UserLoaded(user))
                        }
                    }
                }

                Action.LoadUniversities -> {
                    scope.launch {
                        dispatch(Msg.ChangeState(State.DepartmentsState.Loading))
                        when (val response = getUniversitiesUseCase()) {
                            is Response.Error -> {
                                dispatch(
                                    Msg.ChangeState(
                                        State.DepartmentsState.Error(errorToString(response.error))
                                    )
                                )
                            }

                            is Response.Success -> {
                                val universities = response.data
                                dispatch(Msg.ChangeState(State.DepartmentsState.Loaded(universities)))
                            }
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.UserLoaded -> copy(user = msg.user)
                is Msg.ChangeState -> copy(departmentsState = msg.state)
                is Msg.ChangeSelectedUniversity -> copy(selectedDepartment = msg.department)
                is Msg.ChangeUpdateState -> copy(updateState = msg.state)
            }
    }
}
