package ru.edu.jobs.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import ru.edu.jobs.presentation.auth.AuthContent
import ru.edu.jobs.presentation.theme.EduJobsTheme

@Composable
fun RootContent(component: RootComponent) {
    EduJobsTheme {
        Children(stack = component.stack) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Auth -> AuthContent(component = instance.component)
            }

        }
    }
}