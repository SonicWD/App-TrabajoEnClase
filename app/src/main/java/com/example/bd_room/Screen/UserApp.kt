package com.example.bd_room.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.bd_room.Model.User
import com.example.bd_room.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var scope = rememberCoroutineScope()
    var context = LocalContext.current
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var userIds by remember { mutableStateOf(listOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kloting App") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID (para actualizar/eliminar)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && apellido.isNotBlank() && edad.isNotBlank() && (edad.toIntOrNull() ?: -1) >= 0) {
                                val user = User(
                                    nombre = nombre,
                                    apellido = apellido,
                                    edad = edad.toIntOrNull() ?: 0
                                )
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        userRepository.insertar(user)
                                        users = userRepository.getAllUsers()
                                        userRepository.getAllUsers()

                                    }
                                    Toast.makeText(context, "Usuario Registrado", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Registrar")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                users = withContext(Dispatchers.IO) {
                                    userRepository.getAllUsers()

                                }
                            }
                        }
                    ) {
                        Text("Listar")
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val userIdToDelete = id.toIntOrNull()
                            if (userIdToDelete != null) {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        val userToDelete = users.find { it.id == userIdToDelete }
                                        if (userToDelete != null) {
                                            userRepository.eliminar(userToDelete)
                                            users = userRepository.getAllUsers()
                                        }
                                    }
                                    Toast.makeText(context, "Usuario Eliminado", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Eliminar")
                    }
                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && apellido.isNotBlank() && edad.isNotBlank() && id.isNotBlank()) {
                                val user = User(
                                    id = id.toIntOrNull() ?: 0,
                                    nombre = nombre,
                                    apellido = apellido,
                                    edad = edad.toIntOrNull() ?: 0
                                )
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        userRepository.actualizar(user)
                                        users = userRepository.getAllUsers()
                                    }
                                    Toast.makeText(context, "Usuario Actualizado", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Actualizar")
                    }
                }
            }
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${user.id}", style = MaterialTheme.typography.bodyMedium)
                        Text("${user.nombre} ${user.apellido}", style = MaterialTheme.typography.titleMedium)
                        Text("Edad: ${user.edad}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}