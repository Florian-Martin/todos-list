package com.example.todos_list.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todos_list.R
import com.example.todos_list.TodoApplication
import com.example.todos_list.ui.TodoDetailFragmentArgs
import com.example.todos_list.databinding.FragmentTodoDetailBinding
import com.example.todos_list.model.TodoAndCategory
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TodoDetailFragment : Fragment() {

    /**************************************
     * PROPERTIES
     *************************************/
    private val navigationArgs: TodoDetailFragmentArgs by navArgs()
    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var mTodo: TodoAndCategory
    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            (activity?.application as TodoApplication).database.todoDao(),
            (activity?.application as TodoApplication).database.todoCategoryDao()
        )
    }


    /**************************************
     * LIFECYCLE
     *************************************/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.todoId
        todoViewModel.getTodoAndCategoryById(id).observe(this.viewLifecycleOwner) {
            mTodo = it
            bind(mTodo)
            todoViewModel.setTodoPriority(it.todo.priority)
        }
    }


    /**************************************
     * FUNCTIONS
     *************************************/
    private fun bind(todoAndCategory: TodoAndCategory) {

        val priorityColorIdentifier: Int = when (todoAndCategory.todo.priority) {
            "low" -> R.color.green
            "normal" -> R.color.orange
            "high" -> R.color.red
            else -> -1
        }

        val todoPriorityColor = ResourcesCompat.getColor(
            resources,
            priorityColorIdentifier,
            null
        )

        val editedTodo = if (todoAndCategory.todo.edited == 0) "created" else "edited"

        val date = resources.getString(
            R.string.todo_date,
            editedTodo,
            todoAndCategory.todo.date
        )

        binding.apply {
            todoDetailName.text = todoAndCategory.todo.name
            todoDetailDescription.text = todoAndCategory.todo.description
            todoDetailCategory.text = todoAndCategory.category.name
            todoDetailDate.text = date
            todoDetailPriority.setCardBackgroundColor(todoPriorityColor)
            todoEditButton.setOnClickListener { editTodo() }
            todoDeleteButton.setOnClickListener { showDeleteConfirmationDialog() }
        }
    }

    private fun editTodo() {
        val id = navigationArgs.todoId

        val direction = TodoDetailFragmentDirections.actionTodoDetailFragmentToAddOrEditTodoFragment(
            getString(R.string.edit_fragment_title),
            id
        )
        findNavController().navigate(direction)
    }

    private fun deleteTodo() {
        todoViewModel.deleteTodo(mTodo.todo)
        findNavController().navigateUp()
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_alert))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteTodo()
            }
            .show()
    }
}