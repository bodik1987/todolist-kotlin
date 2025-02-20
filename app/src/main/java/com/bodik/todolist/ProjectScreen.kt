package com.bodik.todolist

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    projectId: String?,
    projectTitle: String?,
    chaptersState: MutableState<List<Chapter>>,
    preferencesHelper: PreferencesHelper
) {
    val addItemModal = remember { mutableStateOf(false) }
    val showAdditionalText = remember { mutableStateOf(false) }
    val addChapterModal = remember { mutableStateOf(false) }

    var itemTitle by remember { mutableStateOf("") }
    var itemAdditionalText by remember { mutableStateOf("") }
    var chapterTitle by remember { mutableStateOf("") }

    val focusRequesterItemTitle = remember { FocusRequester() }
    val focusRequesterItemAdditionalText = remember { FocusRequester() }
    val focusRequesterChapterTitle = remember { FocusRequester() }

    val listState = rememberLazyListState()

    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("Todo", "Note")

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        chapterTitle = ""
                        addChapterModal.value = true
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Localized description")
                        Text(text = stringResource(id = R.string.add_chapter))
                    }
                }
                FloatingActionButton(
                    onClick = {
                        itemTitle = ""
                        itemAdditionalText = ""
                        addItemModal.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }

        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            ItemsList(
                chaptersState = chaptersState,
                listState = listState,
                addItem = {
                    itemTitle = ""
                    itemAdditionalText = ""
                    addItemModal.value = true
                }
            )

            if (addChapterModal.value) {
                LaunchedEffect(addChapterModal.value) {
                    focusRequesterChapterTitle.requestFocus()
                }

                ModalBottomSheet(
                    onDismissRequest = { addChapterModal.value = false },
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
                            value = chapterTitle,
                            onValueChange = { chapterTitle = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (chapterTitle.isEmpty()) {
                                        addChapterModal.value = false
                                    } else {
                                        val newChapter = Chapter(
                                            id = UUID.randomUUID().toString(),
                                            title = chapterTitle,
                                            projectId = projectId ?: ""
                                        )
                                        val updatedChapters = chaptersState.value + newChapter
                                        preferencesHelper.saveChapters(updatedChapters)
                                        chaptersState.value = updatedChapters
                                        chapterTitle = ""
                                        addChapterModal.value = false
                                    }
                                }
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (chapterTitle.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.new_chapter),
                                            style = TextStyle(fontSize = 18.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp)
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.focusRequester(focusRequesterChapterTitle)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                enabled = chapterTitle.isNotEmpty(),
                                onClick = {
                                    val newChapter = Chapter(
                                        id = UUID.randomUUID().toString(),
                                        title = chapterTitle,
                                        projectId = projectId ?: ""
                                    )
                                    val updatedChapters = chaptersState.value + newChapter
                                    preferencesHelper.saveChapters(updatedChapters)
                                    chaptersState.value = updatedChapters
                                    chapterTitle = ""
                                    addChapterModal.value = false
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedIndex == 0) {
                                Icon(
                                    painter = painterResource(id = R.drawable.circle_check),
                                    contentDescription = "Localized description"
                                )
                            }

                            BasicTextField(
                                value = itemTitle,
                                onValueChange = { itemTitle = it },
                                maxLines = 2,
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
                                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (itemTitle.isEmpty()) {
                                            Text(
                                                text = stringResource(id = if (selectedIndex == 0) R.string.todo else R.string.note),
                                                style = TextStyle(fontSize = 22.sp),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier.focusRequester(focusRequesterItemTitle)
                            )
                        }

                        BasicTextField(
                            value = itemAdditionalText,
                            onValueChange = { itemAdditionalText = it },
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
                                        itemAdditionalText = ""
                                        addItemModal.value = false
                                    }
                                }
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (itemAdditionalText.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.additional_text),
                                            style = TextStyle(fontSize = 17.sp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .alpha(0.6f)
                                                .padding(top = 2.dp)
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .focusRequester(focusRequesterItemAdditionalText)
                                .padding(top = 12.dp)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.clock),
                                        contentDescription = "Localized description"
                                    )
                                }

                                SingleChoiceSegmentedButtonRow {
                                    options.forEachIndexed { index, label ->
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = index,
                                                count = options.size
                                            ),
                                            onClick = { selectedIndex = index },
                                            selected = index == selectedIndex
                                        ) {
                                            Text(label)
                                        }
                                    }
                                }
                            }
                            TextButton(
                                enabled = itemTitle.isNotEmpty(),
                                modifier = Modifier.padding(top = 8.dp),
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
}