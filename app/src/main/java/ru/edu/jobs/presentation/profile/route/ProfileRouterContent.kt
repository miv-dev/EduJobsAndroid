package ru.edu.jobs.presentation.profile.route

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import ru.edu.jobs.presentation.profile.ProfileContent
import ru.edu.jobs.presentation.profile.change_university.ChangeUniversityContent
import ru.edu.jobs.presentation.profile.edit.EditProfileContent

@Composable
fun ProfileRouterContent(component: ProfileRouterComponent) {

    Children(stack = component.stack) { child ->
        when (val instance = child.instance) {
            is ProfileRouterComponent.Child.Profile -> ProfileContent(component = instance.component)
            is ProfileRouterComponent.Child.Edit -> EditProfileContent(component = instance.component)
            is ProfileRouterComponent.Child.ChangeUniversity -> ChangeUniversityContent(component = instance.component)
        }

    }

}