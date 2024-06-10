package ru.edu.jobs.presentation.auth.unauthroized

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import ru.edu.jobs.R
import ru.edu.jobs.presentation.login.LoginContent
import ru.edu.jobs.presentation.register.RegisterContent

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UnauthorizedContent(component: UnauthorizedComponent) {
    val model by component.model.subscribeAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { }
    )
    Children(stack = component.stack) { child ->
        when (val instance = child.instance) {
            is UnauthorizedComponent.Child.Login -> LoginContent(component = instance.component)
            is UnauthorizedComponent.Child.Register -> RegisterContent(component = instance.component)
            UnauthorizedComponent.Child.Welcome -> {
                Scaffold {
                    Column(
                        Modifier
                            .padding(it)
                            .padding(horizontal = 12.dp)
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (val state = model.welcomeState) {
                            is UnauthorizedComponent.Model.WelcomeState.Content -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            val browserIntent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(state.welcome.link)
                                            )
                                            launcher.launch(browserIntent)
                                        }) {
                                            Icon(Icons.Rounded.OpenInNew, "Open in browser")
                                        }
                                    }

                                    AsyncImage(
                                        model=  ImageRequest.Builder(LocalContext.current)
                                            .decoderFactory(SvgDecoder.Factory())
                                            .data(if (isSystemInDarkTheme()) state.welcome.darkImage else state.welcome.lightImage)
                                            .build(),
                                        contentDescription = "Logo",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.4f)
                                    )
                                    Text(text = state.welcome.title, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = component::login,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(stringResource(id = R.string.log_in))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    FilledTonalButton(
                                        onClick = component::register,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(stringResource(id = R.string.sign_up))
                                    }
                                    Spacer(Modifier.weight(1f))
                                }
                            }

                            is UnauthorizedComponent.Model.WelcomeState.Error -> {
                                Text(state.error)
                            }

                            UnauthorizedComponent.Model.WelcomeState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        }


                    }
                }
            }
        }

    }

}