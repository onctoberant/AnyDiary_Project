package com.example.anydiaryproject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.AsyncImage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var currentPage by remember { mutableIntStateOf(0) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf(false) }

    val notificationCount = AppState.getDueNotifications().size

    Scaffold(
        containerColor = BgWarm,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 6.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(Icons.Outlined.Home, "Home", currentPage == 0) { currentPage = 0 }
                    NavItem(Icons.Outlined.DateRange, "Calendar", currentPage == 1) { currentPage = 1 }
                    NavItem(Icons.Outlined.CheckCircle, "Todo", currentPage == 2) { currentPage = 2 }
                    NavItem(Icons.Outlined.Notifications, "Alerts", currentPage == 3, notificationCount) { currentPage = 3 }
                }
            }
        },
        floatingActionButton = {
            if (currentPage == 0 || currentPage == 2) {
                FloatingActionButton(
                    onClick = {
                        if (currentPage == 2) showAddTodoDialog = true else showAddPostDialog = true
                    },
                    containerColor = BrownDark,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .size(56.dp)
                        .cardShadow(10.dp, CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Create", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Shared top logo
            AppLogo()

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (currentPage) {
                    0 -> HomeContent()
                    1 -> CalendarContent()
                    2 -> TodoContent()
                    3 -> NotificationContent()
                }
            }
        }
    }

    if (showAddPostDialog) AddPostDialog { showAddPostDialog = false }
    if (showAddTodoDialog) AddTodoDialog { showAddTodoDialog = false }
}

@Composable
fun NavItem(icon: ImageVector, label: String, selected: Boolean, badge: Int = 0, onClick: () -> Unit) {
    val tint by animateColorAsState(if (selected) BlueBright else TextLight, label = "nav")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.bouncyClick(onClick).padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Box {
            Icon(icon, label, tint = tint, modifier = Modifier.size(24.dp))
            if (badge > 0) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-4).dp)
                        .size(14.dp).background(StatusRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("$badge", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = tint, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        if (selected) {
            Spacer(Modifier.height(4.dp))
            Box(Modifier.size(width = 20.dp, height = 3.dp).clip(CircleShape).background(BlueBright)) {}
        }
    }
}

// =================== HOME CONTENT ===================
@Composable
fun HomeContent() {
    val posts = AppState.posts
    val favorites = AppState.members.filter { it.isFavorite }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Favorite Artists row
        if (favorites.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)) {
                    Text("⭐ Favorites", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BrownDark)
                    Spacer(Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(favorites) { member ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(52.dp).clip(CircleShape)
                                        .background(BlueSoft.copy(alpha = 0.3f))
                                        .cardShadow(4.dp, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (member.imageUri != null) {
                                        AsyncImage(member.imageUri, null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                                    } else {
                                        Text(member.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(member.name, fontSize = 11.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }

        if (posts.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(64.dp).background(BlueSoft.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Edit, null, tint = BlueBright, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No Memories Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Start capturing your artist moments", fontSize = 14.sp, color = TextGrey)
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                PostCard(post, Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun PostCard(post: Post, modifier: Modifier = Modifier) {
    val members = AppState.members.filter { post.memberIds.contains(it.id) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "s")

    Card(
        modifier = modifier.fillMaxWidth().scale(scale)
            .cardShadow(if (isPressed) 10.dp else 6.dp, RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null as androidx.compose.foundation.Indication?) { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(Modifier.padding(18.dp)) {
            val memberText = if (members.isNotEmpty()) members.joinToString(", ") { it.name } else "MEMBER"
            Text(
                "${post.date.format(DateTimeFormatter.ofPattern("dd MM yy"))}   •   ${memberText.uppercase()}",
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BrownDark
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = FieldBg, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Content
            Text(post.content, fontSize = 14.sp, color = TextDark, lineHeight = 22.sp)

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = FieldBg, thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            // Delete
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = { AppState.deletePost(post) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp),
                    border = BorderStroke(1.dp, FieldBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete", fontSize = 11.sp, color = TextGrey)
                }
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
    var showMemberSelector by remember { mutableStateOf(false) }
    val selectedMembers = remember { mutableStateListOf<Member>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {},
        dismissButton = {},
        title = { Text("Create Memory", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 20.sp) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                // Date
                Text("Date", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                Surface(
                    Modifier.fillMaxWidth().bouncyClick { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp), color = FieldBg
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, null, tint = BlueBright, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), color = TextDark, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Members
                Text("Member", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                Surface(
                    Modifier.fillMaxWidth().bouncyClick { showMemberSelector = true },
                    shape = RoundedCornerShape(14.dp), color = FieldBg
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, null, tint = BlueBright, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (selectedMembers.isEmpty()) "Select Member" else "${selectedMembers.size} Member(s)",
                            color = if (selectedMembers.isEmpty()) TextGrey else TextDark, fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Content
                Text("Description", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    placeholder = { Text("Write your memory", color = TextLight, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueBright, unfocusedBorderColor = FieldBg,
                        cursorColor = BlueBright, focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextDark, lineHeight = 22.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
                )

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, shape = RoundedCornerShape(14.dp)) {
                        Text("Cancel", color = TextGrey, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                AppState.addPost(selectedMembers.map { it.id }, content, selectedDate)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                        modifier = Modifier.cardShadow(6.dp, RoundedCornerShape(14.dp))
                    ) { Text("Save", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }
    )

    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                    showDatePicker = false
                }) { Text("OK", color = BlueBright, fontWeight = FontWeight.Bold) }
            },
            colors = DatePickerDefaults.colors(containerColor = CardWhite)
        ) { DatePicker(state, colors = DatePickerDefaults.colors(selectedDayContainerColor = BlueBright)) }
    }

    if (showMemberSelector) {
        MemberSelectorDialog(AppState.members, selectedMembers) { showMemberSelector = false }
    }
}

// =================== MEMBER SELECTOR ===================
@Composable
fun MemberSelectorDialog(members: List<Member>, selectedMembers: MutableList<Member>, onDismiss: () -> Unit) {
    var showAdd by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = CardWhite, shape = RoundedCornerShape(24.dp),
        modifier = Modifier.cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {
            Button(onDismiss, colors = ButtonDefaults.buttonColors(containerColor = BlueBright), shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)) {
                Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Select Tags/People", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 20.sp)
                IconButton(onClick = { showAdd = true }, Modifier.background(FieldBg, CircleShape).size(36.dp)) {
                    Icon(Icons.Default.Add, "Add", tint = BlueBright, modifier = Modifier.size(20.dp))
                }
            }
        },
        text = {
            Column(Modifier.heightIn(max = 400.dp)) {
                if (members.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("ยังไม่มีรายการ\nแตะ + เพื่อเพิ่ม", color = TextGrey, textAlign = TextAlign.Center, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(members) { member ->
                            val isSel = selectedMembers.contains(member)
                            Surface(
                                Modifier.fillMaxWidth().bouncyClick {
                                    if (isSel) selectedMembers.remove(member) else selectedMembers.add(member)
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) BlueSoft.copy(alpha = 0.15f) else FieldBg,
                                border = if (isSel) BorderStroke(1.5.dp, BlueBright) else null
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    // Check
                                    Box(
                                        Modifier.size(22.dp).clip(CircleShape).background(if (isSel) BlueBright else TextLight.copy(0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) { if (isSel) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp)) }

                                    Spacer(Modifier.width(10.dp))

                                    // Avatar
                                    Box(Modifier.size(36.dp).clip(CircleShape).background(BlueSoft.copy(0.25f)), contentAlignment = Alignment.Center) {
                                        if (member.imageUri != null) AsyncImage(member.imageUri, null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                                        else Text(member.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 14.sp)
                                    }

                                    Spacer(Modifier.width(10.dp))
                                    Text(member.name, color = TextDark, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

                                    // Favorite toggle
                                    IconButton(
                                        onClick = { AppState.toggleFavorite(member) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            if (member.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                            "Favorite",
                                            tint = if (member.isFavorite) FavoriteStar else TextLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showAdd) AddMemberDialog(onDismiss = { showAdd = false }, onMemberAdded = { selectedMembers.add(it) })
}

// =================== ADD MEMBER ===================
@Composable
fun AddMemberDialog(onDismiss: () -> Unit, onMemberAdded: (Member) -> Unit) {
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(24.dp), containerColor = CardWhite,
        modifier = Modifier.cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {}, dismissButton = {},
        title = { Text("Add Tag / Person", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 20.sp) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.size(80.dp).clip(CircleShape).background(FieldBg).bouncyClick { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) AsyncImage(imageUri, null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Person, null, tint = BrownLight, modifier = Modifier.size(28.dp))
                        Text("Upload", fontSize = 10.sp, color = TextGrey)
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text("Name", fontSize = 12.sp, color = TextGrey, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    placeholder = { Text("ชื่อ (ศิลปิน, เพื่อน, สถานที่...)", color = TextLight) },
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueBright, unfocusedBorderColor = FieldBg,
                        cursorColor = BlueBright, focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = TextDark),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onDismiss, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, TextLight)) {
                        Text("Cancel", color = TextGrey)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) { onMemberAdded(AppState.addMember(name, imageUri?.toString())); onDismiss() }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                        modifier = Modifier.cardShadow(4.dp, RoundedCornerShape(14.dp))
                    ) { Text("Add", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }
    )
}
