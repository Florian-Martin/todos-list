package com.example.todos_list.data

import androidx.room.*
import com.example.todos_list.model.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoCategory(todoCategory: TodoCategory)

    @Update
    suspend fun updateTodoCategory(todoCategory: TodoCategory)

    @Delete
    suspend fun deleteTodoCategory(todoCategory: TodoCategory)

    @Query("SELECT * FROM TodoCategory ORDER BY name")
    fun getAllTodoCategories(): Flow<List<TodoCategory>>

    @Query("SELECT * FROM TodoCategory WHERE name = :name")
    fun getTodoCategoryByName(name: String): Flow<TodoCategory>
}