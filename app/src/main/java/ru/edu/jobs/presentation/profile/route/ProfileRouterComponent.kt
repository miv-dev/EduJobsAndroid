package ru.edu.jobs.presentation.profile.route

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.edu.jobs.presentation.profile.ProfileComponent
import ru.edu.jobs.presentation.profile.change_university.ChangeUniversityComponent
import ru.edu.jobs.presentation.profile.edit.EditProfileComponent

interface ProfileRouterComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Edit(val component: EditProfileComponent) : Child
        data class ChangeUniversity(val component: ChangeUniversityComponent) : Child
        data class Profile(val component: ProfileComponent) : Child
    }
}