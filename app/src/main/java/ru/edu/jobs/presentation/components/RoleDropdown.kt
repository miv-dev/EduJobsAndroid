package ru.edu.jobs.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    role: Role?,
    onChangeUserType: (Role) -> Unit,
    supportingText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        val text = when (role) {
            Role.Specialist -> stringResource(R.string.specialist)
            Role.Employer -> stringResource(R.string.employer)
            Role.Admin -> stringResource(R.string.admin)
            null -> stringResource(R.string.select_role)
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = text,
            leadingIcon = { Icon(Icons.Rounded.Group, contentDescription = "User Role") },
            onValueChange = {},
            readOnly = true,
            supportingText = { supportingText?.let { Text(text = it) } },
            label = { Text(text = stringResource(R.string.role)) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = !expanded
                    onChangeUserType(Role.Specialist)
                },
                text = {
                    Text(text = stringResource(R.string.specialist))
                },
            )
            DropdownMenuItem(
                onClick = {
                    expanded = !expanded
                    onChangeUserType(Role.Employer)
                },
                text = {
                    Text(text = stringResource(R.string.employer))
                },
            )

        }
    }
}