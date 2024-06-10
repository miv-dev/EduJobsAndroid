package ru.edu.jobs.presentation.register

import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Role

interface RegisterComponent {

    val model: StateFlow<RegisterStore.State>

    fun onChangeUsername(username: String)
    fun onChangeEmail(username: String)

    fun onChangePassword(password: String)
    fun onChangePassword2(password: String)

    fun onClickBack()
    fun onClickLogin()

    fun submit()
    fun onChangeUserType(role: Role)
}