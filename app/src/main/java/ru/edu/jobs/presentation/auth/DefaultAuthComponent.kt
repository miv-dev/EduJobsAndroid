package ru.edu.jobs.presentation.auth

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.AuthState
import ru.edu.jobs.domain.usecase.auth.GetAuthStateUseCase
import ru.edu.jobs.presentation.auth.authorized.DefaultAuthorizedComponent
import ru.edu.jobs.presentation.auth.unauthroized.DefaultUnauthorizedComponent
import ru.edu.jobs.presentation.extensions.componentScope

class DefaultAuthComponent @AssistedInject constructor(
    private val unauthorizedComponentFactory: DefaultUnauthorizedComponent.Factory,
    private val authorizedComponentFactory: DefaultAuthorizedComponent.Factory,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    @Assisted("componentContext") componentContext: ComponentContext
) : ComponentContext by componentContext, AuthComponent {

    private val navigation = StackNavigation<Config>()

    private val scope = componentScope()

    init {
        scope.launch {
            getAuthStateUseCase().collect {
                val config = when (it) {
                    is AuthState.Authenticated -> Config.Authorized
                    AuthState.Loading -> Config.Loading
                    AuthState.Unauthenticated -> Config.UnAuthorized
                }
                navigation.replaceAll(config)
            }

        }
    }


    override val stack: Value<ChildStack<*, AuthComponent.Child>> = childStack(
            source = navigation,
            initialConfiguration = Config.Loading,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): AuthComponent.Child {
        return when (config) {
            Config.Authorized -> AuthComponent.Child.Authorized(authorizedComponentFactory.create(componentContext))
            Config.UnAuthorized -> AuthComponent.Child.UnAuthorized(unauthorizedComponentFactory.create(componentContext))
            Config.Loading -> AuthComponent.Child.Loading
        }
    }

    sealed interface Config : Parcelable {
        @Parcelize
        data object Authorized : Config

        @Parcelize
        data object Loading : Config

        @Parcelize
        data object UnAuthorized : Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultAuthComponent
    }

}