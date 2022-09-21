package com.example.todos_list.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todos_list.R
import com.example.todos_list.TodoApplication
import com.example.todos_list.ui.AddOrEditTodoFragmentArgs
import com.example.todos_list.databinding.FragmentAddOrEditTodoBinding
import com.example.todos_list.model.TodoAndCategory
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory
import java.util.*

class AddOrEditTodoFragment : Fragment() {

    /**************************************
     * PROPERTIES
     *************************************/
    private val navigationArgs: AddOrEditTodoFragmentArgs by navArgs()
    private var _binding: FragmentAddOrEditTodoBinding? = null
    private val binding get() = _binding!!
    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            (activity?.application as TodoApplication).database.todoDao(),
            (activity?.application as TodoApplication).database.todoCategoryDao(),
        )
    }
    private val categoryOptions = mutableListOf<String>()


    /**************************************
     * LIFECYCLE
     *************************************/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = todoViewModel
        val id = navigationArgs.todoId

        // Populating categories spinner
        todoViewModel.allTodoCategories.observe(this.viewLifecycleOwner) { todosCategories ->
            categoryOptions.clear()
            todosCategories.let {
                it.forEach {
                    categoryOptions.add(it.name)
                }

                val adapter = ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryOptions
                )
                binding.todoCategorySpinner.adapter = adapter
            }
        }
        binding.todoSaveButton.setOnClickListener { addNewTodo() }

        // Editing case
        if (id > 0) {
            todoViewModel.getTodoAndCategoryById(id).observe(this.viewLifecycleOwner) {
                bind(it)
            }
        } else { // Creating case
            binding.todoSaveButton.setOnClickListener { addNewTodo() }
        }
    }


    /**************************************
     * FUNCTIONS
     *************************************/
    private fun addNewTodo() {
        if (isTodoValid()) {
            setFieldsError(false)
            todoViewModel.addNewTodo(
                binding.todoNameEdit.text.toString(),
                binding.todoDescriptionEdit.text.toString(),
                binding.todoCategorySpinner.selectedItem.toString(),
            )
            val action =
                AddOrEditTodoFragmentDirections.actionAddOrEditTodoFragmentToTodosListFragment()
            findNavController().navigate(action)
        } else {
            setFieldsError(true)
        }
    }

    private fun isTodoValid(): Boolean {
        return todoViewModel.isTodoValid(
            binding.todoNameEdit.text.toString(),
            binding.todoCategorySpinner.selectedItemId.toString(),
            binding.todoPriorityRadioGroup.checkedRadioButtonId
        )
    }

    private fun bind(todoAndCategory: TodoAndCategory) {
           binding.apply {
            todoCategorySpinner.setSelection(categoryOptions.indexOf(todoAndCategory.todo.categoryName))
            todoNameEdit.setText(todoAndCategory.todo.name, TextView.BufferType.SPANNABLE)
            todoDescriptionEdit.setText(
                todoAndCategory.todo.description,
                TextView.BufferType.SPANNABLE
            )
            todoSaveButton.setOnClickListener { updateTodo(todoAndCategory.todo.id) }
        }
    }

    private fun updateTodo(todoId: Int) {
        if (isTodoValid()) {
            setFieldsError(false)
            todoViewModel.updateTodo(
                todoId,
                binding.todoNameEdit.text.toString(),
                binding.todoDescriptionEdit.text.toString(),
                binding.todoCategorySpinner.selectedItem.toString())
            val direction = AddOrEditTodoFragmentDirections.actionAddOrEditTodoFragmentToTodosListFragment()
            findNavController().navigate(direction)
        }
    }

    private fun setFieldsError(error: Boolean) {
        if (error) {
            binding.todoNameLabel.isErrorEnabled = true
            binding.todoNameLabel.error = getString(R.string.set_a_name)
        } else {
            binding.todoNameLabel.isErrorEnabled = false
            binding.todoNameLabel.error = null
        }
    }
}