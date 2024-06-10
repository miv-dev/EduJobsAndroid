package ru.edu.jobs.presentation.auth.authorized

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.edu.jobs.R
import ru.edu.jobs.ext.Keyboard
import ru.edu.jobs.ext.keyboardAsState
import ru.edu.jobs.presentation.add_service.AddServiceContent
import ru.edu.jobs.presentation.detail.DetailContent
import ru.edu.jobs.presentation.favourite.FavouriteContent
import ru.edu.jobs.presentation.main.MainContent
import ru.edu.jobs.presentation.my_services.MyServicesContent
import ru.edu.jobs.presentation.other.OtherServicesContent
import ru.edu.jobs.presentation.profile.route.ProfileRouterContent
import ru.edu.jobs.presentation.search.SearchContent


@Composable
fun AuthorizedContent(component: AuthorizedComponent) {

    val stack by component.stack.subscribeAsState()
    val activeChild = stack.active.instance
    val isKeyboardOpen by keyboardAsState()
    val visible =
        activeChild is AuthorizedComponent.Child.Main || activeChild is AuthorizedComponent.Child.Other || activeChild is AuthorizedComponent.Child.Favourite || activeChild is AuthorizedComponent.Child.Profile || activeChild is AuthorizedComponent.Child.MyServices
    Scaffold(bottomBar = {
        AnimatedVisibility(
            visible = visible && (isKeyboardOpen == Keyboard.Closed),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NavigationBar {
                NavigationBarItem(
                    selected = activeChild is AuthorizedComponent.Child.Main,
                    onClick = component::navigateToMainTab,
                    icon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = stringResource(R.string.main)
                        )
                    },
                    label = { Text(text = stringResource(R.string.main)) },
                    alwaysShowLabel = false
                )

                NavigationBarItem(
                    selected = activeChild is AuthorizedComponent.Child.Other,
                    onClick = component::navigateToOtherTab,
                    icon = {
                        Icon(
                            Icons.Outlined.TravelExplore, contentDescription = stringResource(
                                R.string.other
                            )
                        )
                    },
                    label = { Text(text = stringResource(R.string.other)) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = activeChild is AuthorizedComponent.Child.Favourite,
                    onClick = component::navigateToFavouriteTab,
                    icon = {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.favourite)
                        )
                    },
                    label = { Text(text = stringResource(R.string.favourite)) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = activeChild is AuthorizedComponent.Child.MyServices,
                    onClick = component::navigateToMyServicesTab,
                    icon = {
                        Icon(
                            Icons.Outlined.HomeRepairService,
                            contentDescription = stringResource(R.string.my_services)
                        )
                    },
                    label = { Text(text =  stringResource(R.string.my_services)) },
                    alwaysShowLabel = false
                )
                NavigationBarItem(
                    selected = activeChild is AuthorizedComponent.Child.Profile,
                    onClick = component::navigateToProfileTab,
                    icon = {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(R.string.profile)
                        )
                    },
                    label = { Text(text =  stringResource(R.string.profile)) },
                    alwaysShowLabel = false
                )
            }

        }
    }) {
        Column(Modifier.padding(it)) {
            Children(stack = component.stack) { child ->
                when (val instance = child.instance) {
                    is AuthorizedComponent.Child.Favourite -> FavouriteContent(component = instance.component)
                    is AuthorizedComponent.Child.Main -> MainContent(component = instance.component)
                    is AuthorizedComponent.Child.Profile -> ProfileRouterContent(component = instance.component)
                    is AuthorizedComponent.Child.Detail -> DetailContent(component = instance.component)
                    is AuthorizedComponent.Child.MyServices -> MyServicesContent(component = instance.component)
                    is AuthorizedComponent.Child.AddService -> AddServiceContent(component = instance.component)
                    is AuthorizedComponent.Child.Search -> SearchContent(component = instance.component)
                    is AuthorizedComponent.Child.Other -> OtherServicesContent(component = instance.component)
                }


            }

        }
    }

}