package ru.edu.jobs.presentation.auth.authorized

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.edu.jobs.domain.entity.Welcome
import ru.edu.jobs.presentation.add_service.AddServiceComponent
import ru.edu.jobs.presentation.detail.DetailComponent
import ru.edu.jobs.presentation.favourite.FavouriteComponent
import ru.edu.jobs.presentation.main.MainComponent
import ru.edu.jobs.presentation.my_services.MyServicesComponent
import ru.edu.jobs.presentation.other.OtherComponent
import ru.edu.jobs.presentation.profile.route.ProfileRouterComponent
import ru.edu.jobs.presentation.search.SearchComponent

interface AuthorizedComponent {

    val stack: Value<ChildStack<*, Child>>


    fun navigateToMainTab()
    fun navigateToOtherTab()
    fun navigateToProfileTab()
    fun navigateToFavouriteTab()
    fun navigateToMyServicesTab()


    sealed interface Child {
        data class Detail(val component: DetailComponent) : Child
        data class AddService(val component: AddServiceComponent) : Child
        data class Main(val component: MainComponent) : Child
        data class Other(val component: OtherComponent) : Child
        data class MyServices(val component: MyServicesComponent) : Child
        data class Favourite(val component: FavouriteComponent) : Child
        data class Search(val component: SearchComponent) : Child
        data class Profile(val component: ProfileRouterComponent) : Child
    }

}