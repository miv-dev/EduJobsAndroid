package ru.edu.jobs.presentation.my_services

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.domain.entity.ServiceShort
import ru.edu.jobs.domain.entity.toShort

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultMyServicesComponent @AssistedInject constructor(
    private val myServicesStoreFactory: MyServicesStoreFactory,
    @Assisted("onDetail") private val onDetail: (ServiceShort) -> Unit,
    @Assisted("onAdd") private val onAdd: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : MyServicesComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore { myServicesStoreFactory.create() }

    override val model: StateFlow<MyServicesStore.State> = store.stateFlow
    override fun  openDetail(service: Service)
    {
        onDetail(service.toShort())
    }

    override fun delete(service: Service) {
        store.accept(MyServicesStore.Intent.onDeleteService(service))
    }

    override fun update() {

    }

    override fun onAddClick() {
        onAdd()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onAdd") onAdd: () -> Unit,
            @Assisted("onDetail") onDetail: (ServiceShort) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext
        ): DefaultMyServicesComponent
    }
}