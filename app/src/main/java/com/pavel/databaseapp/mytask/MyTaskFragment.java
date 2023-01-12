package com.pavel.databaseapp.mytask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import com.github.techisfun.onelinecalendar.OnDateClickListener;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.adapter.taskadapter.TaskAdapter;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentMyTasksBinding;
import com.pavel.databaseapp.services.notification.UpdateTasksService;
import com.pavel.databaseapp.services.notification.UpdateTasksService.LocalBinder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyTaskFragment extends Fragment implements TaskAdapter.ViewHolder.OnTaskCompleteClickListener {
    MyTaskViewModel myTaskViewModel;
    FragmentMyTasksBinding binding;
    TaskAdapter taskAdapter;

    private UpdateTasksService tasksService;
    private boolean taskServiceBind = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder localBinder = (LocalBinder) service;
            tasksService = localBinder.getService();
            if(tasksService != null){
                //Установка значений для сервиса
                tasksService.setUpdatedTasks(myTaskViewModel.getMutableTasks());
                tasksService.setEmployee(myTaskViewModel.getEmployee());
                tasksService.setStatus(myTaskViewModel.getStatus());

                tasksService.startMonitoring();
                taskServiceBind = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            taskServiceBind = false;
        }
    };
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
        taskCalendarInit();
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
                if(myTaskViewModel.getPickedDate() != null)
                    taskAdapter.filterByDate(myTaskViewModel.getPickedDate());

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

    public void taskCalendarInit(){
        binding.taskCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClicked(@NonNull Date date) {
                //Toast.makeText(getContext(), date.toString(), Toast.LENGTH_SHORT).show();
                //DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                DateFormat parseFormat = new SimpleDateFormat(getResources().getString(R.string.date_format));
                Toast.makeText(getContext(), parseFormat.format(date), Toast.LENGTH_SHORT).show();
                try {
                    Date parseDate = parseFormat.parse(parseFormat.format(date));
                    taskAdapter.filterByDate(parseDate);
                    myTaskViewModel.setPickedDate(parseDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getContext(), String.valueOf(taskAdapter.getFilterItemCount()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(),UpdateTasksService.class);
        boolean autoUpdate = getContext().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
            //tasksService.startMonitoring();
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unbindService(serviceConnection);
        if (taskServiceBind) taskServiceBind = false;
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(getContext(),
                taskAdapter.getItemByPosition(position).taskName,
                Toast.LENGTH_SHORT).show();
        myTaskViewModel.completeTaskUpload(taskAdapter.getItemByPosition(position));
        taskAdapter.completeTask(position);
    }
}
