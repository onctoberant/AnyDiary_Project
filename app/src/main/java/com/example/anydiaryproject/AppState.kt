package com.example.anydiaryproject

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate

object AppState {

    val members = mutableStateListOf<Member>()
    val posts = mutableStateListOf<Post>()
    val todos = mutableStateListOf<Todo>()

    private var nextMemberId = 0
    private var nextPostId = 0
    private var nextTodoId = 0

    fun addMember(name: String, imageUri: String?): Member {
        val newMember = Member(
            id = nextMemberId++,
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
        posts.add(0, newPost)
    }

    fun deletePost(post: Post) {
        posts.remove(post)
    }

    fun addTodo(title: String, detail: String, date: LocalDate): Todo {
        val newTodo = Todo(
            id = nextTodoId++,
            title = title,
            detail = detail,
            date = date,
            isDone = false
        )
        todos.add(0, newTodo)
        return newTodo
    }

    fun toggleTodo(todo: Todo) {
        val index = todos.indexOf(todo)
        if (index != -1) {
            todos[index] = todo.copy(isDone = !todo.isDone)
        }
    }

    fun deleteTodo(todo: Todo) {
        todos.remove(todo)
    }

    fun getDueNotifications(): List<Todo> {
        val today = LocalDate.now()
        return todos.filter { !it.isDone && !it.date.isAfter(today) }
    }
}
