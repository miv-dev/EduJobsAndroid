package ru.edu.jobs.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.presentation.extensions.errorToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(component: MainComponent) {
    val model by component.model.collectAsState()

    Column {
        AnimatedVisibility(model.servicesState is LoadState.Updating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Column(Modifier.padding(top = 12.dp)) {

            Column {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(100)),
                    onClick = { component.onSearchClicked(null) }) {
                    Row(
                        Modifier
                            .padding(12.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(text = stringResource(R.string.search))
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Rounded.Search, contentDescription = "Search")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

            }




            when (val state = model.servicesState) {

                is LoadState.Error -> {
                    Text(text = errorToString(state.error))
                }

                LoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Success -> {

                    ServiceList(
                        services = state.data,
                        onSearchClicked = { component.onSearchClicked(null) },
                        onDetailClicked = { service ->
                            component.onDetailClicked(service)
                        },
                    )
                }

                is LoadState.Updating -> {
                    ServiceList(
                        services = state.data,
                        onSearchClicked = { component.onSearchClicked(null) },
                        onDetailClicked = { service ->
                            component.onDetailClicked(service)
                        },
                    )
                }
            }


        }
    }


}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ServiceList(
    services: List<Service>,
    onDetailClicked: (Service) -> Unit,
    onSearchClicked: () -> Unit
) {

    val typography = MaterialTheme.typography
    val theme = MaterialTheme.colorScheme

    if (services.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ElevatedCard(elevation = CardDefaults.cardElevation(3.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(text = stringResource(R.string.find_something))
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onSearchClicked) {
                        Text(text = stringResource(R.string.search))
                    }
                }
            }

        }
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = stringResource(R.string.viewed), style = typography.titleMedium)
        }
        items(services) { service ->
            ElevatedCard {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = service.name, style = typography.titleLarge)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Column(
                        Modifier
                            .background(theme.background, RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = service.description,
                            maxLines = 3,
                            minLines = 2,
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        service.user.profile.avatar?.let {
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
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(verticalArrangement = Arrangement.Center) {
                            Text(service.user.username)
                            service.user.department?.let { Text(it.name) }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { onDetailClicked(service) }) {
                            Text(text = stringResource(R.string.more))
                        }

                    }

                }


            }
        }
    }
}
