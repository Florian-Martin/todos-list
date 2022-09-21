package com.example.todos_list.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todos_list.model.Todo
import com.example.todos_list.model.TodoCategory

@Database(entities = [Todo::class, TodoCategory::class], version = 1, exportSchema = false)
abstract class TodosDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun todoCategoryDao(): TodoCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TodosDatabase? = null

        fun getDatabase(context: Context): TodosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodosDatabase::class.java,
                    "todos"
                )
                    .createFromAsset("database/todos.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}