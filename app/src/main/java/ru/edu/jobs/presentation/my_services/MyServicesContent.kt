package ru.edu.jobs.presentation.my_services

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Service

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyServicesContent(component: MyServicesComponent) {
    val model by component.model.collectAsState()
    val typography = MaterialTheme.typography
    var showAlertDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    if (showAlertDialog && selectedService != null) {
        AlertDialog(onDismissRequest = { showAlertDialog = false }) {
            Card {
                Column(Modifier.padding(24.dp)) {
                    Text(text = stringResource(R.string.warning), style = typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(
                            R.string.are_you_sure_you_want_to_delete,
                            selectedService!!.name
                        ),
                        style = typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            showAlertDialog = false
                            selectedService = null
                        }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            showAlertDialog = false
                            component.delete(selectedService!!)
                            selectedService = null
                        }) {
                            Text(text = stringResource(R.string.yes))
                        }

                    }
                }
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = component::onAddClick) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->

        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (model.servicesState is LoadState.Updating) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            when (val state = model.servicesState) {
                is LoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error ${state.error}")
                            Button(onClick = { /*TODO*/ }) {
                                Icon(Icons.Rounded.Update, contentDescription = "Reload")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Reload")
                            }
                        }
                    }
                }

                LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Success -> ServiceList(
                    services = state.data,
                    deleteState = model.deleteState,
                    onItemClick = component::openDetail,
                    onDeleteItemClick = {
                        selectedService = it
                        showAlertDialog = true
                    }
                )

                is LoadState.Updating -> ServiceList(
                    services = state.data,
                    onItemClick = component::openDetail,
                    onDeleteItemClick = {
                        selectedService = it
                        showAlertDialog = true
                    },
                    deleteState = model.deleteState
                )
            }

        }
    }
}

@Composable
private fun ServiceList(
    services: List<Service>,
    onItemClick: (service: Service) -> Unit,
    onDeleteItemClick: (service: Service) -> Unit,
    deleteState: MyServicesStore.State.DeleteState?
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(services) { service ->
            val itemVisibility = remember {
                Animatable(1f)
            }
            LaunchedEffect(deleteState) {
                if (deleteState is MyServicesStore.State.DeleteState.Deleted && deleteState.id == service.id) {
                    itemVisibility.animateTo(targetValue = 0f, animationSpec = tween(1000))
                }
            }
            Card(Modifier.alpha(itemVisibility.value)) {
                Column(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Text(text = service.name)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { onDeleteItemClick(service) }) {
                            deleteState?.let {
                                if (it.id == service.id) {
                                    when (it) {
                                        is MyServicesStore.State.DeleteState.Deleted -> Icon(
                                            Icons.Rounded.Done,
                                            contentDescription = "Deleted"
                                        )

                                        is MyServicesStore.State.DeleteState.Deleting -> {
                                            CircularProgressIndicator()
                                        }

                                        is MyServicesStore.State.DeleteState.Error -> Icon(
                                            Icons.Rounded.Error,
                                            contentDescription = "Delete Error"
                                        )
                                    }
                                } else {
                                    Text(text = stringResource(R.string.delete))
                                }

                            } ?: Text(text = stringResource(R.string.delete))

                        }
                        Button(onClick = { onItemClick(service) }) {
                            Text(text = stringResource(R.string.view))
                        }
                    }
                }

            }
        }
    }
}