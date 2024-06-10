package ru.edu.jobs.presentation.auth.unauthroized

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.edu.jobs.domain.entity.Welcome
import ru.edu.jobs.presentation.login.LoginComponent
import ru.edu.jobs.presentation.register.RegisterComponent

interface UnauthorizedComponent {

    val stack: Value<ChildStack<*, Child>>
    val model: Value<Model>


    data class Model(
        val welcomeState: WelcomeState
    ){
        sealed interface WelcomeState{
            data object Loading : WelcomeState
            data class Content(val welcome: Welcome) : WelcomeState
            data class Error(val error: String) : WelcomeState
        }
    }

    fun register()

    fun login()

    sealed interface Child {
        data class Login(val component: LoginComponent) : Child
        data class Register(val component: RegisterComponent) : Child
        data object Welcome : Child
    }

}