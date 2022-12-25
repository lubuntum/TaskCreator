package com.pavel.databaseapp.mytask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.adapter.taskadapter.TaskAdapter;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentMyTasksBinding;

import java.util.List;

public class MyTaskFragment extends Fragment implements TaskAdapter.ViewHolder.OnTaskCompleteClickListener {
    MyTaskViewModel myTaskViewModel;
    FragmentMyTasksBinding binding;
    TaskAdapter taskAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        myTaskViewModel =  new ViewModelProvider(getActivity()).get(MyTaskViewModel.class);
        myTaskViewModel.mutableInit();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyTasksBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myTaskViewModel.getTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
        statusInit();
        downloadTasks();
        freshTasksListInit();

    }
    public void statusInit(){
        Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(String status) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            }
        };
        myTaskViewModel.getStatus().observe(getViewLifecycleOwner(),statusObserver);

    }
    public void downloadTasks(){
        myTaskViewModel.getTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
        binding.uploadBar.setVisibility(View.VISIBLE);
        Observer<List<Task>> tasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                String activeTaskStr = String.format(getString(R.string.active_tasks_count), tasks.size());
                binding.activeTask.setText(activeTaskStr);
                taskAdapter = new TaskAdapter(getContext(),tasks, R.layout.task_item_2);
                taskAdapter.setTaskCompleteListener(getCurrentFragment());
                binding.tasksList.setAdapter(taskAdapter);

                binding.uploadBar.setVisibility(View.GONE);
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        };
        myTaskViewModel.getMutableTasks().observe(getViewLifecycleOwner(), tasksObserver);
    }
    public void freshTasksListInit(){
        binding.swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshContainer.setRefreshing(true);
                myTaskViewModel.getTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
            }
        });
    }
    //для применеия this в анонимных классах, поскольку там свой this, а нужен этот
    public MyTaskFragment getCurrentFragment(){
        return this;
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(getContext(),
                taskAdapter.getItemByPosition(position).taskName,
                Toast.LENGTH_SHORT).show();
        myTaskViewModel.completeTask(taskAdapter.getItemByPosition(position));
    }
}
