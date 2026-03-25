package com.example.anydiaryproject

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TodoContent() {
    val todos = AppState.todos
    val (completed, pending) = todos.partition { it.isDone }

    Column(Modifier.fillMaxSize().padding(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 100.dp)) {
        if (todos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(64.dp).background(BlueSoft.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = BlueBright, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("No things to remember", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text("Tap + to add", fontSize = 14.sp, color = TextGrey)
                }
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            if (pending.isNotEmpty()) {
                item { Text("Pending (${pending.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextGrey, modifier = Modifier.padding(vertical = 4.dp)) }
                items(pending, key = { it.id }) { TodoRow(it) }
            }
            if (completed.isNotEmpty()) {
                item { Text("Completed (${completed.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextGrey, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) }
                items(completed, key = { it.id }) { TodoRow(it) }
            }
        }
    }
}

@Composable
fun TodoRow(item: Todo) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.98f else 1f, label = "s")

    val overdue = !item.isDone && item.date.isBefore(LocalDate.now())
    val badge = when { item.isDone -> "Completed"; overdue -> "Overdue"; item.date == LocalDate.now() -> "Today"; else -> "Upcoming" }
    val badgeColor = when { item.isDone -> TextGrey; overdue -> StatusRed; else -> BlueBright }

    Card(
        Modifier.fillMaxWidth().scale(sc).cardShadow(if (item.isDone) 2.dp else 6.dp, RoundedCornerShape(16.dp))
            .clickable(interactionSource = src, indication = null as androidx.compose.foundation.Indication?) { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (item.isDone) CardWhite.copy(0.8f) else CardWhite)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(24.dp).clip(CircleShape).background(if (item.isDone) BlueBright else FieldBg)
                    .bouncyClick { AppState.toggleTodo(item) },
                contentAlignment = Alignment.Center
            ) { if (item.isDone) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp)) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = if (item.isDone) TextGrey else TextDark,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None)
                if (item.detail.isNotEmpty()) {
                    Text(item.detail, fontSize = 13.sp, color = TextLight,
                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(6.dp), color = badgeColor.copy(alpha = if (item.isDone) 0.15f else 1f)) {
                        Text(badge, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            color = if (item.isDone) TextGrey else Color.White)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(item.date.format(DateTimeFormatter.ofPattern("dd MMM")), fontSize = 11.sp, color = TextLight)
                }
            }

            IconButton(onClick = { AppState.deleteTodo(item) }, Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Delete, "Delete", tint = TextLight, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =================== ADD TODO ===================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(24.dp), containerColor = CardWhite,
        modifier = Modifier.fillMaxWidth().cardShadow(16.dp, RoundedCornerShape(24.dp)),
        confirmButton = {}, dismissButton = {},
        title = { Text("Things to Remember", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 20.sp) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text("Title", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Things to remember (e.g. concert, tickets)...", color = TextLight, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueBright, unfocusedBorderColor = FieldBg,
                        cursorColor = BlueBright, focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextDark),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                Spacer(Modifier.height(12.dp))

                Text("Details", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    placeholder = { Text("Details...", color = TextLight, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueBright, unfocusedBorderColor = FieldBg,
                        cursorColor = BlueBright, focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextDark),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
                )

                Spacer(Modifier.height(12.dp))

                Text("Due Date", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(bottom = 6.dp))
                Surface(modifier = Modifier.fillMaxWidth().bouncyClick { showDatePicker = true }, shape = RoundedCornerShape(14.dp), color = FieldBg) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, null, tint = BlueBright, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "Select Date",
                            color = if (selectedDate == null) TextGrey else TextDark, fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onDismiss, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, TextLight)) {
                        Text("Cancel", color = TextGrey)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = { if (title.isNotBlank()) { AppState.addTodo(title, detail, selectedDate ?: LocalDate.now()); onDismiss() } },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                        modifier = Modifier.cardShadow(4.dp, RoundedCornerShape(14.dp))
                    ) { Text("Add", fontWeight = FontWeight.Bold, color = Color.White) }
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
}