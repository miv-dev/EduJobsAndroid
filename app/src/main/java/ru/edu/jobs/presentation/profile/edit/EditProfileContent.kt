package ru.edu.jobs.presentation.profile.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.edu.jobs.R
import ru.edu.jobs.presentation.components.RoleDropdown
import ru.edu.jobs.presentation.profile.edit.EditProfileStore.State.UpdateState

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(component: EditProfileComponent) {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                component.onChangeAvatar(uri.toString())
            }
        }
    val focusManager = LocalFocusManager.current

    val model by component.model.collectAsState()
    val theme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val hasError = model.updateState is UpdateState.Error
    var showAlertDialog by remember { mutableStateOf(false) }
    if (showAlertDialog) {
        AlertDialog(onDismissRequest = { showAlertDialog = false }) {
            Card {
                Column(Modifier.padding(24.dp)) {
                    Text(text = stringResource(R.string.warning), style = typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.changing_the_role_will_clear_all_viewed_and_favourite_are_you_sure_you_want_to_continue), style = typography.bodyMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showAlertDialog = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            showAlertDialog = false
                            component.onSaveClick()
                        }) {
                            Text(text = stringResource(R.string.text_continue))
                        }

                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.edit_profile))
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClick) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        bottomBar = {
            val btnErrorColors = ButtonDefaults.buttonColors(theme.errorContainer, theme.error)

            Button(
                modifier = Modifier.padding(12.dp),
                onClick = {
                    if (model.oldUser?.role != model.type) {
                        showAlertDialog = true
                    } else {
                        component.onSaveClick()
                    }
                },
                colors = if (hasError) btnErrorColors else ButtonDefaults.buttonColors()
            ) {
                Spacer(modifier = Modifier.weight(1f))

                this.AnimatedVisibility(model.updateState is UpdateState.Idle) {
                    Text(text = stringResource(R.string.save))
                    Spacer(modifier = Modifier.width(16.dp))

                }

                AnimatedContent(model.updateState, label = "button icon") {
                    when (it) {
                        is UpdateState.Error -> Icon(
                            Icons.Rounded.Error,
                            contentDescription = "Error",
                            tint = theme.error
                        )

                        UpdateState.Idle -> Icon(
                            Icons.Rounded.Save,
                            contentDescription = "Submit"
                        )

                        UpdateState.Loading -> {
                            CircularProgressIndicator(
                                Modifier.size(24.dp),
                                theme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        }

                        UpdateState.Success -> Icon(
                            Icons.Rounded.Done,
                            contentDescription = "Success"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

            }
        }
    ) { values ->

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            item {

                Box {
                    model.avatar?.let {
                        Box(
                            Modifier
                                .sizeIn(minWidth = 40.dp, maxWidth = 120.dp)
                                .aspectRatio(1f / 1f)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            GlideImage(
                                model = it,
                                contentScale = ContentScale.Crop,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } ?: Box(
                        Modifier
                            .sizeIn(minWidth = 40.dp, maxWidth = 100.dp)
                            .aspectRatio(1f / 1f)
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                BorderStroke(width = 1.dp, color = theme.outline),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "User avatar",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = "Choose avatar")
                    }
                }

            }
            item {
                RoleDropdown(
                    role = model.type,
                    onChangeUserType = component::onChangeType,
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = model.email,
                    onValueChange = component::onChangeEmail,
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.AlternateEmail,
                            contentDescription = "Email"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = model.emailError != null,
                    supportingText = { model.emailError?.let { Text(text = it) } },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    placeholder = { Text(text = stringResource(id = R.string.enter_email)) },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = model.username,
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "Person") },
                    onValueChange = component::onChangeUsername,
                    label = { Text(stringResource(R.string.username)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = model.usernameError != null,
                    supportingText = { model.usernameError?.let { Text(text = it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row {
                    OutlinedTextField(
                        value = model.firstName,
                        onValueChange = component::onChangeFirstName,
                        label = { Text(stringResource(R.string.first_name)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        ),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = model.lastName,
                        onValueChange = component::onChangeLastName,
                        label = { Text(stringResource(R.string.last_name)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = model.phone,
                    onValueChange = component::onChangePhone,
                    label = { Text(text = stringResource(R.string.phone)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    isError = model.phoneError != null,
                    supportingText = { model.phoneError?.let { Text(text = it) } },
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = "Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


}