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

import com.github.techisfun.onelinecalendar.OnDateClickListener;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.adapter.taskadapter.TaskAdapter;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentMyTasksBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyTaskFragment extends Fragment implements TaskAdapter.ViewHolder.OnTaskCompleteClickListener {
    protected MyTaskViewModel myTaskViewModel;
    protected FragmentMyTasksBinding binding;
    TaskAdapter taskAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myTaskViewModel =  new ViewModelProvider(getActivity()).get(MyTaskViewModel.class);
        myTaskViewModel.init();
        taskAdapter = null;

        binding = FragmentMyTasksBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusInit();
        downloadTasks();
        freshTasksListInit();
        taskCalendarInit();
        tasksMsgInit();

        myTaskViewModel.downloadTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);

        MyTaskViewModel.setTaskListIsActive(true);
        myTaskViewModel.synchronizeTaskList();
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
        //myTaskViewModel.downloadTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
        binding.uploadBar.setVisibility(View.VISIBLE);
        Observer<List<Task>> tasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks == null || tasks.size() <= 0) {
                    myTaskViewModel.getMsgWall().setValue(getResources().getString(R.string.empty_total_task_list));
                    return;
                }
                if(taskAdapter == null) {
                    taskAdapter = new TaskAdapter(getContext(), tasks, R.layout.task_item_2);
                    taskAdapter.setTaskCompleteListener(getCurrentFragment());
                    binding.tasksList.setAdapter(taskAdapter);
                }
                taskAdapter.addNewTasks(tasks);//Добавить новые даты при синзронизации и тд
                //отсортировать задачи по дате после синхронизации если нужно
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
                myTaskViewModel.downloadTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
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
                if(taskAdapter == null) {
                    myTaskViewModel.getMsgWall().setValue(getResources().getString(R.string.empty_total_task_list));
                    return;
                }
                DateFormat parseFormat = new SimpleDateFormat(getResources().getString(R.string.date_format));
                Toast.makeText(getContext(), parseFormat.format(date), Toast.LENGTH_SHORT).show();
                try {
                    Date parseDate = parseFormat.parse(parseFormat.format(date));
                    taskAdapter.filterByDate(parseDate);
                    myTaskViewModel.setPickedDate(parseDate);

                    myTaskViewModel.getMsgWall().setValue("");
                    if(taskAdapter.getFilterList() == null || taskAdapter.getFilterList().size() == 0)
                        myTaskViewModel.getMsgWall().setValue(
                                String.format(getResources()
                                        .getString(R.string.empty_filter_task_list), myTaskViewModel.getPickedDate()
                                        .toString()));
                        //После фильтрации проверка задач
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getContext(), String.valueOf(taskAdapter.getFilterItemCount()), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Переделать с помощью Observer как Status + Toast
    public void tasksMsgInit(){
        /*
        if(taskAdapter.getFilterList().size() == 0) {
            binding.notFoundTaskMsg.setVisibility(View.VISIBLE);
            binding.notFoundTaskMsg.setText(String.format(
                    getResources()
                            .getString(R.string.empty_filter_task_list), myTaskViewModel.getPickedDate()
                            .toString()));
        }
        else if(taskAdapter.getTaskList().size() == 0){
            binding.notFoundTaskMsg.setVisibility(View.VISIBLE);
            binding.notFoundTaskMsg.setText(
                    getResources().getString(R.string.empty_total_task_list));
        }
        else binding.notFoundTaskMsg.setVisibility(View.GONE);
        binding.uploadBar.setVisibility(View.INVISIBLE);
         */
        Observer<String> foundTaskMsgObserver = new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                binding.notFoundTaskMsg.setVisibility(View.VISIBLE);
                binding.notFoundTaskMsg.setText(msg);
                binding.uploadBar.setVisibility(View.INVISIBLE);
            }
        };
        myTaskViewModel.getMsgWall().observe(getViewLifecycleOwner(),foundTaskMsgObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyTaskViewModel.setTaskListIsActive(false);
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
