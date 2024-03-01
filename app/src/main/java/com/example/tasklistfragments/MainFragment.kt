package com.example.tasklistfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklistfragments.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskDataSource: TaskDataSource
    private lateinit var adapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        //создание объекта ресурсов
        if (!this::taskDataSource.isInitialized) {
            taskDataSource = TaskDataSource()
        }

        adapter = TasksAdapter(
            object : TaskActionListener {
                override fun onTaskDelete(task: Task) {
                    taskDataSource.deleteTask(task)
                }

                override fun onTaskSwap(currentPosition: Int, targetPosition: Int) {
                    taskDataSource.moveTask(currentPosition, targetPosition)
                }
            }
        )

        val layoutManager = LinearLayoutManager(requireActivity())

        //получаем начальный список задач
        adapter.tasks = taskDataSource.getTaskList()

        //прицепляем адаптер и менеджер макета
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        //создание декоратора для Drag&Drop
        val dividerItemDecoration =
            DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        val callback = DragManageAdapter(adapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN))
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(binding.recyclerView)

        //накидываем слушатель на ресурсы
        taskDataSource.addListener(taskListener)

        binding.buttonAddTask.setOnClickListener {
            replaceFragment(AddTaskFragment())
        }

        setFragmentResultByRequestKey("imageInfoFromAddTaskFragment")

        return binding.root
    }


    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun setFragmentResultByRequestKey(requestKey: String) {
        setFragmentResultListener(requestKey) { _, bundle ->
            val taskName = bundle.getString("taskName")
            val taskImage = bundle.getString("taskImage")

            taskDataSource.addTask(
                Task(
                    id = (taskDataSource.getTaskList().size + 1).toLong(),
                    name = taskName ?: "",
                    imageUrl = taskImage ?: ""
                )
            )
        }
    }

    private val taskListener: TaskListener = {
        adapter.tasks = it
    }

}