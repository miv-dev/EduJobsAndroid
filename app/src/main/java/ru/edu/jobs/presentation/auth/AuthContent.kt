package ru.edu.jobs.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import ru.edu.jobs.presentation.auth.authorized.AuthorizedContent
import ru.edu.jobs.presentation.auth.unauthroized.UnauthorizedContent

@Composable
fun AuthContent(component: AuthComponent) {
    Children(stack = component.stack) { child ->
        when (val instance = child.instance) {
            is AuthComponent.Child.Authorized -> AuthorizedContent(component = instance.component)
            is AuthComponent.Child.UnAuthorized -> UnauthorizedContent(component = instance.component)
            AuthComponent.Child.Loading -> Scaffold{
                Column(Modifier.padding(it).padding(40.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator()
                }
            }
        }

    }

}