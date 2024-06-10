package ru.edu.jobs.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.edu.jobs.domain.entity.Service
import ru.edu.jobs.presentation.search.SearchStore.State.SearchState

@Composable
fun SearchContent(component: SearchComponent) {
    val model = component.model.collectAsState()


    SearchScreen(
        model = model.value,
        onBack = component::onBackClicked,
        onSearch = component::onSearch,
        viewService = component::viewService
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    model: SearchStore.State,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    viewService: (Service) -> Unit
) {

    val typography = MaterialTheme.typography
    val theme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = model.query,
                    onValueChange = onSearch,
                    shape = RoundedCornerShape(100),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search") },
                    trailingIcon = {
                        if (model.query.isNotEmpty()) {
                            IconButton(onClick = { onSearch("") }, Modifier.padding(end = 4.dp)) {
                                Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                )

            }
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(top = 12.dp)
        ) {
            when (val state = model.searchState) {
                is SearchState.Loaded -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.services) { service ->
                            ElevatedCard(onClick = { viewService(service) }) {
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

                                }


                            }
                        }
                    }
                }

                is SearchState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                }

                is SearchState.Error -> {
                    // Empty
                }

                SearchState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ElevatedCard( elevation = CardDefaults.cardElevation(3.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.size(100.dp)) {
                                Icon(
                                    imageVector = Icons.Rounded.SearchOff,
                                    contentDescription = "Empty",
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(text = "Empty")
                            }
                        }

                    }
                }
            }
        }
    }
}