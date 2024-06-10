package ru.edu.jobs.presentation.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.LoadState
import ru.edu.jobs.domain.entity.ParsedService
import ru.edu.jobs.presentation.components.HtmlText
import ru.edu.jobs.presentation.extensions.errorToString

@Composable
fun OtherServicesContent(component: OtherComponent) {
    val model by component.model.collectAsState()
    Column {
        AnimatedVisibility(model.parsedServicesState is LoadState.Updating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Column(Modifier.padding(top = 12.dp)) {
            when (val state = model.parsedServicesState) {

                is LoadState.Error -> {
                    Text(text = errorToString(state.error))
                }

                LoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Success -> {

                    ParsedServiceList(
                        services = state.data,
                        onDetailClicked = { service ->
                            component.onDetailClicked(service)
                        },
                    )
                }

                is LoadState.Updating -> {
                    ParsedServiceList(
                        services = state.data,
                        onDetailClicked = { service ->
                            component.onDetailClicked(service)
                        },
                    )
                }
            }


        }
    }


}


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ParsedServiceList(
    services: List<ParsedService>,
    onDetailClicked: (ParsedService) -> Unit,
) {

    val typography = MaterialTheme.typography
    val theme = MaterialTheme.colorScheme

    if (services.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.empty))
        }
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(services) { service ->
            ElevatedCard(onClick = { onDetailClicked(service) }) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = service.title, style = typography.titleLarge)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Column(
                        Modifier
                            .background(theme.background, RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                    ) {
                        HtmlText(
                            html = service.description,
                            maxLines = 2,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = "Site", Modifier.size(14.dp))
                        Text(
                            text = service.site.toString().uppercase(),
                            style = typography.labelMedium
                        )

                    }
                }


            }
        }
    }
}