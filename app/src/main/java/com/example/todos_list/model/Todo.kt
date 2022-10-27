package com.example.todos_list.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "Todo", foreignKeys = [ForeignKey(
        entity = TodoCategory::class,
        childColumns = ["category_name"],
        parentColumns = ["name"]
    )]
)
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "edited")
    val edited: Int = 0,

    @ColumnInfo(name = "priority")
    val priority: String,

    @ColumnInfo(name = "reminder")
    val reminder: String = ""
)