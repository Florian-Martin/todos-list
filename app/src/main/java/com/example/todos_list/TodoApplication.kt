package com.example.todos_list

import android.app.Application
import com.example.todos_list.data.TodosDatabase

class TodoApplication : Application(){
    val database: TodosDatabase by lazy { TodosDatabase.getDatabase(this) }
}