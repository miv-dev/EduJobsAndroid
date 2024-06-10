package ru.edu.jobs.presentation.profile.change_university

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.Department
import ru.edu.jobs.presentation.components.UploadButton
import ru.edu.jobs.presentation.components.UploadButtonState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeUniversityContent(component: ChangeUniversityComponent) {
    val model by component.model.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = component::onBackClicked) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            })
        },
        bottomBar = {
            UploadButton(
                state = when (model.updateState) {
                    is ChangeUniversityStore.State.UpdateState.Error -> UploadButtonState.Error
                    ChangeUniversityStore.State.UpdateState.Idle -> UploadButtonState.Idle
                    ChangeUniversityStore.State.UpdateState.Loading -> UploadButtonState.Loading
                    ChangeUniversityStore.State.UpdateState.Success -> UploadButtonState.Success
                },
                onClick = component::onSaveClicked,
                label = stringResource(R.string.save),
                modifier = Modifier.padding(12.dp)
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            ListItem(
                overlineContent = { Text(text = stringResource(R.string.current_department)) },
                headlineContent = {
                    val user = model.user
                    if (user?.department != null) {
                        Text(text = stringResource(R.string.department, user.department.name))
                    } else {
                        Text(text = stringResource(R.string.department_not_selected))
                    }
                },
                supportingContent = {
                    AnimatedVisibility(model.selectedDepartment != model.user?.department) {
                        Text(
                            text = stringResource(
                                R.string.new_university,
                                model.selectedDepartment?.name
                                    ?: stringResource(R.string.not_selected)
                            )
                        )
                    }
                },
                trailingContent = {
                    AnimatedVisibility(model.selectedDepartment != model.user?.department) {
                        IconButton(onClick = { component.onUniversitySelected(model.user?.department) }) {
                            Icon(
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = "Selected",
                            )
                        }
                    }
                }
            )
            when (val state = model.departmentsState) {
                is ChangeUniversityStore.State.DepartmentsState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error)
                    }
                }

                is ChangeUniversityStore.State.DepartmentsState.Loaded -> UniversityList(
                    universities = state.departments,
                    selectedDepartment = model.selectedDepartment,
                    onUniversitySelected = component::onUniversitySelected
                )

                ChangeUniversityStore.State.DepartmentsState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityList(
    universities: List<Department>,
    selectedDepartment: Department?,
    onUniversitySelected: (Department?) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(onClick = { onUniversitySelected(null) }) {
                Row(Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.not_selected))
                    Spacer(Modifier.weight(1f))
                    if (selectedDepartment == null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                        )
                    }
                }
            }
        }
        items(universities) { university ->
            UniversityItem(
                department = university,
                isSelected = university == selectedDepartment,
                onUniversitySelected = onUniversitySelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityItem(
    department: Department,
    isSelected: Boolean,
    onUniversitySelected: (Department) -> Unit
) {
    ElevatedCard(onClick = { onUniversitySelected(department) }) {
        Row(Modifier.padding(16.dp)) {
            Text(text = department.name)
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                )
            }
        }
    }
}