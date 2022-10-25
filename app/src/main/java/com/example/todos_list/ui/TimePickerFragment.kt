package com.example.todos_list.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.todos_list.TodoApplication
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory
import java.util.*

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {
    val viewModel: TodoViewModel by activityViewModels {
        TodoViewModelFactory(
            requireActivity().application,
            (activity?.application as TodoApplication).database.todoDao(),
            (activity?.application as TodoApplication).database.todoCategoryDao()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(
            activity,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        viewModel.setReminderTime(hourOfDay, minute)
    }

    override fun onCancel(dialog: DialogInterface) {
        viewModel.resetTodo()
    }
}