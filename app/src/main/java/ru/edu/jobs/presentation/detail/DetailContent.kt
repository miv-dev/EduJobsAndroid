@file:OptIn(ExperimentalMaterial3Api::class)

package ru.edu.jobs.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.edu.jobs.R
import ru.edu.jobs.presentation.components.HtmlText
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailContent(component: DetailComponent) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { }
    )

    val model by component.model.collectAsState()
    val typography = MaterialTheme.typography
    Scaffold(
        topBar = {
            TopAppBar(title = { }, navigationIcon = {
                IconButton(onClick = component::onBackClicked) {
                    Icon(Icons.Rounded.ArrowBack, "back")
                }
            }, actions = {
                when (model.openBy) {
                    is OpenBy.Viewer -> IconToggleButton(
                        checked = model.isFavourite,
                        onCheckedChange = component::onFavoriteClicked,
                    ) {
                        Icon(
                            if (model.isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Tap to add in favourites"
                        )
                    }

                    is OpenBy.Owner -> IconButton(onClick = component::onEditClicked) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Tap to edit"
                        )
                    }

                    is OpenBy.Parsed -> {
                        IconButton(onClick = {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(model.parsedService?.url ?: "")
                            )
                            launcher.launch(browserIntent)
                        }) {
                            Icon(Icons.Rounded.OpenInNew, "Open in browser")
                        }
                    }
                }

            })
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            when (model.openBy) {
                is OpenBy.Owner,
                is OpenBy.Viewer -> {
                    when (val state = model.serviceState) {
                        is DetailStore.State.ServiceState.Error -> Text(
                            state.error.localizedMessage ?: "Unknown Error"
                        )

                        is DetailStore.State.ServiceState.Loaded -> Column {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(
                                    12.dp
                                )
                            ) {
                                item {
                                    Text(
                                        text = state.service.name,
                                        style = typography.headlineMedium
                                    )
                                    state.service.deadline?.let {
                                        ListItem(
                                            overlineContent = {
                                                Text(text = stringResource(id = R.string.deadline))
                                            },
                                            headlineContent = {
                                                Text(text = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                            }
                                        )
                                    }
                                    Text(text = state.service.description)
                                    Spacer(Modifier.height(12.dp))

                                }
                                item {
                                    Text(
                                        text = stringResource(R.string.contacts),
                                        style = typography.headlineSmall
                                    )
                                    ElevatedCard {
                                        Column(
                                            Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth()
                                        ) {
                                            val user = state.service.user


                                            ListItem(
                                                leadingContent = {
                                                    user.profile.avatar?.let {
                                                        Box(
                                                            Modifier
                                                                .size(40.dp)
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
                                                    }
                                                },
                                                headlineContent = {
                                                    if (user.profile.firstName != null || user.profile.lastName != null) {
                                                        Text(text = "${user.profile.firstName} ${user.profile.lastName}")
                                                    } else {
                                                        Text(text = user.username)
                                                    }
                                                },
                                            )
                                            ListItem(

                                                overlineContent = {
                                                    Text(text = stringResource(R.string.email))
                                                },
                                                headlineContent = {
                                                    Text(text = user.email)
                                                },
                                                trailingContent = {
                                                    IconButton(onClick = {
                                                        val intent = Intent(
                                                            Intent.ACTION_SENDTO,
                                                            Uri.parse("mailto:${user.email}")
                                                        )
                                                        intent.putExtra(
                                                            Intent.EXTRA_SUBJECT,
                                                            state.service.name
                                                        )

                                                        launcher.launch(intent)
                                                    }) {
                                                        Icon(
                                                            Icons.Rounded.Send,
                                                            "Contact via email"
                                                        )
                                                    }
                                                }
                                            )
                                            user.profile.phone?.let {
                                                ListItem(

                                                    overlineContent = {
                                                        Text(text = stringResource(R.string.phone))
                                                    },
                                                    headlineContent = {
                                                        Text(text = it)
                                                    },
                                                    trailingContent = {
                                                        IconButton(onClick = {
                                                            val intent = Intent(
                                                                Intent.ACTION_DIAL,
                                                                Uri.parse("tel:$it")
                                                            )
                                                            launcher.launch(intent)
                                                        }) {
                                                            Icon(
                                                                Icons.Rounded.Phone,
                                                                "Contact via phone"
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }

                                }

                            }

                        }

                        DetailStore.State.ServiceState.Loading -> {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                is OpenBy.Parsed -> {
                    val service = model.parsedService
                    if (service != null) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(
                                12.dp
                            )
                        ) {
                            item {
                                Text(text = service.title, style = typography.headlineMedium)
                                service.price?.let {
                                    ListItem(
                                        overlineContent = {
                                            Text(text = stringResource(id = R.string.price))
                                        },
                                        headlineContent = {
                                            Text(text = it)
                                        }
                                    )
                                }
                                HtmlText(html = service.description)
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
