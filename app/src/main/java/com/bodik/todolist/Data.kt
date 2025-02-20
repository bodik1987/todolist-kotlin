package com.bodik.todolist

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

data class Project(
    val id: String,
    val title: String,
)

//data class Chapter(
//    val id: String,
//    val title: String,
//    val projectId: String,
//    val items: List<Item> // List of Todo or Note items
//)

data class Todo(
    val id: String,
    val title: String,
    val type: String = "TODO",
    val isCompleted: Boolean,
    val dateOfCompletion: Date? = null,
    val description: String? = null,
    val chapterId: String,
    val projectId: String
)

data class Note(
    val id: String,
    val title: String,
    val type: String = "NOTE",
    val isCompleted: Boolean,
    val dateOfCompletion: Date? = null,
    val description: String? = null,
    val chapterId: String,
    val projectId: String
)

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    // Projects
    fun saveProjects(projects: List<Project>) {
        val editor = sharedPreferences.edit()
        editor.putInt("project_count", projects.size)

        projects.forEachIndexed { index, project ->
            editor.putString("project_${index + 1}_id", project.id)
            editor.putString("project_${index + 1}_title", project.title)
        }
        editor.apply()
    }

    fun loadProjects(): List<Project> {
        val projectCount = sharedPreferences.getInt("project_count", 0)
        val projects = mutableListOf<Project>()

        for (i in 1..projectCount) {
            val id = sharedPreferences.getString("project_${i}_id", "") ?: ""
            val title = sharedPreferences.getString("project_${i}_title", "") ?: ""

            if (id.isNotEmpty() && title.isNotEmpty()) {
                projects.add(Project(id, title))
            }
        }
        return projects
    }
}


