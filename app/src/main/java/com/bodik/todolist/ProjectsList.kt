package com.bodik.todolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProjectsList(
    projectsState: MutableState<List<Project>>,
    navController: NavController,
    listState: LazyListState
) {
    val projects = projectsState.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (projects.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.empty_list),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            LazyColumn(state = listState) {
                items(projects) { project ->
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("projects/${project.id}/${project.title}")
                            },
                        headlineContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.folder),
                                        contentDescription = null
                                    )

                                    Text(
                                        project.title,
                                        style = TextStyle(fontSize = 18.sp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
