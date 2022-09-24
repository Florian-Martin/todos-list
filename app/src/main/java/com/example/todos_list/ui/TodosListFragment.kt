package com.example.todos_list.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todos_list.R
import com.example.todos_list.TodoApplication
import com.example.todos_list.adapter.TodoAdapter
import com.example.todos_list.databinding.FragmentTodosListBinding
import com.example.todos_list.gesture.SwipeToDeleteCallback
import com.example.todos_list.viewmodel.TodoViewModel
import com.example.todos_list.viewmodel.TodoViewModelFactory

class TodosListFragment : Fragment() {

    /**************************************
     * PROPERTIES
     *************************************/
    private var _binding: FragmentTodosListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TodoAdapter

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
    ): View {
        _binding = FragmentTodosListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TodoAdapter {
            val direction =
                TodosListFragmentDirections.actionTodosListFragmentToTodoDetailFragment(it.todo.id)
            findNavController().navigate(direction)
        }

        binding.todosListRecyclerView.adapter = adapter
        swipeToDeleteHandling()

        todoViewModel.allTodosAndCategory.observe(this.viewLifecycleOwner) { todos ->
            todos.let {
                adapter.submitList(it)
            }
        }
        binding.todosListRecyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.todoAddFab.setOnClickListener {
            todoViewModel.setTodoPriority("normal")
            val direction =
                TodosListFragmentDirections.actionTodosListFragmentToAddOrEditTodoFragment(
                    getString(R.string.add_fragment_title),
                    -1
                )
            findNavController().navigate(direction)
        }
    }

    /**************************************
     * FUNCTIONS
     *************************************/
    private fun swipeToDeleteHandling() {
        val swipeTodDeleteCallback = object : SwipeToDeleteCallback(requireContext().applicationContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                val todoToDelete = adapter.currentList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    todoViewModel.deleteTodo(todoToDelete.todo)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeTodDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.todosListRecyclerView)
    }
}