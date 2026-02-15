package com.example.anydiaryproject

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate

object AppState {

    val members = mutableStateListOf<Member>()
    val posts = mutableStateListOf<Post>()

    private var nextMemberId = 0
    private var nextPostId = 0

    fun addMember(name: String, imageUri: String?): Member {

        val newMember = Member(
            id = nextMemberId++,   // 🔥 ใช้ตัวนับจริง
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
            id = nextPostId++,
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







