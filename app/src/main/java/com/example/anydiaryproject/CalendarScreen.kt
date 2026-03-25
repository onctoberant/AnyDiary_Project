package com.example.anydiaryproject

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarContent() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var selectedMember by remember { mutableStateOf<Member?>(null) }

    val posts = if (selectedMember == null) AppState.posts
    else AppState.posts.filter { it.memberIds.contains(selectedMember!!.id) }

    val daysWithPosts = posts.map { it.date }.toSet()

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 100.dp)
    ) {
        // Member filter
        if (AppState.members.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    val all = selectedMember == null
                    Surface(
                        Modifier.bouncyClick { selectedMember = null },
                        shape = RoundedCornerShape(14.dp),
                        color = if (all) BlueBright else FieldBg
                    ) {
                        Text(
                            "All", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = if (all) Color.White else TextDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                items(AppState.members) { m ->
                    val sel = selectedMember == m
                    Surface(
                        Modifier.bouncyClick { selectedMember = if (sel) null else m },
                        shape = RoundedCornerShape(14.dp),
                        color = if (sel) BlueBright else FieldBg
                    ) {
                        Row(
                            Modifier.padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (sel) Color.White.copy(0.25f) else BlueSoft.copy(0.3f)), contentAlignment = Alignment.Center) {
                                if (m.imageUri != null) AsyncImage(m.imageUri, null, Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape), contentScale = ContentScale.Crop)
                                else Text(m.name.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else BrownDark)
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(m.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if (sel) Color.White else TextDark)
                            if (m.isFavorite) { Spacer(Modifier.width(3.dp)); Text("⭐", fontSize = 9.sp) }
                        }
                    }
                }
            }
        }

        // Calendar card
        Card(
            Modifier
                .fillMaxWidth()
                .cardShadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(Modifier.padding(18.dp)) {
                // Month nav
                Row(Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }, Modifier.size(32.dp)) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Prev", tint = BlueBright, modifier = Modifier.size(22.dp))
                    }
                    Text(
                        "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${currentMonth.year}",
                        fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }, Modifier.size(32.dp)) {
                        Icon(Icons.Default.KeyboardArrowRight, "Next", tint = BlueBright, modifier = Modifier.size(22.dp))
                    }
                }

                // Day headers
                Row(Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                        Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextGrey)
                    }
                }
                Spacer(Modifier.height(8.dp))

                val first = currentMonth.atDay(1)
                val days = currentMonth.lengthOfMonth()
                val startOff = first.dayOfWeek.value % 7
                val rows = (startOff + days + 6) / 7

                for (r in 0 until rows) {
                    Row(Modifier.fillMaxWidth()) {
                        for (c in 0..6) {
                            val d = r * 7 + c - startOff + 1
                            if (d in 1..days) {
                                val date = currentMonth.atDay(d)
                                val hasPost = daysWithPosts.contains(date)
                                val isToday = date == LocalDate.now()
                                val isSel = date == selectedDay

                                Box(
                                    Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (isSel) Modifier.background(BlueBright)
                                            else if (isToday) Modifier.border(
                                                1.5.dp,
                                                BrownLight,
                                                CircleShape
                                            )
                                            else Modifier
                                        )
                                        .bouncyClick { selectedDay = date },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "$d", fontSize = 14.sp,
                                            fontWeight = if (isSel || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.White else TextDark
                                        )
                                        if (hasPost) {
                                            Box(Modifier
                                                .padding(top = 1.dp)
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isSel) Color.White else StatusRed)) {}
                                        }
                                    }
                                }
                            } else Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Posts for selected day
        if (selectedDay != null) {
            val dayPosts = posts.filter { it.date == selectedDay }
            Box {
                Spacer(Modifier.height(16.dp))
            }

            if (dayPosts.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FieldBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No Memories Yet",
                        color = TextGrey,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(dayPosts, key = { it.id }) { post ->
                        val members = AppState.members.filter { post.memberIds.contains(it.id) }
                        val src = remember { MutableInteractionSource() }
                        val pressed by src.collectIsPressedAsState()
                        val sc by animateFloatAsState(if (pressed) 0.98f else 1f, label = "s")

                        Card(
                            Modifier
                                .fillMaxWidth()
                                .scale(sc)
                                .cardShadow(4.dp, RoundedCornerShape(14.dp))
                                .clickable(interactionSource = src, indication = null as androidx.compose.foundation.Indication?) { },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                if (members.isNotEmpty()) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(members) { m ->
                                            Surface(color = FieldBg, shape = RoundedCornerShape(10.dp)) {
                                                Row(Modifier.padding(start = 3.dp, end = 8.dp, top = 3.dp, bottom = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Box(Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(BlueSoft.copy(0.3f)), contentAlignment = Alignment.Center) {
                                                        if (m.imageUri != null) AsyncImage(m.imageUri, null, Modifier
                                                            .size(20.dp)
                                                            .clip(CircleShape), contentScale = ContentScale.Crop)
                                                        else Text(m.name.take(1).uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrownDark)
                                                    }
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(m.name, fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }
                                Text(post.content, color = TextDark, fontSize = 14.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}