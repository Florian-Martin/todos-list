package com.example.todos_list.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todos_list.databinding.FragmentTodosListBinding

class TodosListFragment : Fragment() {

    private var _binding: FragmentTodosListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodosListBinding.inflate(inflater, container, false)
        return binding.root
    }
}