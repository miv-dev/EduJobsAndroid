package ru.edu.jobs.presentation.auth

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.edu.jobs.presentation.auth.authorized.AuthorizedComponent
import ru.edu.jobs.presentation.auth.unauthroized.UnauthorizedComponent

interface AuthComponent {

    val stack: Value<ChildStack<*, Child>>



    sealed interface Child {
        data class Authorized(val component: AuthorizedComponent) : Child
        data class UnAuthorized(val component: UnauthorizedComponent) : Child
        data object Loading : Child
    }
}