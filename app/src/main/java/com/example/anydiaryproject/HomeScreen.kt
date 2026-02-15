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
// import kotlin.time.Instant
import java.time.Instant
import java.time.ZoneId



private val BluePrimary = Color(0xFF1565C0)
private val BlueLight = Color(0xFFE3F2FD)
private val BlueSoft = Color(0xFF64B5F6)
private val BrownPrimary = Color(0xFF6D4C41)
private val BrownDark = Color(0xFF4E342E)
private val WhiteSoft = Color(0xFFFAFAFA)

// new
private val BlueSky = Color(0xFFBED9F4)
private val YellowLight = Color(0xFFFEFDD0)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    var currentPage by remember { mutableStateOf(0) }
    var showAddPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = YellowLight,

        topBar = {
            Surface(
                color = BrownPrimary,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TopButton(Icons.Filled.Home, currentPage == 0) { currentPage = 0 }
                    TopButton(Icons.Filled.DateRange, currentPage == 1) { currentPage = 1 }
                    TopButton(Icons.Filled.Check, currentPage == 2) { currentPage = 2 }
                    TopButton(Icons.Filled.Notifications, currentPage == 3) { currentPage = 3 }
                }
            }
        },

        floatingActionButton = {
            if (currentPage == 0) {
                FloatingActionButton(
                    onClick = { showAddPostDialog = true },
                    containerColor = BrownPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(YellowLight)
                .padding(16.dp)
        ) {

            when (currentPage) {
                0 -> HomeContent()
                1 -> Text("Calendar Screen", color = BrownDark)
                2 -> Text("To Do List Screen", color = BrownDark)
                3 -> Text("Message Screen", color = BrownDark)
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog { showAddPostDialog = false }
    }
}

// ================= TOP BUTTON =================

@Composable
fun TopButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) YellowLight    // เมื่อกดปุ่ม 4 ปุ่มบน
        else Color.Transparent

    val iconColor =
        if (selected) BrownPrimary      // ปุ่มบนสีน้ำตาล
        else WhiteSoft.copy(alpha = 0.6f)   // น้ำตาลจางลงตอนยังไม่เลือก

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
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
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WhiteSoft
                )
            ) {

                Column(modifier = Modifier.padding(18.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                text = members.joinToString { it.name },
                                color = BluePrimary,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = post.date.format(
                                    DateTimeFormatter.ofPattern("dd MMM yyyy")
                                ),
                                color = BrownPrimary
                            )
                        }

                        IconButton(
                            onClick = { AppState.deletePost(post) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = BrownDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        post.content,
                        color = Color.Black
                    )
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
    var showMemberSelector by remember { mutableStateOf(false) }

    val members = AppState.members
    val selectedMembers = remember { mutableStateListOf<Member>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp,
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

                    // 🔵 Member Button
                    Button(
                        onClick = { showMemberSelector = true },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrownDark,
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = when {
                                    selectedMembers.isEmpty() -> "Add Member"
                                    selectedMembers.size == 1 -> selectedMembers.first().name
                                    else -> "${selectedMembers.size} Members"
                                }
                            )

                            if (selectedMembers.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.KeyboardArrowDown, null)
                            }
                        }
                    }

                    // 🟤 Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDatePicker = true }
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = BrownPrimary
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = selectedDate.toString(),
                            color = BrownDark
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
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueSoft,
                        cursorColor = BluePrimary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BrownDark)
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
                            containerColor = BrownPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    )

    // 📅 Date Picker
    if (showDatePicker) {

        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", color = BluePrimary) }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = BluePrimary,
                    todayDateBorderColor = BrownPrimary
                )
            )
        }
    }

    // Member dialogs (logic เดิม)
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onMemberAdded = { member -> selectedMembers.add(member) }
        )
    }

    if (showMemberSelector) {

        AlertDialog(
            onDismissRequest = { showMemberSelector = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            confirmButton = {},
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Member", color = BrownPrimary)

                    IconButton(
                        onClick = {
                            showMemberSelector = false
                            showAddMemberDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, null, tint = BrownPrimary)
                    }
                }
            },
            text = {

                Column {

                    if (members.isEmpty()) {
                        Text("No members yet", color = BrownDark)
                    }

                    members.forEach { member ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedMembers.contains(member))
                                        selectedMembers.remove(member)
                                    else
                                        selectedMembers.add(member)
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = selectedMembers.contains(member),
                                onCheckedChange = {
                                    if (it) selectedMembers.add(member)
                                    else selectedMembers.remove(member)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BluePrimary
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(member.name, color = BrownDark)
                        }
                    }
                }
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
    ) { uri: Uri? -> imageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        confirmButton = {},
        dismissButton = {},
        title = null,
        text = {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = BrownDark,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(95.dp)
                        .background(BlueSoft, CircleShape) // รูปโปรไฟล์สำหรับ add
                        .clickable { launcher.launch("image/*") }
                ) {

                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(95.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Member Name") },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueSoft,
                        cursorColor = BluePrimary
                    )
                )

                Spacer(modifier = Modifier.height(26.dp))

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
                        containerColor = BrownPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save")
                }
            }
        }
    )
}






