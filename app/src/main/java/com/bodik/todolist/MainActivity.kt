package com.bodik.todolist

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bodik.todolist.ui.theme.TodolistTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodolistTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tobBarTitle = if (currentRoute?.startsWith("projects/") == true) {
        navBackStackEntry?.arguments?.getString("projectTitle")
    } else {
        "Todolist"
    }

    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val addProjectModal = remember { mutableStateOf(false) }
    val projectsState = remember { mutableStateOf(preferencesHelper.loadProjects()) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                title = {
                    if (tobBarTitle != null) {
                        Text(tobBarTitle)
                    } else if (currentRoute != null) {
                        Text(currentRoute)
                    }
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                Home(
                    navController,
                    addProjectModal = addProjectModal,
                    preferencesHelper = preferencesHelper,
                    projectsState = projectsState
                )
            }
            composable(
                route = "projects/{projectId}/{projectTitle}",
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType },
                    navArgument("projectTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                val projectTitle = backStackEntry.arguments?.getString("projectTitle")
                ProjectScreen(projectId = projectId, projectTitle = projectTitle)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    addProjectModal: MutableState<Boolean>,
    preferencesHelper: PreferencesHelper,
    projectsState: MutableState<List<Project>>,
) {
    var projectTitle by remember { mutableStateOf("") }
    val focusRequesterProject = remember { FocusRequester() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    projectTitle = ""
                    addProjectModal.value = true
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
            ProjectsList(
                preferencesHelper = preferencesHelper,
                projectsState = projectsState,
                navController = navController
            )

            if (addProjectModal.value) {
                LaunchedEffect(addProjectModal.value) {
                    focusRequesterProject.requestFocus()
                }

                ModalBottomSheet(
                    onDismissRequest = { addProjectModal.value = false },
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
                            value = projectTitle,
                            onValueChange = { projectTitle = it },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (projectTitle.isEmpty()) {
                                        addProjectModal.value = false
                                    } else {
                                        val newProject = Project(
                                            id = UUID.randomUUID().toString(),
                                            title = projectTitle,
                                        )
                                        val updatedProjects = projectsState.value + newProject
                                        preferencesHelper.saveProjects(updatedProjects)
                                        projectsState.value = updatedProjects
                                        projectTitle = ""
                                        addProjectModal.value = false
                                    }
                                }
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (projectTitle.isEmpty()) {
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
                            modifier = Modifier.focusRequester(focusRequesterProject)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                enabled = projectTitle.isNotEmpty(),
                                onClick = {
                                    val newProject = Project(
                                        id = UUID.randomUUID().toString(),
                                        title = projectTitle,
                                    )
                                    val updatedProjects = projectsState.value + newProject
                                    preferencesHelper.saveProjects(updatedProjects)
                                    projectsState.value = updatedProjects
                                    projectTitle = ""
                                    addProjectModal.value = false
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