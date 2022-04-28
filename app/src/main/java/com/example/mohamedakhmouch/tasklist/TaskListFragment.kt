package com.example.mohamedakhmouch.tasklist

import TaskListAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mohamedakhmouch.databinding.FragmentTaskListBinding
import com.example.mohamedakhmouch.form.FormActivity
import com.example.mohamedakhmouch.network.Api
import kotlinx.coroutines.launch


class TaskListFragment : Fragment() {
    private val viewModel: TasksListViewModel by viewModels()


    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?

        if (task != null) {
            viewModel.create(task)
        }
    }

    private val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?

        if (task != null) {
            viewModel.update(task)
        }
    }

    private val adapter = TaskListAdapter()
    private lateinit var binding: FragmentTaskListBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Dans `onCreate` ou `onCreateView`:
        binding = FragmentTaskListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()!!
            val userInfoTextView = binding.textView
            userInfoTextView.text = "${userInfo.firstName} ${userInfo.lastName}"
        }
        viewModel.refresh()// on demande de rafraîchir les données sans attendre le retour directement
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, FormActivity::class.java)
            createTask.launch(intent)
        }


        adapter.onClickDelete = { task ->
            viewModel.delete(task)
        }

        adapter.onClickEdit = { task ->
            viewModel.update(task)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }

        binding.taskList.adapter = adapter
    }
}