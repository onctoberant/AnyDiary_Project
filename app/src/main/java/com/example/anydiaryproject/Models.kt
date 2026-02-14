package com.example.anydiaryproject

import java.time.LocalDate

// class kotlin = มี logic ทำงาน
// data class เก็บข้อมูล
data class Member(
    val id: Int,
    val name: String,
    val imageUri: String? = null
)

data class Post(
    val id: Int,
    val memberIds: List<Int> = emptyList(),
    val content: String,
    val date: LocalDate
)

data class Todo(
    val id: Int,
    val title: String,
    val isDone: Boolean
)

data class NotificationItem(
    val id: Int,
    val message: String,
    val date: String
)
