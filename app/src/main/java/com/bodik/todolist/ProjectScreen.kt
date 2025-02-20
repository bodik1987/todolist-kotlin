package com.bodik.todolist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(projectId: String?, projectTitle: String?) {
    val addItemModal = remember { mutableStateOf(false) }
    var itemTitle by remember { mutableStateOf("") }
    val focusRequesterItemTitle = remember { FocusRequester() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemTitle = ""
                    addItemModal.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->


            if (addItemModal.value) {
                LaunchedEffect(addItemModal.value) {
                    focusRequesterItemTitle.requestFocus()
                }

                ModalBottomSheet(
                    onDismissRequest = { addItemModal.value = false },
                    dragHandle = null,
                    windowInsets = WindowInsets.ime
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        BasicTextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (itemTitle.isEmpty()) {
                                        addItemModal.value = false
                                    } else {
//                                        val newProject = Project(
//                                            id = UUID.randomUUID().toString(),
//                                            title = itemTitle,
//                                        )
//                                        val updatedProjects = projectsState.value + newProject
//                                        preferencesHelper.saveProjects(updatedProjects)
//                                        projectsState.value = updatedProjects
                                        itemTitle = ""
                                        addItemModal.value = false
                                    }
                                }
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (itemTitle.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.new_project),
                                            style = TextStyle(fontSize = 18.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp)
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.focusRequester(focusRequesterItemTitle)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                enabled = itemTitle.isNotEmpty(),
                                onClick = {
//                                    val newProject = Project(
//                                        id = UUID.randomUUID().toString(),
//                                        title = projectTitle,
//                                    )
//                                    val updatedProjects = projectsState.value + newProject
//                                    preferencesHelper.saveProjects(updatedProjects)
//                                    projectsState.value = updatedProjects
                                    itemTitle = ""
                                    addItemModal.value = false
                                },
                            ) {
                                Text(
                                    stringResource(id = R.string.save),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Project ID: $projectId"
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Project Title: $projectTitle"
        )
    }

}