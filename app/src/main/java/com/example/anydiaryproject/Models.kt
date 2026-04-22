package com.example.anydiaryproject

import java.time.LocalDate

data class Member(
    val id: Int,
    val name: String,
    val imageUri: String? = null,
    val isFavorite: Boolean = false
)

data class Post(
    val id: Int,
    val memberIds: List<Int>,
    val content: String,
    val date: LocalDate,
    val imageUri: String? = null
)

data class Expense(
    val id: Int,
    val title: String = "",
    val amount: Double,
    val memberIds: List<Int> = emptyList(),
    val date: LocalDate = LocalDate.now()
)

data class Todo(
    val id: Int,
    val title: String,
    val detail: String = "",
    val date: LocalDate = LocalDate.now(),
    val isDone: Boolean = false
)
