package ru.edu.jobs.presentation.auth.unauthroized

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Response
import ru.edu.jobs.domain.usecase.welcome.GetWelcomeUseCase
import ru.edu.jobs.presentation.auth.unauthroized.UnauthorizedComponent.Child
import ru.edu.jobs.presentation.auth.unauthroized.UnauthorizedComponent.Model
import ru.edu.jobs.presentation.extensions.componentScope
import ru.edu.jobs.presentation.extensions.errorToString
import ru.edu.jobs.presentation.login.DefaultLoginComponent
import ru.edu.jobs.presentation.register.DefaultRegisterComponent

class DefaultUnauthorizedComponent @AssistedInject constructor(
    private val loginComponentFactory: DefaultLoginComponent.Factory,
    private val registerComponentFactory: DefaultRegisterComponent.Factory,
    private val getWelcomeUseCase: GetWelcomeUseCase,
    @Assisted("componentContext") componentContext: ComponentContext
) : UnauthorizedComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val scope = componentScope()
    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Welcome,
        handleBackButton = true,
        childFactory = ::child,
    )
    private val _model =
        MutableValue(Model(Model.WelcomeState.Loading))

    override val model: Value<Model>
        get() = _model

    init {
        scope.launch {
            val result = getWelcomeUseCase()
            when (result) {
                is Response.Error -> _model.value = Model(
                    Model.WelcomeState.Error(errorToString(result.error))
                )

                is Response.Success -> _model.value = Model(Model.WelcomeState.Content(result.data))
            }
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child {
        return when (config) {
            Config.Login -> Child.Login(
                loginComponentFactory.create(
                    onBack = { navigation.pop() },
                    onRegister = { navigation.replaceCurrent(Config.Register) },
                    componentContext = componentContext,
                )
            )

            Config.Register -> Child.Register(
                registerComponentFactory.create(
                    componentContext = componentContext,
                    onBackClicked = { navigation.pop() },
                    onLoginClicked = { navigation.replaceCurrent(Config.Login) },
                )
            )

            Config.Welcome -> Child.Welcome
        }
    }

    override fun register() {
        navigation.push(Config.Register)
    }

    override fun login() {
        navigation.push(Config.Login)
    }


    sealed interface Config : Parcelable {
        @Parcelize
        data object Login : Config

        @Parcelize
        data object Welcome : Config

        @Parcelize
        data object Register : Config
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultUnauthorizedComponent
    }
}