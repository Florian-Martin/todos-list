package com.example.todos_list.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todos_list.getOrAwaitValue
import com.example.todos_list.model.Todo
import com.example.todos_list.model.TodoCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TodosDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var todoCategoryDao: TodoCategoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TodosDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        todoDao = database.todoDao()
        todoCategoryDao = database.todoCategoryDao()
    }

    @After
    fun tearDown() = database.close()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertTodoTest() = runTest {
        val cat = TodoCategory("work", "test")
        todoCategoryDao.insertTodoCategory(cat)

        val todo = Todo(
            1,
            "test1",
            "test",
            "work",
            "2022-09-22",
            0,
            "low"
        )

        todoDao.insertTodo(todo)

        val allTodos = todoDao.getAllTodos().asLiveData().getOrAwaitValue()

        assertEquals(true, allTodos.contains(todo))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteTodoTest() = runTest {
        val cat = TodoCategory("work", "test")
        todoCategoryDao.insertTodoCategory(cat)

        val todo = Todo(
            1,
            "test1",
            "test",
            "work",
            "2022-09-22",
            0,
            "low"
        )
        todoDao.insertTodo(todo)
        todoDao.deleteTodo(todo)

        val allTodos = todoDao.getAllTodos().asLiveData().getOrAwaitValue()


        assertEquals(false, allTodos.contains(todo))
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateTodoTest() = runTest {
        val cat = TodoCategory("work", "test")
        todoCategoryDao.insertTodoCategory(cat)

        val todo = Todo(
            1,
            "test1",
            "test",
            "work",
            "2022-09-02",
            0,
            "low"
        )
        todoDao.insertTodo(todo)

        val updatedTodo = todo.copy(name = "successfully updated", edited = 1)
        todoDao.updateTodo(updatedTodo)

        val updatedTodoName = todoDao.getTodoById(1).asLiveData().getOrAwaitValue().name

        assertEquals("successfully updated", updatedTodoName)
    }
}