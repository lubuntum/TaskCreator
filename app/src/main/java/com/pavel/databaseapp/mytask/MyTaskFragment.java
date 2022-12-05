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

import com.pavel.databaseapp.adapter.taskadapter.TaskAdapter;
import com.pavel.databaseapp.createtask.CreateTaskViewModel;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentMyTasksBinding;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.List;

public class MyTaskFragment extends Fragment {
    MyTaskViewModel myTaskViewModel;
    FragmentMyTasksBinding binding;
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
        myTaskViewModel.getTaskByEmployeeName(SettingsViewModel.EMPLOYEE_NAME);
        statusInit();
        downloadTasks();
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
        myTaskViewModel.getTaskByEmployeeName(SettingsViewModel.EMPLOYEE_NAME);
        Observer<List<Task>> tasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                String activeTaskStr = String.format(binding.activeTask.getText().toString(),tasks.size());
                binding.activeTask.setText(activeTaskStr);
                TaskAdapter adapter = new TaskAdapter(getContext(),tasks);
                binding.tasksList.setAdapter(adapter);
            }
        };
        myTaskViewModel.getMutableTasks().observe(getViewLifecycleOwner(), tasksObserver);
    }
}
