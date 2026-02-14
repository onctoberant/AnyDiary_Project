package com.example.anydiaryproject

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate

object AppState {

    val members = mutableStateListOf<Member>()
    val posts = mutableStateListOf<Post>()

    fun addMember(name: String, imageUri: String?): Member {
        val newMember = Member(
            id = members.size + 1,
            name = name,
            imageUri = imageUri
        )
        members.add(newMember)
        return newMember
    }

    fun addPost(
        memberIds: List<Int>,
        content: String,
        date: LocalDate
    ) {
        val newPost = Post(
            id = posts.size + 1,
            memberIds = memberIds,
            content = content,
            date = date
        )
        posts.add(newPost)
    }

    fun deletePost(post: Post) {
        posts.remove(post)
    }
}


val notifications = mutableStateListOf<NotificationItem>()

fun addNotification(message: String) {
    notifications.add(
        NotificationItem(
            id = notifications.size + 1,
            message = message,
            date = java.time.LocalDate.now().toString()
        )
    )
}


