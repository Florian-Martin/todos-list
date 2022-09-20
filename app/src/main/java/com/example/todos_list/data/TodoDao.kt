package com.example.todos_list.data

import androidx.room.*
import com.example.todos_list.model.Todo
import com.example.todos_list.model.TodoAndCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM Todo ORDER BY name")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE id = :id")
    fun getTodoById(id: Int): Flow<Todo>

    @Query("SELECT * FROM Todo ORDER BY Todo.name")
    fun getAllTodosAndCategory(): Flow<List<TodoAndCategory>>

    @Query("SELECT * FROM Todo WHERE id = :id")
    fun getTodoAndCategoryById(id: Int): Flow<TodoAndCategory>
}
