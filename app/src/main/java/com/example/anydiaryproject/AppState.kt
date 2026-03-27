package com.example.anydiaryproject

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return LocalDate.parse(json.asString)
    }
}

object AppState {

    val members = mutableStateListOf<Member>()
    val posts = mutableStateListOf<Post>()
    val todos = mutableStateListOf<Todo>()

    private var nextMemberId = 0
    private var nextPostId = 0
    private var nextTodoId = 0

    private var prefs: SharedPreferences? = null
    private val gson: Gson = GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.getSharedPreferences("anydiary_prefs", Context.MODE_PRIVATE)
        loadData()
    }

    private fun loadData() {
        val p = prefs ?: return
        p.getString("members", null)?.let {
            val type = object : TypeToken<List<Member>>() {}.type
            val list: List<Member> = gson.fromJson(it, type)
            members.clear()
            members.addAll(list)
            nextMemberId = (list.maxOfOrNull { m -> m.id } ?: -1) + 1
        }
        
        p.getString("posts", null)?.let {
            val type = object : TypeToken<List<Post>>() {}.type
            val list: List<Post> = gson.fromJson(it, type)
            posts.clear()
            posts.addAll(list)
            nextPostId = (list.maxOfOrNull { p -> p.id } ?: -1) + 1
        }
        
        p.getString("todos", null)?.let {
            val type = object : TypeToken<List<Todo>>() {}.type
            val list: List<Todo> = gson.fromJson(it, type)
            todos.clear()
            todos.addAll(list)
            nextTodoId = (list.maxOfOrNull { t -> t.id } ?: -1) + 1
        }
    }

    private fun saveData() {
        prefs?.edit()?.apply {
            putString("members", gson.toJson(members))
            putString("posts", gson.toJson(posts))
            putString("todos", gson.toJson(todos))
            apply()
        }
    }

    fun addMember(name: String, imageUri: String?): Member {
        val newMember = Member(id = nextMemberId++, name = name, imageUri = imageUri)
        members.add(newMember)
        saveData()
        return newMember
    }

    fun deleteMember(member: Member) {
        members.remove(member)
        for (i in posts.indices) {
            val post = posts[i]
            if (post.memberIds.contains(member.id)) {
                posts[i] = post.copy(memberIds = post.memberIds.filter { it != member.id })
            }
        }
        saveData()
    }

    fun addPost(memberIds: List<Int>, content: String, date: LocalDate, imageUri: String? = null) {
        val newPost = Post(id = nextPostId++, memberIds = memberIds, content = content, date = date, imageUri = imageUri)
        posts.add(0, newPost)
        saveData()
    }

    fun deletePost(post: Post) {
        posts.remove(post)
        saveData()
    }

    fun addTodo(title: String, detail: String, date: LocalDate): Todo {
        val newTodo = Todo(id = nextTodoId++, title = title, detail = detail, date = date, isDone = false)
        todos.add(0, newTodo)
        saveData()
        return newTodo
    }

    fun toggleTodo(todo: Todo) {
        val index = todos.indexOf(todo)
        if (index != -1) {
            todos[index] = todo.copy(isDone = !todo.isDone)
            saveData()
        }
    }

    fun deleteTodo(todo: Todo) {
        todos.remove(todo)
        saveData()
    }

    fun toggleFavorite(member: Member) {
        val index = members.indexOf(member)
        if (index != -1) {
            members[index] = member.copy(isFavorite = !member.isFavorite)
            saveData()
        }
    }

    fun getDueNotifications(): List<Todo> {
        val today = LocalDate.now()
        return todos.filter { !it.isDone && !it.date.isAfter(today) }
    }
}
