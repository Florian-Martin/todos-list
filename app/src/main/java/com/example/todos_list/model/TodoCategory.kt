package com.example.todos_list.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TodoCategory")
data class TodoCategory (
    @PrimaryKey(autoGenerate = false)
    val name: String,

    @ColumnInfo(name = "color")
    val color: String
)