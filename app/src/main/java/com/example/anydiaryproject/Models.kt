package com.example.anydiaryproject

import java.time.LocalDate

// class kotlin = มี logic ทำงาน
// data class เก็บข้อมูล
data class Post(
    val id: Int,
    val memberId: Int,
    val date: LocalDate,
    val content: String
)

data class Member(
    val id: Int,
    val name: String,
    val imageUri: String
)

data class Todo(
    val id: Int,
    val title: String,
    val isDone: Boolean
)