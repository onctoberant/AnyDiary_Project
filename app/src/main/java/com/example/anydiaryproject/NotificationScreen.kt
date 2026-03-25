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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
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
fun NotificationContent() {
    val dueItems = AppState.getDueNotifications()

    if (dueItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔔", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "All clear!",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrownLight
                )
                Text(
                    "No tasks due right now 🎉",
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
        item {
            Text(
                "⚠️ You have ${dueItems.size} pending task(s)",
                fontWeight = FontWeight.SemiBold,
                color = BrownDark,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(dueItems, key = { it.id }) { todo ->
            NotificationCard(todo)
        }
    }
}

@Composable
fun NotificationCard(todo: Todo) {
    var expanded by remember { mutableStateOf(false) }
    val isOverdue = todo.date.isBefore(LocalDate.now())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) PinkPastel.copy(alpha = 0.15f) else YellowPastel.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Alert icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isOverdue) listOf(PinkPastel, PinkPastel.copy(alpha = 0.5f))
                                else listOf(YellowPastel, YellowPastel.copy(alpha = 0.5f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isOverdue) "🔴" else "🔔",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todo.title,
                        fontWeight = FontWeight.SemiBold,
                        color = BrownDark,
                        fontSize = 15.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📅 ${todo.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                            fontSize = 11.sp,
                            color = if (isOverdue) PinkPastel else BrownLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isOverdue) PinkPastel else YellowPastel,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                if (isOverdue) "Overdue!" else "Due Today!",
                                fontSize = 9.sp,
                                color = if (isOverdue) Color.White else BrownDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Mark done button
                IconButton(
                    onClick = { AppState.toggleTodo(todo) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Mark Done",
                        tint = MintGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded detail
            if (expanded && todo.detail.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = BrownLight.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = todo.detail,
                    fontSize = 13.sp,
                    color = Color(0xFF555555),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
