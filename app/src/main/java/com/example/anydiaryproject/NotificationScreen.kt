package com.example.anydiaryproject

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun NotificationContent() {
    val due = AppState.getDueNotifications()
    val overdue = due.filter { it.date.isBefore(LocalDate.now()) }
    val today = due.filter { it.date == LocalDate.now() }

    Column(Modifier.fillMaxSize().padding(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 100.dp)) {
        if (due.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(64.dp).background(BlueSoft.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Notifications, null, tint = BlueBright, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("No Alerts", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text("Everything is caught up ✨", fontSize = 14.sp, color = TextGrey, textAlign = TextAlign.Center)
                }
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            if (overdue.isNotEmpty()) {
                item { Text("Overdue", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = StatusRed, modifier = Modifier.padding(vertical = 4.dp)) }
                items(overdue, key = { "o_${it.id}" }) { AlertCard(it, true) }
            }
            if (today.isNotEmpty()) {
                item { Text("Due Today", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BlueBright, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) }
                items(today, key = { "t_${it.id}" }) { AlertCard(it, false) }
            }
        }
    }
}

@Composable
fun AlertCard(task: Todo, isOverdue: Boolean) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.98f else 1f, label = "s")

    val accent = if (isOverdue) StatusRed else BlueBright
    val icon = if (isOverdue) Icons.Default.Warning else Icons.Outlined.CheckCircle

    Card(
        Modifier.fillMaxWidth().scale(sc).cardShadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(interactionSource = src, indication = null as androidx.compose.foundation.Indication?) { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(accent.copy(0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(if (isOverdue) "Overdue!" else "Due Today", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = accent)
                Spacer(Modifier.height(2.dp))
                Text(task.title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextDark)
                if (task.detail.isNotEmpty()) {
                    Text(task.detail, fontSize = 12.sp, color = TextGrey, maxLines = 1)
                }
            }
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(BlueBright)
                    .bouncyClick { AppState.toggleTodo(task) },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Check, "Done", tint = Color.White, modifier = Modifier.size(16.dp)) }
        }
    }
}
