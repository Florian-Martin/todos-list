package com.example.todos_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todos_list.R
import com.example.todos_list.databinding.TodoListItemBinding
import com.example.todos_list.model.TodoAndCategory

class TodoAdapter(private val onItemClicked: (TodoAndCategory) -> Unit) :
    ListAdapter<TodoAndCategory, TodoAdapter.TodoViewHolder>(DiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            TodoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onItemClicked(current) }
        holder.bind(current)
    }

    class TodoViewHolder(private var binding: TodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todoAndCategory: TodoAndCategory) {
            val colorIdentifier = itemView.resources.getIdentifier(
                todoAndCategory.category.color,
                "color",
                itemView.context.packageName
            )

            val todoCategoryColor = ResourcesCompat.getColor(
                this.itemView.resources,
                colorIdentifier,
                null
            )

            val priorityColorIdentifier: Int = when (todoAndCategory.todo.priority) {
                "low" -> R.color.green
                "normal" -> R.color.orange
                "high" -> R.color.red
                else -> -1
            }

            val todoPriorityColor = ResourcesCompat.getColor(
                this.itemView.resources,
                priorityColorIdentifier,
                null
            )

            val editedTodo = if (todoAndCategory.todo.edited == 0) "created" else "edited"

            val date = this.itemView.resources.getString(
                R.string.todo_date,
                editedTodo,
                todoAndCategory.todo.date
            )

            binding.apply {
                todoItemName.text = todoAndCategory.todo.name
                todoItemDescription.text = todoAndCategory.todo.description
                todoItemDate.text = date
                todoItemCategoryColor.setBackgroundColor(todoCategoryColor)
                todoItemCategory.text = todoAndCategory.category.name
                todoItemPriority.setCardBackgroundColor(todoPriorityColor)
            }
        }
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<TodoAndCategory>() {
            override fun areItemsTheSame(
                oldItem: TodoAndCategory,
                newItem: TodoAndCategory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: TodoAndCategory,
                newItem: TodoAndCategory
            ): Boolean {
                return oldItem.todo.name == newItem.todo.name &&
                        oldItem.todo.description == newItem.todo.description
            }

        }
    }

}