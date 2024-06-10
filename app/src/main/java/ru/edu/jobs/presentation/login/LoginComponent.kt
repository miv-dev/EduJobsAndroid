package ru.edu.jobs.presentation.login

import kotlinx.coroutines.flow.StateFlow

interface LoginComponent {
    val model: StateFlow<LoginStore.State>



    fun onChangeUsername(username: String)

    fun onChangePassword(password: String)

    fun onBackClick()

    fun onRegisterClick()

    fun submit()
}