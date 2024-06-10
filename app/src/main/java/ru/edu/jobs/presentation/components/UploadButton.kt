package ru.edu.jobs.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class UploadButtonState {
    Loading, Error, Success, Idle
}

@Composable
fun UploadButton(
    modifier: Modifier = Modifier,
    state: UploadButtonState = UploadButtonState.Idle,
    onClick: () -> Unit,
    label: String
) {
    val theme = MaterialTheme.colorScheme
    val btnErrorColors = ButtonDefaults.buttonColors(theme.errorContainer, theme.error)
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = if (state == UploadButtonState.Error) btnErrorColors else ButtonDefaults.buttonColors()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        this.AnimatedVisibility( state == UploadButtonState.Idle) {
            Text(text = label)
            Spacer(modifier = Modifier.width(8.dp))

        }
        AnimatedContent(state, label = "button icon") {
            when (it) {
                UploadButtonState.Error -> Icon(
                    Icons.Rounded.Error,
                    contentDescription = "Submit",
                    tint = theme.error
                )

                UploadButtonState.Idle -> {

                }

                UploadButtonState.Loading -> {
                    CircularProgressIndicator(
                        Modifier.size(24.dp),
                        theme.onPrimary,
                        strokeWidth = 2.dp
                    )
                }

                UploadButtonState.Success -> Icon(
                    Icons.Rounded.Done,
                    contentDescription = "Success"
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

    }
}