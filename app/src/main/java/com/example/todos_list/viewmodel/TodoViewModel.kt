package com.example.todos_list.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.to_do_list.utils.getTimeDifference
import com.example.to_do_list.utils.getTodayDate
import com.example.todos_list.data.TodoCategoryDao
import com.example.todos_list.data.TodoDao
import com.example.todos_list.model.Todo
import com.example.todos_list.model.TodoAndCategory
import com.example.todos_list.model.TodoCategory
import com.example.todos_list.worker.TodoReminderNotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TodoViewModel(
    application: Application,
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

    private val workManager = WorkManager.getInstance(application)

    private var _todoPriority = MutableLiveData("normal")
    val todoPriority: LiveData<String>
        get() = _todoPriority

    private var _isNameValid = true
    val isNameValid: Boolean
        get() = _isNameValid

    private var isCategoryValid = true

    private var isPriorityValid = true

    private var _isReminderDateValid = true
    val isReminderDateValid: Boolean
        get() = _isReminderDateValid

    private var _dateAndTimeReminder = MutableLiveData<String>("")
    val dateAndTimeReminder: LiveData<String>
        get() = _dateAndTimeReminder

    private var secondsBeforeReminder = 0L


    /**************************************
     * FUNCTIONS
     *************************************/
    fun addNewTodo(
        todoName: String,
        todoDescription: String?,
        todoCategory: String
    ) {
        val newTodo = Todo(
            name = todoName,
            description = todoDescription,
            categoryName = todoCategory,
            date = getTodayDate("yyyy-MM-dd"),
            priority = todoPriority.value!!
        )

        viewModelScope.launch {
            insertTodoInDb(newTodo)
            if (_dateAndTimeReminder.value != ""){
                val todo = getLastInsertedTodo()
                scheduleReminder(todo)
            }
        }
    }

    fun restoreTodo(
        todoName: String,
        todoDescription: String?,
        todoCategory: String,
        todoDate: String,
        edited: Int,
        todoPriority: String
    ) {
        val todoToRestore = Todo(
            name = todoName,
            description = todoDescription,
            categoryName = todoCategory,
            date = todoDate,
            edited = edited,
            priority = todoPriority
        )
        viewModelScope.launch { todoDao.insertTodo(todoToRestore) }
    }

    /**
     * Create a WorkRequest to display a notification to the user to remind him
     * the [Todo] he has set a reminder for.
     * @param todo = The Todo needing a reminder
     */
    private fun scheduleReminder(todo: Todo) {
        val data = workDataOf(
            "id" to todo.id,
            "name" to todo.name,
            "description" to todo.description
        )
        val notificationRequest = OneTimeWorkRequestBuilder<TodoReminderNotificationWorker>()
            .setInputData(data)
            .setInitialDelay(secondsBeforeReminder, TimeUnit.SECONDS)
            .addTag("${todo.name} notification")
            .build()

        workManager.enqueue(notificationRequest)
    }

    fun isNameValid(todoName: String): Boolean {
        _isNameValid = todoName.isNotBlank()
        return isNameValid
    }

    fun isCategoryValid(category: String): Boolean {
        isCategoryValid = category.isNotBlank()
        return isCategoryValid
    }

    fun isPriorityValid(radioButtonId: Int): Boolean {
        isPriorityValid = radioButtonId != -1
        return isPriorityValid
    }

    fun isDateReminderValid(text: String): Boolean {
        return if (text.isNotBlank()) {
            val pattern = "yyyy-MM-dd HH:mm:ss"
            secondsBeforeReminder = getTimeDifference(
                SimpleDateFormat(pattern, Locale.getDefault()),
                dateAndTimeReminder.value!!,
                getTodayDate(pattern),
                TimeUnit.SECONDS
            )
            _isReminderDateValid = secondsBeforeReminder > 0
            secondsBeforeReminder > 0
        } else {
            _isReminderDateValid = true
            true
        }
    }

    fun setTodoPriority(priority: String) {
        _todoPriority.value = priority
    }

    fun setReminderDate(year: Int, month: Int, day: Int) {
        _dateAndTimeReminder.value = "$year-$month-$day"
    }

    fun setReminderTime(hourOfDay: Int, minute: Int) {
        val h = if (hourOfDay.toString().length == 1) "0$hourOfDay" else hourOfDay
        val m = if (minute.toString().length == 1) "0$minute" else minute
        _dateAndTimeReminder.value += " $h:$m:00"
    }

    fun resetTodo() {
        setTodoPriority("normal")
        _dateAndTimeReminder.value = ""
    }

    private suspend fun insertTodoInDb(todo: Todo){
        withContext(Dispatchers.IO){
            todoDao.insertTodo(todo)
        }
    }

    private suspend fun getLastInsertedTodo(): Todo {
        return withContext(Dispatchers.IO){
            todoDao.getLastInsertedTodo()
        }
    }

    fun getTodoAndCategoryById(id: Int): LiveData<TodoAndCategory> {
        return todoDao.getTodoAndCategoryById(id).asLiveData()
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }

    fun updateTodo(id: Int, name: String, description: String, category: String) {
        val updatedTodo = getUpdatedTodo(id, name, description, category)
        viewModelScope.launch {
            todoDao.updateTodo(updatedTodo)
        }
    }

    private fun getUpdatedTodo(id: Int, name: String, description: String, category: String): Todo {
        return Todo(
            id,
            name,
            description,
            category,
            date = getTodayDate("yyyy-MM-dd"),
            1,
            todoPriority.value!!
        )
    }
}


class TodoViewModelFactory(
    private val application: Application,
    private val todoDao: TodoDao,
    private val todoCategoryDao: TodoCategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(application, todoDao, todoCategoryDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}