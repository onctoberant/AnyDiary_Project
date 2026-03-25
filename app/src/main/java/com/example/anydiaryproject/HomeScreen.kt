package com.example.anydiaryproject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

// =================== COLOR PALETTE ===================
private val BluePrimary = Color(0xFF4DB8FF)
private val BlueLight = Color(0xFFA7D8FF)
private val BrownLight = Color(0xFFD2B48C)
private val BrownDark = Color(0xFF8B5E3C)
private val YellowPastel = Color(0xFFFFE599)
private val PinkPastel = Color(0xFFFFB6C1)
private val MintGreen = Color(0xFFB5EAD7)
private val CreamWhite = Color(0xFFFFFDF7)
private val CardWhite = Color(0xFFFFFFFF)

// =================== HOME SCREEN (MAIN SHELL) ===================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var currentPage by remember { mutableIntStateOf(0) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf(false) }

    val notificationCount = AppState.getDueNotifications().size

    Scaffold(
        containerColor = CreamWhite,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp),
                color = CardWhite,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        icon = Icons.Filled.Home,
                        label = "Home",
                        selected = currentPage == 0,
                        onClick = { currentPage = 0 }
                    )
                    BottomNavItem(
                        icon = Icons.Filled.DateRange,
                        label = "Calendar",
                        selected = currentPage == 1,
                        onClick = { currentPage = 1 }
                    )
                    BottomNavItem(
                        icon = Icons.Filled.CheckCircle,
                        label = "Todo",
                        selected = currentPage == 2,
                        onClick = { currentPage = 2 }
                    )
                    BottomNavItem(
                        icon = Icons.Filled.Notifications,
                        label = "Alert",
                        selected = currentPage == 3,
                        badgeCount = notificationCount,
                        onClick = { currentPage = 3 }
                    )
                }
            }
        },
        floatingActionButton = {
            when (currentPage) {
                0 -> FloatingActionButton(
                    onClick = { showAddPostDialog = true },
                    containerColor = BrownDark,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
                2 -> FloatingActionButton(
                    onClick = { showAddTodoDialog = true },
                    containerColor = MintGreen,
                    contentColor = BrownDark,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Todo")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(CreamWhite)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(BlueLight, PinkPastel.copy(alpha = 0.5f))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                val title = when (currentPage) {
                    0 -> "🎤 My Diary"
                    1 -> "📅 Calendar"
                    2 -> "✅ Todo List"
                    3 -> "🔔 Notifications"
                    else -> ""
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrownDark
                    )
                )
            }

            // Content
            when (currentPage) {
                0 -> HomeContent()
                1 -> CalendarContent()
                2 -> TodoContent()
                3 -> NotificationContent()
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog { showAddPostDialog = false }
    }
    if (showAddTodoDialog) {
        AddTodoDialog { showAddTodoDialog = false }
    }
}

// =================== BOTTOM NAV ITEM ===================
@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (selected) BluePrimary else BrownLight,
        label = "nav_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .size(16.dp)
                        .background(PinkPastel, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(BluePrimary)
            )
        }
    }
}

// =================== HOME CONTENT (POST LIST) ===================
@Composable
fun HomeContent() {
    val posts = AppState.posts

    if (posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎤", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No diary entries yet!",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrownLight
                )
                Text(
                    "Tap + to record your concert memories",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrownLight.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(posts, key = { it.id }) { post ->
            PostCard(post)
        }
    }
}

// =================== POST CARD ===================
@Composable
fun PostCard(post: Post) {
    val members = AppState.members.filter { post.memberIds.contains(it.id) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Date + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(YellowPastel, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "📅 ${post.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrownDark
                        )
                    }
                }
                IconButton(
                    onClick = { AppState.deletePost(post) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = PinkPastel,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Members row
            if (members.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(members) { member ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(56.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BlueLight)
                                    .border(2.dp, BluePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (member.imageUri != null) {
                                    AsyncImage(
                                        model = member.imageUri,
                                        contentDescription = member.name,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = member.name.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = member.name,
                                fontSize = 10.sp,
                                color = BrownDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333),
                lineHeight = 22.sp
            )

            // Concert theme emoji footer
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text("❤️", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("⭐", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("🎤", fontSize = 12.sp)
            }
        }
    }
}

// =================== ADD POST DIALOG ===================
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
        containerColor = CreamWhite,
        tonalElevation = 8.dp,
        confirmButton = {},
        dismissButton = {},
        title = {
            Text(
                "✏️ New Diary Entry",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrownDark
                )
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Date selector
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    color = YellowPastel.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = BrownDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedDate.format(
                                DateTimeFormatter.ofPattern("dd MMMM yyyy")
                            ),
                            color = BrownDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Member selector button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMemberSelector = true },
                    shape = RoundedCornerShape(14.dp),
                    color = BlueLight.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BluePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                selectedMembers.isEmpty() -> "👥 Add Members (artists / friends)"
                                selectedMembers.size == 1 -> "👤 ${selectedMembers.first().name}"
                                else -> "👥 ${selectedMembers.size} members selected"
                            },
                            color = BrownDark
                        )
                    }
                }

                // Selected member chips
                if (selectedMembers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(selectedMembers.toList()) { member ->
                            AssistChip(
                                onClick = { selectedMembers.remove(member) },
                                label = { Text(member.name, fontSize = 12.sp) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = PinkPastel.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(50)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "วันนี้ไปเจอใคร ทำอะไรมา? 🎵\nWhat did you do today?",
                            color = BrownLight
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueLight,
                        cursorColor = BluePrimary,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BrownLight)
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
                            containerColor = BrownDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text("💾 Save")
                    }
                }
            }
        }
    )

    // Date Picker
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
                    todayDateBorderColor = BrownDark
                )
            )
        }
    }

    // Add new member dialog
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onMemberAdded = { member -> selectedMembers.add(member) }
        )
    }

    // Member selector dialog
    if (showMemberSelector) {
        AlertDialog(
            onDismissRequest = { showMemberSelector = false },
            containerColor = CreamWhite,
            shape = RoundedCornerShape(24.dp),
            confirmButton = {
                TextButton(onClick = { showMemberSelector = false }) {
                    Text("Done", color = BluePrimary)
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "👥 Select Members",
                        color = BrownDark,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            showMemberSelector = false
                            showAddMemberDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = BluePrimary
                        )
                    }
                }
            },
            text = {
                Column {
                    if (members.isEmpty()) {
                        Text(
                            "No members yet.\nTap + to add your first artist!",
                            color = BrownLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    members.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (selectedMembers.contains(member))
                                        selectedMembers.remove(member)
                                    else
                                        selectedMembers.add(member)
                                }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
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

                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (member.imageUri != null) {
                                    AsyncImage(
                                        model = member.imageUri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        member.name.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(member.name, color = BrownDark)
                        }
                    }
                }
            }
        )
    }
}

// =================== ADD MEMBER DIALOG ===================
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
        containerColor = CreamWhite,
        tonalElevation = 8.dp,
        confirmButton = {},
        dismissButton = {},
        title = {
            Text(
                "🎤 Add Member",
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Photo picker
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BlueLight, PinkPastel.copy(alpha = 0.5f))
                            )
                        )
                        .border(3.dp, BluePrimary, CircleShape)
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Text("📷", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("ชื่อ / Name", color = BrownLight) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueLight,
                        cursorColor = BluePrimary,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BrownLight)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
                            containerColor = BluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("✓ Save")
                    }
                }
            }
        }
    )
}
