package com.example.anydiaryproject

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RobustImage(uri: String?, modifier: Modifier = Modifier) {
    if (uri == null) return
    val model = remember(uri) {
        when {
            uri.startsWith("file://") -> Uri.parse(uri)
            uri.startsWith("/") -> File(uri)
            else -> uri
        }
    }
    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var currentPage by remember { mutableIntStateOf(0) }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf(false) }

    val notificationCount = AppState.getDueNotifications().size

    Scaffold(
        containerColor = BgWarm,
        floatingActionButtonPosition = FabPosition.End,
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
                        .padding(bottom = 8.dp, end = 4.dp)
                        .size(72.dp)
                        .cardShadow(10.dp, CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Create", modifier = Modifier.size(36.dp))
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
    val tint by animateColorAsState(if (selected) BrownDark else TextGrey, label = "nav")
    val bg by animateColorAsState(if (selected) FieldBg else Color.Transparent, label = "bg")

    Box(
        modifier = Modifier
            .bouncyClick(onClick)
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box {
            Icon(icon, label, tint = tint, modifier = Modifier.size(24.dp))
            if (badge > 0) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-4).dp)
                        .size(14.dp).background(StatusRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("$badge", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
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
                                        RobustImage(member.imageUri, Modifier.fillMaxSize().clip(CircleShape))
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
                Box(Modifier.fillParentMaxHeight(0.75f).fillParentMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(64.dp).background(BlueSoft.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Edit, null, tint = BlueBright, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("No Memories Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        // Text("Start capturing your artist moments", fontSize = 14.sp, color = TextGrey)
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
            .cardShadow(if (isPressed) 16.dp else 12.dp, RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null as androidx.compose.foundation.Indication?) { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(Modifier.padding(18.dp)) {
            // Header: Avatars + Name + Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (members.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        members.take(3).forEach { m ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BlueSoft)
                                    .border(1.5.dp, CardWhite, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BlueSoft.copy(0.3f)), contentAlignment = Alignment.Center) {
                                    if (m.imageUri != null) RobustImage(m.imageUri, Modifier.fillMaxSize())
                                    else Text(m.name.take(1).uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrownDark)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = members.joinToString(", ") { it.name },
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark,
                        modifier = Modifier.weight(1f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text("MEMBER", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                }
                
                Text(
                    post.date.format(DateTimeFormatter.ofPattern("dd/MM/yy")),
                    fontSize = 11.sp, color = TextGrey
                )
            }
            
            Spacer(Modifier.height(14.dp))
            
            // Content Box
            val boxShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .cardShadow(2.dp, boxShape)
                    .background(Color(0xFFFAFAFA), boxShape)
                    .padding(14.dp)
            ) {
                Text(post.content, fontSize = 14.sp, color = TextDark, lineHeight = 22.sp)
            }

            if (post.imageUri != null) {
                Spacer(Modifier.height(12.dp))
                RobustImage(
                    post.imageUri,
                    Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(12.dp))

            // Delete
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(
                    Icons.Outlined.Delete, "Delete", 
                    tint = TextLight, 
                    modifier = Modifier.size(20.dp).clickable { AppState.deletePost(post) }
                )
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
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                try {
                    context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {}
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    if (inputStream != null) {
                        val file = java.io.File(context.filesDir, "post_${System.currentTimeMillis()}.jpg")
                        val outputStream = java.io.FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()
                        selectedImageUri = "file://" + file.absolutePath
                    } else {
                        selectedImageUri = it.toString()
                    }
                } catch (e: Exception) {
                    selectedImageUri = it.toString()
                }
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                // Header Row
                Row(
                    Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("CREATE\nPOST", fontWeight = FontWeight.ExtraBold, color = TextDark, fontSize = 16.sp, lineHeight = 18.sp)
                    
                    // Date Button
                    Surface(
                        Modifier.bouncyClick { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp), color = CardWhite,
                        border = BorderStroke(1.dp, TextLight)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.DateRange, null, tint = TextDark, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (selectedDate == LocalDate.now()) "select a date" else selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                color = TextDark, fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Member Selector Button
                Surface(
                    Modifier.fillMaxWidth().bouncyClick { showMemberSelector = true },
                    shape = RoundedCornerShape(12.dp), color = CardWhite,
                    border = BorderStroke(1.dp, TextLight)
                ) {
                    Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, null, tint = TextDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (selectedMembers.isEmpty()) "select member" else selectedMembers.joinToString { it.name },
                            color = TextDark, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Icon(Icons.Outlined.FavoriteBorder, null, tint = StatusRed, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Content
                Box {
                    OutlinedTextField(
                        value = content, onValueChange = { content = it },
                        placeholder = { Text("write your memory", color = TextGrey, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextLight, unfocusedBorderColor = TextLight,
                            cursorColor = TextDark, focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextDark, lineHeight = 22.sp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
                    )
                    
                    if (selectedImageUri != null) {
                        RobustImage(
                            selectedImageUri,
                            Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    IconButton(
                        onClick = { photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.Image, contentDescription = "Add Image", tint = TextDark, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Action Buttons
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("cancel", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                AppState.addPost(selectedMembers.map { it.id }, content, selectedDate, selectedImageUri)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownDark),
                        contentPadding = PaddingValues(horizontal = 32.dp)
                    ) { Text("save", color = CardWhite, fontSize = 14.sp) }
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
        confirmButton = {},
        text = {
            Column(Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 500.dp).padding(top = 8.dp)) {
                // Header Row
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SELECT\nMEMBER", fontWeight = FontWeight.ExtraBold, color = TextDark, fontSize = 16.sp, lineHeight = 18.sp)
                    Surface(
                        onClick = { showAdd = true }, shape = CircleShape,
                        color = CardWhite, border = BorderStroke(1.dp, TextLight), modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = TextDark, modifier = Modifier.padding(8.dp))
                    }
                }
                
                Spacer(Modifier.height(24.dp))

                // Members List
                if (members.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                         Text("No Members", color = TextGrey, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, false)) {
                        items(members) { member ->
                            val isSel = selectedMembers.contains(member)
                            Surface(
                                Modifier.fillMaxWidth().bouncyClick {
                                    if (isSel) selectedMembers.remove(member) else selectedMembers.add(member)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                border = BorderStroke(1.dp, TextLight)
                            ) {
                                Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    // Check Icon
                                    Box(
                                        Modifier.size(24.dp).clip(CircleShape).background(if (isSel) BrownDark else CardWhite)
                                            .border(1.dp, if (isSel) BrownDark else TextLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) { if (isSel) Icon(Icons.Default.Check, null, tint = CardWhite, modifier = Modifier.size(16.dp)) }
                                    
                                    Spacer(Modifier.width(14.dp))

                                    // Avatar
                                    Box(Modifier.size(32.dp).clip(CircleShape).background(FieldBg), contentAlignment = Alignment.Center) {
                                        if (member.imageUri != null) RobustImage(member.imageUri, Modifier.fillMaxSize().clip(CircleShape))
                                    }

                                    Spacer(Modifier.width(14.dp))
                                    Text(member.name, color = TextDark, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    
                                    Box(Modifier.clickable {
                                        AppState.deleteMember(member)
                                        selectedMembers.remove(member)
                                    }.padding(8.dp)) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete Member", tint = TextLight, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Actions
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("cancel", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = onDismiss, shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownDark), contentPadding = PaddingValues(horizontal = 32.dp)
                    ) { Text("done", color = CardWhite, fontSize = 14.sp) }
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
    var imageUri by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                if (inputStream != null) {
                    val file = java.io.File(context.filesDir, "member_${System.currentTimeMillis()}.jpg")
                    val outputStream = java.io.FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    imageUri = "file://" + file.absolutePath
                } else {
                    imageUri = it.toString()
                }
            } catch (e: Exception) {
                imageUri = it.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(24.dp), containerColor = CardWhite,
        modifier = Modifier.cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {},
        text = {
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("ADD\nMEMBER", fontWeight = FontWeight.ExtraBold, color = TextDark, fontSize = 16.sp, modifier = Modifier.fillMaxWidth(), lineHeight = 18.sp)
                
                Spacer(Modifier.height(32.dp))

                Box(
                    Modifier.size(80.dp).clip(CircleShape).background(CardWhite).border(1.dp, TextLight, CircleShape)
                        .bouncyClick { launcher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) }.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) RobustImage(imageUri, Modifier.fillMaxSize().clip(CircleShape))
                    else {
                        Icon(Icons.Outlined.Person, null, tint = TextDark, modifier = Modifier.size(36.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    placeholder = { Text("add member", color = TextGrey, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextLight, unfocusedBorderColor = TextLight,
                        cursorColor = TextDark, focusedContainerColor = CardWhite, unfocusedContainerColor = CardWhite
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextDark),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

                Spacer(Modifier.height(32.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("cancel", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onMemberAdded(AppState.addMember(name, imageUri))
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownDark), contentPadding = PaddingValues(horizontal = 32.dp)
                    ) { Text("done", color = CardWhite, fontSize = 14.sp) }
                }
            }
        }
    )
}
