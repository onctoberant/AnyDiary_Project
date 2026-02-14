package com.example.anydiaryproject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import java.time.ZoneId




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    var currentPage by remember { mutableStateOf(0) }
    var showAddPostDialog by remember { mutableStateOf(false) }

    Scaffold(

        containerColor = Color.White,

        topBar = {

            Surface(
                color = Color(0xFFE3F2FD), // ฟ้าอ่อน
                shadowElevation = 4.dp
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TopButton(
                        icon = Icons.Filled.Home,
                        selected = currentPage == 0
                    ) { currentPage = 0 }

                    TopButton(
                        icon = Icons.Filled.DateRange,
                        selected = currentPage == 1
                    ) { currentPage = 1 }

                    TopButton(
                        icon = Icons.Filled.Check,
                        selected = currentPage == 2
                    ) { currentPage = 2 }

                    TopButton(
                        icon = Icons.Filled.Notifications,
                        selected = currentPage == 3
                    ) { currentPage = 3 }
                }
            }
        },

        floatingActionButton = {
            if (currentPage == 0) {
                FloatingActionButton(
                    onClick = { showAddPostDialog = true },
                    containerColor = Color(0xFF90CAF9) // ฟ้า
                ) {
                    Text("+", color = Color.White)
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {

            when (currentPage) {

                0 -> HomeContent()

                1 -> Text(
                    "Calendar Screen",
                    color = Color(0xFF6B4F3B)
                )

                2 -> Text(
                    "To Do List Screen",
                    color = Color(0xFF6B4F3B)
                )

                3 -> Text(
                    "Message Screen",
                    color = Color(0xFF6B4F3B)
                )
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog { showAddPostDialog = false }
    }
}

@Composable
fun TopButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) Color.White
        else Color.Transparent

    val iconColor =
        if (selected) Color(0xFF6B4F3B)
        else Color(0xFF6B4F3B).copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )
    }
}


@Composable
fun HomeContent() {

    LazyColumn {

        items(AppState.posts) { post ->

            val members = AppState.members.filter {
                post.memberIds.contains(it.id)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF4F7FB)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                text = members.joinToString { it.name },
                                color = Color(0xFF6B4F3B),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = post.date.format(
                                    DateTimeFormatter.ofPattern("dd MMM yyyy")
                                ),
                                color = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = { AppState.deletePost(post) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(post.content)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(onDismiss: () -> Unit) {

    var content by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    val members = AppState.members
    val selectedMembers = remember { mutableStateListOf<Member>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(32.dp),
        containerColor = Color(0xFFF8FBFF),
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {

            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = { showAddMemberDialog = true },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A90E2)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = when {
                                    selectedMembers.isEmpty() ->
                                        "Add Member"
                                    selectedMembers.size == 1 ->
                                        selectedMembers.first().name
                                    else ->
                                        "${selectedMembers.size} Members"
                                },
                                color = Color.White
                            )

                            if (selectedMembers.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF4A90E2)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = selectedDate.toString(),
                            color = Color(0xFF8B5E3C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Write something...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (content.isNotBlank()) {

                                AppState.addPost(
                                    memberIds = selectedMembers.map { it.id },
                                    content = content,
                                    date = selectedDate
                                )

                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5E3C)
                        )
                    ) {
                        Text("save", color = Color.White)
                    }
                }
            }
        }
    )

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(
                                millis / (24 * 60 * 60 * 1000)
                            )
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onMemberAdded = { member ->
                selectedMembers.add(member)
            }
        )
    }
}




@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onMemberAdded: (Member) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

//    val newMember = AppState.addMember(
 //       name = name,
 //       imageUri = imageUri?.toString()
 //   )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFF5DA9E9), CircleShape)
                        .clickable { launcher.launch("image/*") }
                ) {

                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Member Name") },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {

                            val newMember = AppState.addMember(
                                name = name,
                                imageUri = imageUri?.toString()
                            )

                            onMemberAdded(newMember)
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5E3C)
                    )
                ) {
                    Row {
                        Text("save ", color = Color.White)
                    }
                }
            }
        }
    )
}




