package ru.edu.jobs.presentation.auth.authorized

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.edu.jobs.presentation.add_service.DefaultAddServiceComponent
import ru.edu.jobs.presentation.add_service.OpenReason
import ru.edu.jobs.presentation.auth.authorized.AuthorizedComponent.Child
import ru.edu.jobs.presentation.detail.DefaultDetailComponent
import ru.edu.jobs.presentation.detail.OpenBy
import ru.edu.jobs.presentation.favourite.DefaultFavouriteComponent
import ru.edu.jobs.presentation.main.DefaultMainComponent
import ru.edu.jobs.presentation.my_services.DefaultMyServicesComponent
import ru.edu.jobs.presentation.other.DefaultOtherComponent
import ru.edu.jobs.presentation.profile.route.DefaultProfileRouterComponent
import ru.edu.jobs.presentation.search.DefaultSearchComponent

class DefaultAuthorizedComponent @AssistedInject constructor(
    private val profileRouterComponentFactory: DefaultProfileRouterComponent.Factory,
    private val otherComponentFactory: DefaultOtherComponent.Factory,
    private val searchComponentFactory: DefaultSearchComponent.Factory,
    private val favouriteComponentFactory: DefaultFavouriteComponent.Factory,
    private val myServicesComponentFactory: DefaultMyServicesComponent.Factory,
    private val detailComponentFactory: DefaultDetailComponent.Factory,
    private val mainComponentFactory: DefaultMainComponent.Factory,
    private val addServiceComponentFactory: DefaultAddServiceComponent.Factory,
    @Assisted("componentContext") componentContext: ComponentContext
) : AuthorizedComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultAuthorizedComponent
    }

    sealed interface Config : Parcelable {
        @Parcelize
        data object Main : Config

        @Parcelize
        data object Favourite : Config

        @Parcelize
        data object MyServices : Config

        @Parcelize
        data object Profile : Config

        @Parcelize
        data object Other : Config


        @Parcelize
        data class AddService(
            val openReason: OpenReason
        ) : Config

        @Parcelize
        data class Search(val query: String?) : Config


        @Parcelize
        data class Detail(
            val openBy: OpenBy,
        ) : Config
    }

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::child,

        )


    override fun navigateToMainTab() {
        navigation.bringToFront(Config.Main)

    }

    override fun navigateToProfileTab() {
        navigation.bringToFront(Config.Profile)

    }

    override fun navigateToFavouriteTab() {
        navigation.bringToFront(Config.Favourite)
    }


    override fun navigateToMyServicesTab() {
        navigation.bringToFront(Config.MyServices)
    }


    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): Child {
        return when (config) {
            Config.Favourite -> Child.Favourite(
                favouriteComponentFactory.create(
                    onServiceItemClicked = {
                        navigation.push(
                            Config.Detail(
                                OpenBy.Viewer(it.id)
                            )
                        )
                    },
                    componentContext = componentContext
                )
            )

            Config.Main -> Child.Main(
                mainComponentFactory.create(
                    onDetail = { service ->
                        navigation.push(
                            Config.Detail(
                                OpenBy.Viewer(service.id)
                            )
                        )
                    },
                    onSearch = { query ->
                        navigation.push(Config.Search(query))
                    },
                    componentContext = componentContext
                )
            )

            Config.Profile -> Child.Profile(
                profileRouterComponentFactory.create(
                    componentContext = componentContext
                )
            )

            is Config.Detail -> Child.Detail(
                detailComponentFactory.create(
                    onBack = navigation::pop,
                    openBy = config.openBy,
                    onEdit = { navigation.push(Config.AddService(OpenReason.EditService(it))) },
                    componentContext = componentContext,
                )
            )

            Config.MyServices -> Child.MyServices(
                myServicesComponentFactory.create(
                    componentContext = componentContext,
                    onDetail = { service ->
                        navigation.push(Config.Detail(OpenBy.Owner(service.id)))
                    },
                    onAdd = { navigation.push(Config.AddService(OpenReason.AddService)) }
                )
            )

            is Config.AddService -> Child.AddService(
                addServiceComponentFactory.create(
                    componentContext = componentContext,
                    onBack = { navigation.pop() },
                    openReason = config.openReason
                )
            )


            is Config.Search -> Child.Search(
                searchComponentFactory.create(
                    query = config.query,
                    onBack = { navigation.pop() },
                    onServiceClick = { service ->
                        navigation.push(Config.Detail( OpenBy.Viewer(service.id)))
                    },
                    componentContext = componentContext
                )
            )

            Config.Other -> Child.Other(
                otherComponentFactory.create(
                    onDetail = { service ->
                        navigation.push(
                            Config.Detail(
                                OpenBy.Parsed(service.uuid)
                            )
                        )
                    },
                    componentContext = componentContext
                )
            )
        }
    }

    override fun navigateToOtherTab() {
        navigation.bringToFront(Config.Other)
    }
}