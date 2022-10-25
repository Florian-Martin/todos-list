package com.example.todos_list.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.todos_list.TodoApplication
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val todoViewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            requireActivity().application,
            (activity?.application as TodoApplication).database.todoDao(),
            (activity?.application as TodoApplication).database.todoCategoryDao()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        todoViewModel.setReminderDate(year, month + 1, day)
        TimePickerFragment().show(parentFragmentManager, "timePicker")
    }

}