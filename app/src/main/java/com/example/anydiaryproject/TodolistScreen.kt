package com.example.anydiaryproject

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Colors
private val BluePrimary = Color(0xFF4DB8FF)
private val BlueLight = Color(0xFFA7D8FF)
private val BrownDark = Color(0xFF8B5E3C)
private val BrownLight = Color(0xFFD2B48C)
private val PinkPastel = Color(0xFFFFB6C1)
private val YellowPastel = Color(0xFFFFE599)
private val CreamWhite = Color(0xFFFFFDF7)
private val CardWhite = Color(0xFFFFFFFF)
private val MintGreen = Color(0xFFB5EAD7)

@Composable
fun TodoContent() {
    val todos = AppState.todos

    if (todos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No tasks yet!",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrownLight
                )
                Text(
                    "Plan your next concert or event 🎶",
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(todos, key = { it.id }) { todo ->
            TodoCard(todo)
        }
    }
}

@Composable
fun TodoCard(todo: Todo) {
    val isOverdue = !todo.isDone && todo.date.isBefore(LocalDate.now())
    val isDueToday = !todo.isDone && todo.date == LocalDate.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                todo.isDone -> MintGreen.copy(alpha = 0.3f)
                isOverdue -> PinkPastel.copy(alpha = 0.2f)
                isDueToday -> YellowPastel.copy(alpha = 0.4f)
                else -> CardWhite
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (todo.isDone) MintGreen else BlueLight.copy(alpha = 0.5f)
                    )
                    .clickable { AppState.toggleTodo(todo) },
                contentAlignment = Alignment.Center
            ) {
                if (todo.isDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    fontWeight = FontWeight.SemiBold,
                    color = if (todo.isDone) BrownLight else BrownDark,
                    fontSize = 15.sp,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📅 ${todo.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                        fontSize = 11.sp,
                        color = when {
                            isOverdue -> PinkPastel
                            isDueToday -> Color(0xFFE6A700)
                            else -> BrownLight
                        }
                    )

                    if (isOverdue) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(PinkPastel, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "Overdue!",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isDueToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(YellowPastel, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "Today!",
                                fontSize = 9.sp,
                                color = BrownDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (todo.detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.detail,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Delete
            IconButton(
                onClick = { AppState.deleteTodo(todo) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = PinkPastel.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// =================== ADD TODO DIALOG ===================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = CreamWhite,
        tonalElevation = 8.dp,
        confirmButton = {},
        dismissButton = {},
        title = {
            Text(
                "📋 New Todo",
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("เช่น ไปคอนเสิร์ต / Title", color = BrownLight) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueLight,
                        cursorColor = BluePrimary,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    placeholder = { Text("รายละเอียด / Details 🎵", color = BrownLight) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = BlueLight,
                        cursorColor = BluePrimary,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

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
                            if (title.isNotBlank()) {
                                AppState.addTodo(
                                    title = title,
                                    detail = detail,
                                    date = selectedDate
                                )
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MintGreen,
                            contentColor = BrownDark
                        )
                    ) {
                        Text("✓ Save")
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
}