package ru.edu.jobs.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import ru.edu.jobs.EduJobsApp
import ru.edu.jobs.presentation.root.DefaultRootComponent
import ru.edu.jobs.presentation.root.RootContent
import ru.edu.jobs.presentation.theme.EduJobsTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: DefaultRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as EduJobsApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)
        val rootComponent = rootComponentFactory.create(defaultComponentContext())
        setContent {
            EduJobsTheme {
                RootContent(component = rootComponent)
            }
        }
    }
}
