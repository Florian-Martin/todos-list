package com.example.todos_list.viewmodel

import androidx.lifecycle.*
import com.example.to_do_list.utils.calendarToString
import com.example.todos_list.data.TodoCategoryDao
import com.example.todos_list.data.TodoDao
import com.example.todos_list.model.Todo
import com.example.todos_list.model.TodoAndCategory
import com.example.todos_list.model.TodoCategory
import kotlinx.coroutines.launch
import java.util.*

class TodoViewModel(
    private val todoDao: TodoDao,
    private val todoCategoryDao: TodoCategoryDao
) : ViewModel() {

    /**************************************
     * PROPERTIES
     *************************************/
    val allTodoCategories: LiveData<List<TodoCategory>> =
        todoCategoryDao.getAllTodoCategories().asLiveData()
    val allTodosAndCategory: LiveData<List<TodoAndCategory>> =
        todoDao.getAllTodosAndCategory().asLiveData()

    private var _todoPriority = MutableLiveData("normal")
    val todoPriority: LiveData<String>
        get() = _todoPriority


    /**************************************
     * FUNCTIONS
     *************************************/
    fun setTodoPriority(priority: String) {
        _todoPriority.value = priority
    }

    fun addNewTodo(
        todoName: String,
        todoDescription: String?,
        todoCategory: String
    ) {
        val newTodo = Todo(
            name = todoName,
            description = todoDescription,
            categoryName = todoCategory,
            date = calendarToString(Calendar.getInstance(), "yyyy-MM-dd"),
            edited = 0,
            priority = todoPriority.value!!
        )

        viewModelScope.launch {
            todoDao.insertTodo(newTodo)
        }

        setTodoPriority("normal")
    }

    fun isTodoValid(todoName: String, todoCategory: String, radioButtonId: Int): Boolean {
        return !(todoName.isBlank() || todoCategory.isBlank() || radioButtonId == -1)
    }

    fun getTodoAndCategoryById(id: Int): LiveData<TodoAndCategory> {
        return todoDao.getTodoAndCategoryById(id).asLiveData()
    }
}


class TodoViewModelFactory(
    private val todoDao: TodoDao,
    private val todoCategoryDao: TodoCategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao, todoCategoryDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}