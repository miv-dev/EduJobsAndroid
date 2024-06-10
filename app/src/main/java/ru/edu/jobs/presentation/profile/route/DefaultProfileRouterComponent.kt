package ru.edu.jobs.presentation.profile.route

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.edu.jobs.presentation.profile.DefaultProfileComponent
import ru.edu.jobs.presentation.profile.change_university.DefaultChangeUniversityComponent
import ru.edu.jobs.presentation.profile.edit.DefaultEditProfileComponent
import ru.edu.jobs.presentation.profile.route.ProfileRouterComponent.Child

class DefaultProfileRouterComponent @AssistedInject constructor(
    private val editProfileComponentFactory: DefaultEditProfileComponent.Factory,
    private val profileComponentFactory: DefaultProfileComponent.Factory,
    private val changeUniversityComponentFactory: DefaultChangeUniversityComponent.Factory,
    @Assisted("componentContext") componentContext: ComponentContext
) : ProfileRouterComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    sealed interface Config : Parcelable {
        @Parcelize
        data object Edit : Config

        @Parcelize
        data object ChangeUniversity : Config

        @Parcelize
        data object Profile : Config
    }

    override val stack: Value<ChildStack<*, Child>> = childStack(

        source = navigation,
        initialConfiguration = Config.Profile,
        handleBackButton = true,
        persistent = false,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): Child {
        return when (config) {
            is Config.Edit -> Child.Edit(
                editProfileComponentFactory.create(
                    componentContext = componentContext,
                    onBack = { navigation.pop() }
                )
            )

            is Config.ChangeUniversity -> Child.ChangeUniversity(
                changeUniversityComponentFactory.create(
                    onBack = { navigation.pop() },
                    componentContext = componentContext,
                )
            )

            is Config.Profile -> Child.Profile(
                profileComponentFactory.create(
                    onEditProfile = { navigation.push(Config.Edit) },
                    onChangeUniversity = { navigation.push(Config.ChangeUniversity) },
                    componentContext = componentContext

                )
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultProfileRouterComponent
    }


}