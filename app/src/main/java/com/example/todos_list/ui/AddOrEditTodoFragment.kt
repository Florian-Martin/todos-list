package com.example.todos_list.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.todos_list.ui.AddOrEditTodoFragmentArgs
import com.example.todos_list.databinding.FragmentAddOrEditTodoBinding

class AddOrEditTodoFragment : Fragment() {

    private val navigationArgs: AddOrEditTodoFragmentArgs by navArgs()
    private var _binding: FragmentAddOrEditTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOrEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }
}