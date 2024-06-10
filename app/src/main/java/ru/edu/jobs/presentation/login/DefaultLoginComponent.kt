package ru.edu.jobs.presentation.login

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
import ru.edu.jobs.presentation.extensions.componentScope


class DefaultLoginComponent @AssistedInject constructor(
    private val storeFactory: LoginStoreFactory,
    @Assisted("onBack") onBack: () -> Unit,
    @Assisted("onRegister") onRegister: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : LoginComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    LoginStore.Label.OnBackClicked -> onBack()
                    LoginStore.Label.OnRegisterClicked -> onRegister()
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<LoginStore.State>
        get() = store.stateFlow

    override fun onChangeUsername(username: String) {
        store.accept(LoginStore.Intent.UsernameChanged(username))
    }

    override fun onChangePassword(password: String) {
        store.accept(LoginStore.Intent.PasswordChanged(password))
    }

    override fun onBackClick() {
        store.accept(LoginStore.Intent.OnBackClicked)
    }

    override fun onRegisterClick() {
        store.accept(LoginStore.Intent.OnRegisterClicked)

    }

    override fun submit() {
        store.accept(LoginStore.Intent.OnSubmitClicked)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBack") onBack: () -> Unit,
            @Assisted("onRegister") onRegister: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultLoginComponent
    }
}