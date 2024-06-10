package ru.edu.jobs.presentation.profile.change_university

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.presentation.extensions.componentScope

class DefaultChangeUniversityComponent @AssistedInject constructor(
    private val changeUniversityStoreFactory: ChangeUniversityStoreFactory,
    @Assisted("onBack") private val onBack: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : ChangeUniversityComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { changeUniversityStoreFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    ChangeUniversityStore.Label.BackClick -> onBack()
                }
            }
        }
    }

    override fun onUniversitySelected(department: Department?) {
        store.accept(ChangeUniversityStore.Intent.UniversitySelected(department))
    }

    override fun onBackClicked() {
        store.accept(ChangeUniversityStore.Intent.BackClick)
    }

    override fun onSaveClicked() {
        store.accept(ChangeUniversityStore.Intent.SaveClick)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ChangeUniversityStore.State> = store.stateFlow

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBack") onBack: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultChangeUniversityComponent
    }
}