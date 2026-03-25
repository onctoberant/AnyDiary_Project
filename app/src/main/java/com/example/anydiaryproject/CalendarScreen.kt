package com.example.anydiaryproject

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Colors (same palette)
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
fun CalendarContent() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var selectedMemberFilter by remember { mutableStateOf<Member?>(null) }

    val posts = if (selectedMemberFilter == null) {
        AppState.posts
    } else {
        AppState.posts.filter { it.memberIds.contains(selectedMemberFilter!!.id) }
    }

    val daysWithPosts = posts.map { it.date }.toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Member filter chips
        if (AppState.members.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMemberFilter == null,
                        onClick = { selectedMemberFilter = null },
                        label = { Text("All", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
                items(AppState.members) { member ->
                    FilterChip(
                        selected = selectedMemberFilter == member,
                        onClick = {
                            selectedMemberFilter = if (selectedMemberFilter == member) null else member
                        },
                        label = { Text(member.name, fontSize = 12.sp) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(BlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (member.imageUri != null) {
                                    AsyncImage(
                                        model = member.imageUri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        member.name.take(1).uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary
                                    )
                                }
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPastel.copy(alpha = 0.5f),
                            selectedLabelColor = BrownDark
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
            }
        }

        // Month header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                    }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            tint = BrownDark
                        )
                    }

                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${currentMonth.year}",
                        fontWeight = FontWeight.Bold,
                        color = BrownDark,
                        fontSize = 18.sp
                    )

                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                    }) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = BrownDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Day-of-week headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    days.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrownLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calendar grid
                val firstOfMonth = currentMonth.atDay(1)
                val daysInMonth = currentMonth.lengthOfMonth()
                val startDayOfWeek = firstOfMonth.dayOfWeek.value % 7 // Sunday=0

                val totalCells = startDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val dayNum = cellIndex - startDayOfWeek + 1

                            if (dayNum in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayNum)
                                val hasPost = daysWithPosts.contains(date)
                                val isToday = date == LocalDate.now()
                                val isSelected = date == selectedDay

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                isSelected -> BluePrimary.copy(alpha = 0.2f)
                                                isToday -> YellowPastel.copy(alpha = 0.5f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .then(
                                            if (isToday) Modifier.border(
                                                1.5.dp,
                                                BluePrimary,
                                                RoundedCornerShape(10.dp)
                                            )
                                            else Modifier
                                        )
                                        .clickable { selectedDay = date },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$dayNum",
                                            fontSize = 14.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) BluePrimary else BrownDark
                                        )
                                        if (hasPost) {
                                            Text("❤️", fontSize = 8.sp)
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Posts for selected day
        if (selectedDay != null) {
            val dayPosts = posts.filter { it.date == selectedDay }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📝 Posts on ${selectedDay!!.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                fontWeight = FontWeight.Bold,
                color = BrownDark,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (dayPosts.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Text(
                        "No posts on this day",
                        modifier = Modifier.padding(16.dp),
                        color = BrownLight,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dayPosts, key = { it.id }) { post ->
                        val members = AppState.members.filter {
                            post.memberIds.contains(it.id)
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (members.isNotEmpty()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        members.forEach { member ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(BlueLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (member.imageUri != null) {
                                                        AsyncImage(
                                                            model = member.imageUri,
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .clip(CircleShape),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    } else {
                                                        Text(
                                                            member.name.take(1).uppercase(),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BluePrimary
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    member.name,
                                                    fontSize = 12.sp,
                                                    color = BrownDark
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                Text(
                                    post.content,
                                    color = Color(0xFF333333),
                                    fontSize = 14.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}