package com.example.todos_list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todos_list.R
import com.example.todos_list.TodoApplication
import com.example.todos_list.databinding.FragmentAddOrEditTodoBinding
import com.example.todos_list.model.TodoAndCategory
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory

class AddOrEditTodoFragment : Fragment() {

    /**************************************
     * PROPERTIES
     *************************************/
    private val navigationArgs: AddOrEditTodoFragmentArgs by navArgs()
    private var _binding: FragmentAddOrEditTodoBinding? = null
    private val binding get() = _binding!!
    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            requireActivity().application,
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
        binding.lifecycleOwner = viewLifecycleOwner
        val id = navigationArgs.todoId

        // Populating categories spinner
        todoViewModel.allTodoCategories.observe(this.viewLifecycleOwner) { todosCategories ->
            categoryOptions.clear()
            todosCategories.let { todosCategories ->
                todosCategories.forEach { todoCategory ->
                    categoryOptions.add(todoCategory.name)
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
        binding.reminderEdit.setOnClickListener { showDatePickerDialog() }
        binding.cancelReminderButton.setOnClickListener { cancelReminder() }

        // Editing case
        if (id > 0) {
            todoViewModel.getTodoAndCategoryById(id).observe(this.viewLifecycleOwner) {
                bind(it)
            }
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
        return todoViewModel.isNameValid(binding.todoNameEdit.text.toString())
                && todoViewModel.isCategoryValid(binding.todoCategorySpinner.selectedItemId.toString())
                && todoViewModel.isPriorityValid(binding.todoPriorityRadioGroup.checkedRadioButtonId)
                && todoViewModel.isDateReminderValid(binding.reminderEdit.text.toString())
    }

    private fun bind(todoAndCategory: TodoAndCategory) {
        binding.apply {
            todoCategorySpinner.setSelection(categoryOptions.indexOf(todoAndCategory.todo.categoryName))
            todoNameEdit.setText(todoAndCategory.todo.name, TextView.BufferType.SPANNABLE)
            todoDescriptionEdit.setText(
                todoAndCategory.todo.description,
                TextView.BufferType.SPANNABLE
            )
            reminderEdit.setText(todoAndCategory.todo.reminder)
            todoSaveButton.setOnClickListener { updateTodo(todoAndCategory) }
        }
    }

    private fun updateTodo(todoAndCategory: TodoAndCategory) {
        if (isTodoValid()) {
            setFieldsError(false)
            todoViewModel.updateTodo(
                todoAndCategory,
                binding.todoNameEdit.text.toString(),
                binding.todoDescriptionEdit.text.toString(),
                binding.todoCategorySpinner.selectedItem.toString(),
            )
            val direction =
                AddOrEditTodoFragmentDirections.actionAddOrEditTodoFragmentToTodosListFragment()
            findNavController().navigate(direction)
        } else {
            setFieldsError(true)
        }
    }

    private fun setFieldsError(error: Boolean) {
        binding.todoNameLabel.isErrorEnabled = todoViewModel.isNameValid
        binding.reminderLabel.isErrorEnabled = todoViewModel.isReminderDateValid
        if (error) {
            if (!todoViewModel.isNameValid) {
                binding.todoNameLabel.error = getString(R.string.set_a_name)
            } else {
                binding.todoNameLabel.error = null
            }
            if (!todoViewModel.isReminderDateValid) {
                binding.reminderLabel.error = getString(R.string.set_a_valid_reminder)
            } else {
                binding.reminderLabel.error = null
            }
        } else {
            binding.todoNameLabel.error = null
            binding.reminderLabel.error = null
        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(parentFragmentManager, "datePicker")
    }

    private fun cancelReminder() {
        todoViewModel.cancelReminder()
    }
}