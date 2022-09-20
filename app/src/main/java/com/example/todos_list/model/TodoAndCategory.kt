package com.example.todos_list.model

import androidx.room.Embedded
import androidx.room.Relation


data class TodoAndCategory(
    @Embedded val todo: Todo,

    @Relation(
        parentColumn = "category_name",
        entityColumn = "name"
    )
    val category: TodoCategory
)