package com.example.anydiaryproject

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate

object AppState {

    val members = mutableStateListOf<Member>()
    val posts = mutableStateListOf<Post>()

    fun addMember(name: String, imageUri: String) {
        members.add(
            Member(
                id = members.size + 1,
                name = name,
                imageUri = imageUri
            )
        )
    }

    fun addPost(memberId: Int, content: String) {
        posts.add(
            Post(
                id = posts.size + 1,
                memberId = memberId,
                date = LocalDate.now(),
                content = content
            )
        )
    }
}
