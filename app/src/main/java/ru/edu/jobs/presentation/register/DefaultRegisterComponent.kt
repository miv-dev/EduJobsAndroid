package ru.edu.jobs.presentation.register

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.presentation.extensions.componentScope

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRegisterComponent @AssistedInject constructor(
    private val registerStoreFactory: RegisterStoreFactory,
    @Assisted("onBackClicked") onBackClicked: () -> Unit,
    @Assisted("onLoginClicked") onLoginClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : RegisterComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { registerStoreFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    is RegisterStore.Label.ClickBack -> onBackClicked()
                    is RegisterStore.Label.ClickLogin -> onLoginClicked()
                }
            }
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onLoginClicked") onLoginClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultRegisterComponent
    }

    override val model: StateFlow<RegisterStore.State>
        get() = store.stateFlow

    override fun onChangeUsername(username: String) {
        store.accept(RegisterStore.Intent.UsernameChanged(username))
    }

    override fun onChangeEmail(username: String) {
        store.accept(RegisterStore.Intent.EmailChanged(username))
    }

    override fun onChangePassword(password: String) {
        store.accept(RegisterStore.Intent.PasswordChanged(password))
    }

    override fun onChangePassword2(password: String) {
        store.accept(RegisterStore.Intent.PasswordConfirmationChanged(password))
    }

    override fun onClickBack() {
        store.accept(RegisterStore.Intent.ClickBack)
    }

    override fun onClickLogin() {
        store.accept(RegisterStore.Intent.ClickLogin)
    }

    override fun submit() {
        store.accept(RegisterStore.Intent.RegisterClicked)
    }

    override fun onChangeUserType(role: Role) {
        store.accept(RegisterStore.Intent.UserTypeChanged(role))
    }
}