package ru.edu.jobs.presentation.add_service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.Role
import ru.edu.jobs.ext.DateUtils
import ru.edu.jobs.presentation.components.UploadButton
import ru.edu.jobs.presentation.components.UploadButtonState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceContent(component: AddServiceComponent) {
    val model by component.model.collectAsState()
    val dateState = rememberDatePickerState()
    val millisToLocalDate = dateState.selectedDateMillis?.let {
        DateUtils().convertMillisToLocalDate(it)
    }
    val dateToString = millisToLocalDate?.let {
        DateUtils().dateToString(millisToLocalDate)
    } ?: "Введите дату"

    LaunchedEffect(millisToLocalDate) {
        component.onDeadlineChanged(millisToLocalDate)
    }

    LaunchedEffect(model.deadline) {
        model.deadline?.let {
            dateState.setSelection(it.toEpochDay() * 86400000L)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            ElevatedCard {

                DatePicker(state = dateState)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { showDatePicker = false }) {
                        Text(text = "OK")
                    }
                }

            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                when (model.openReason) {
                    OpenReason.AddService -> Text(text = stringResource(R.string.add_service))
                    is OpenReason.EditService -> Text(text = stringResource(R.string.edit_service))
                }

            }, navigationIcon = {
                IconButton(onClick = component::onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            })
        }, bottomBar = {
            UploadButton(
                modifier = Modifier.padding(16.dp),
                onClick = component::onCreateClick,
                label = when (model.openReason) {
                    OpenReason.AddService -> stringResource(R.string.create)
                    is OpenReason.EditService -> stringResource(R.string.save)
                },
                state = when (model.sendState) {
                    is AddServiceStore.State.SendState.Error -> UploadButtonState.Error
                   AddServiceStore.State.SendState.Idle -> UploadButtonState.Idle
                   AddServiceStore.State.SendState.Loading -> UploadButtonState.Loading
                   AddServiceStore.State.SendState.Success -> UploadButtonState.Success
                },
            )
        }
    ) { values ->
        Column(Modifier.padding(values)) {
            val user = model.user
            if (user == null) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            label = { Text(text = stringResource(R.string.name)) },
                            value = model.name,
                            isError = model.nameError != null,
                            supportingText = { model.nameError?.let { Text(text = it) } },
                            onValueChange = component::onNameChanged,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            label = { Text(text = stringResource(R.string.description)) },
                            value = model.description,
                            isError = model.descriptionError != null,
                            supportingText = { model.descriptionError?.let { Text(text = it) } },
                            onValueChange = component::onDescriptionChanged,
                            minLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )


                        if (user.role == Role.Employer) {
                            ListItem(
                                overlineContent = { Text(text = stringResource(R.string.deadline)) },
                                headlineContent = {
                                    Text(text = dateToString)
                                },
                                trailingContent = {
                                    Button(onClick = { showDatePicker = true }) {
                                        Text(text = stringResource(R.string.pick_date))
                                    }
                                },
                            )
                        }

                    }
                }
            }


        }
    }

}