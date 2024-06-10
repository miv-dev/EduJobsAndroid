package ru.edu.jobs.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.edu.jobs.R
import ru.edu.jobs.presentation.components.RoleDropdown
import ru.edu.jobs.presentation.register.RegisterStore.State.RegisterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(component: RegisterComponent) {
    val model by component.model.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val theme = MaterialTheme.colorScheme
    val hasError = model.registerState is RegisterState.Error
    var showPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(model.registerState) {
        val state = model.registerState
        if (state is RegisterState.Error) {
            snackbarHostState.showSnackbar(state.error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.sign_up)) }, navigationIcon = {
                IconButton(onClick = { component.onClickBack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }

    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.username,
                onValueChange = component::onChangeUsername,
                placeholder = { Text(text = stringResource(id = R.string.enter_username)) },
                isError = model.usernameError != null,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                supportingText = { model.usernameError?.let { Text(text = it) } },
                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "Username") },
                label = { Text(text = stringResource(id = R.string.username)) })
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.email,
                onValueChange = component::onChangeEmail,
                isError = model.emailError != null,
                supportingText = { model.emailError?.let { Text(text = it) } },
                label = { Text(text = stringResource(R.string.email)) },
                leadingIcon = { Icon(Icons.Rounded.AlternateEmail, contentDescription = "Email") },
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                placeholder = { Text(text = stringResource(R.string.enter_email)) },
            )

            var expanded by remember {
                mutableStateOf(false)
            }

            RoleDropdown(
                role = model.usertype,
                onChangeUserType = component::onChangeUserType,
                supportingText = stringResource(R.string.you_can_change_your_role_later)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.password,
                onValueChange = component::onChangePassword,
                leadingIcon = { Icon(Icons.Rounded.Dialpad, contentDescription = "Password") },

                isError = model.passwordError != null,
                supportingText = { model.passwordError?.let { Text(text = it) } },
                label = { Text(text = stringResource(id = R.string.password)) },

                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                placeholder = { Text(text = stringResource(id = R.string.enter_password)) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        val icon = if (showPassword) {
                            Icons.Rounded.Visibility

                        } else {
                            Icons.Rounded.VisibilityOff
                        }
                        Icon(icon, contentDescription = "Show Password")
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.passwordConfirmation,
                onValueChange = component::onChangePassword2,
                isError = model.passwordConfirmationError != null,
                leadingIcon = { Icon(Icons.Rounded.Dialpad, contentDescription = "Password") },
                supportingText = { model.passwordConfirmationError?.let { Text(text = it) } },
                label = { Text(text = stringResource(id = R.string.password)) },
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                placeholder = { Text(text = stringResource(R.string.confirm_password)) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        val icon = if (showPassword) {
                            Icons.Rounded.Visibility

                        } else {
                            Icons.Rounded.VisibilityOff
                        }
                        Icon(icon, contentDescription = "Show Password")
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.weight(1f))
            val btnErrorColors = ButtonDefaults.buttonColors(theme.errorContainer, theme.error)
            Button(
                onClick = component::submit,
                colors = if (hasError) btnErrorColors else ButtonDefaults.buttonColors()
            ) {
                Spacer(modifier = Modifier.weight(1f))

                this.AnimatedVisibility(model.registerState is RegisterState.Idle) {
                    Text(text = stringResource(id = R.string.send))
                    Spacer(modifier = Modifier.width(8.dp))

                }

                AnimatedContent(model.registerState, label = "button icon") {
                    when (it) {
                        is RegisterState.Error -> Icon(
                            Icons.Rounded.Error,
                            contentDescription = "Submit",
                            tint = theme.error
                        )

                        RegisterState.Idle -> Icon(
                            Icons.Rounded.ArrowForward,
                            contentDescription = "Submit "
                        )

                        RegisterState.Loading -> {
                            CircularProgressIndicator(
                                Modifier.size(24.dp),
                                theme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        }

                        RegisterState.Success -> Icon(
                            Icons.Rounded.Done,
                            contentDescription = "Success"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.have_an_account),
                )
                FilledTonalButton(onClick = component::onClickLogin) {
                    Text(text = stringResource(id = R.string.log_in))
                }
            }
        }
    }
}