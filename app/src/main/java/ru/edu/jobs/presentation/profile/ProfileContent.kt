@file:OptIn(ExperimentalMaterial3Api::class)

package ru.edu.jobs.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.edu.jobs.R
import ru.edu.jobs.domain.entity.Role

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileContent(component: ProfileComponent) {
    val model = component.model.collectAsState()
    val theme = MaterialTheme.colorScheme
    val user = model.value.user
    Column {
        if (user == null) {
            return
        }


        Row(Modifier.aspectRatio(4f / 3f)) {

            ListItem(
                modifier = Modifier.align(Alignment.CenterVertically),
                leadingContent = {
                    user.profile.avatar?.let {
                        Box(
                            Modifier
                                .sizeIn(minWidth = 40.dp, maxWidth = 70.dp)
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
                },
                headlineContent = {
                    Text(text = user.username, style = MaterialTheme.typography.headlineMedium)
                },
                overlineContent = {
                    val text = when (user.role) {
                        Role.Specialist -> stringResource(R.string.specialist)
                        Role.Employer -> stringResource(R.string.employer)
                        Role.Admin -> stringResource(R.string.admin)
                        null -> stringResource(R.string.select_role)
                    }
                    Text(text = text.uppercase())
                },
                supportingContent = {
                    Text(text = user.email)
                }
            )

        }

        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            ElevatedCard(onClick = component::editProfile) {
                Row(Modifier.padding(12.dp)) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit Profile")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.edit_profile))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.ArrowForward, contentDescription = "Forward")

                }
            }
            ElevatedCard(onClick = component::changeUniversity) {
                Row(Modifier.padding(12.dp)) {
                    Icon(Icons.Rounded.Apartment, contentDescription = "University")
                    Spacer(modifier = Modifier.width(8.dp))
                    if (user.department != null) {
                        Text(text = user.department.name)
                    } else {
                        Text(text = stringResource(R.string.change_department))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.ArrowForward, contentDescription = "Forward")

                }
            }



            Spacer(modifier = Modifier.weight(1f))
            OutlinedCard(
                onClick = component::logout,
                border = BorderStroke(1.dp, theme.error),
                colors = CardDefaults.outlinedCardColors(contentColor = theme.error)
            ) {
                Row(Modifier.padding(12.dp)) {
                    Text(text = stringResource(R.string.logout))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.Logout, contentDescription = "Logout")

                }
            }
            Spacer(modifier = Modifier.weight(3f))


        }


    }

}