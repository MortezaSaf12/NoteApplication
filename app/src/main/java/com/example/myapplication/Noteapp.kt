package com.example.myapplication

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class TodoItem(
    val itemId: Int,
    var title: String,
    var description: String,
    val isCompleted: MutableState<Boolean> = mutableStateOf(false)
)

@Composable
fun Navigation(){
    DarkModeTheme {
        val navController = rememberNavController()
        val todoList = remember { mutableStateListOf<TodoItem>() }

        NavHost(navController = navController, startDestination = "homepage") {
            composable("homepage") { MainScreen(navController, todoList) }
            composable("addNote") { AddNoteScreen(navController, todoList) }
            composable("editTodo/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                val todoItem = todoList.find { it.itemId == itemId }
                todoItem?.let { EditTodoScreen(navController, it) }
            }
        }
    }
}

@Composable
fun DarkModeTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, todoList: MutableList<TodoItem>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notes") })
        },
        floatingActionButton = {
            FilledTonalButton(onClick = { navController.navigate("addNote") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Todo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(todoList) { item ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // Add spacing for a neat layout
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Checkbox with modified appearance
                        Checkbox(
                            checked = item.isCompleted.value,
                            onCheckedChange = {
                                item.isCompleted.value = !item.isCompleted.value
                            },
                            modifier = Modifier.padding(end = 16.dp) // Ensure space between checkbox and text
                        )

                        // Title and description of the note
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Edit and Delete buttons
                        Row {
                            IconButton(
                                onClick = { navController.navigate("editTodo/${item.itemId}") }
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(
                                onClick = { todoList.remove(item) }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController, todoList: MutableList<TodoItem>) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        titleError = null
        descriptionError = null

        if (title.length < 3) {
            titleError = "Title must be at least 3 characters."
            isValid = false
        }
        if (title.length > 50) {
            titleError = "Title must not exceed 50 characters."
            isValid = false
        }
        if (description.length > 120) {
            descriptionError = "Description must not exceed 120 characters."
            isValid = false
        }

        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create new note") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null // Reset error when user starts typing
                },
                label = { Text("Title") },
                isError = titleError != null
            )
            if (titleError != null) {
                Text(
                    text = titleError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = null // Reset error when user starts typing
                },
                label = { Text("Description") },
                isError = descriptionError != null
            )
            if (descriptionError != null) {
                Text(
                    text = descriptionError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FilledTonalButton(onClick = {
                if (validate()) {
                    todoList.add(TodoItem(itemId = todoList.size, title = title, description = description))
                    navController.popBackStack()
                }
            }) {
                Text("Create")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(navController: NavController, todoItem: TodoItem) {
    var title by remember { mutableStateOf(todoItem.title) }
    var description by remember { mutableStateOf(todoItem.description) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        titleError = null
        descriptionError = null

        if (title.length < 3) {
            titleError = "Title must be at least 3 characters."
            isValid = false
        }
        if (title.length > 50) {
            titleError = "Title must not exceed 50 characters."
            isValid = false
        }
        if (description.length > 120) {
            descriptionError = "Description must not exceed 120 characters."
            isValid = false
        }

        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null // Reset error when user starts typing
                },
                label = { Text("Title") },
                isError = titleError != null
            )
            if (titleError != null) {
                Text(
                    text = titleError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = null // Reset error when user starts typing
                },
                label = { Text("Description") },
                isError = descriptionError != null
            )
            if (descriptionError != null) {
                Text(
                    text = descriptionError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if (validate()) {
                    todoItem.title = title
                    todoItem.description = description
                    navController.popBackStack()
                }
            }) {
                Text("Save")
            }
        }
    }
}