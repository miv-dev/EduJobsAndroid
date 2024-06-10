package ru.edu.jobs.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.edu.jobs.presentation.auth.AuthComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>


    sealed interface Child {
        data class Auth(val component: AuthComponent): Child


    }

}