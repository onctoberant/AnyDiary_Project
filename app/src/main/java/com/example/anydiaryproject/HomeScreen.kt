package com.example.anydiaryproject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HomeScreen() {

    var showAddPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPostDialog = true }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "My Posts",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(AppState.posts) { post ->

                    val member =
                        AppState.members.find { it.id == post.memberId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        Row(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            AsyncImage(
                                model = member?.imageUri,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {

                                Text(
                                    text = member?.name ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = post.date.toString(),
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = post.content)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog { showAddPostDialog = false }
    }
}

@Composable
fun AddPostDialog(onDismiss: () -> Unit) {

    var content by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf<Member?>(null) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (selectedMember != null && content.isNotBlank()) {

                        AppState.addPost(
                            memberId = selectedMember!!.id,
                            content = content
                        )

                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Post") },
        text = {
            Column {

                DropdownMember(
                    selectedMember = selectedMember,
                    onSelected = { selectedMember = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showAddMemberDialog = true }
                ) {
                    Text("Add Member")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Post Content") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )

    if (showAddMemberDialog) {
        AddMemberDialog { showAddMemberDialog = false }
    }
}

@Composable
fun DropdownMember(
    selectedMember: Member?,
    onSelected: (Member) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {

        Button(onClick = { expanded = true }) {
            Text(selectedMember?.name ?: "Select Member")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppState.members.forEach { member ->

                DropdownMenuItem(
                    text = { Text(member.name) },
                    onClick = {
                        onSelected(member)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddMemberDialog(onDismiss: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && imageUri != null) {

                        AppState.addMember(
                            name = name,
                            imageUri = imageUri.toString()
                        )

                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Member") },
        text = {
            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Member Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("image/*") }
                ) {
                    Text("Select Image")
                }

                Spacer(modifier = Modifier.height(8.dp))

                imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    )
}
