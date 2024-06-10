package ru.edu.jobs.presentation.favourite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteContent(component: FavouriteComponent) {
    val model by component.model.collectAsState()


    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(model.services){
            ElevatedCard(onClick = { component.onServiceItemClick(it) }) {
                ListItem(
                    overlineContent = { Text(text = it.name)},
                    headlineContent = {
                    Text(text = it.description, maxLines = 3)
                })
            }
        }
    }


}