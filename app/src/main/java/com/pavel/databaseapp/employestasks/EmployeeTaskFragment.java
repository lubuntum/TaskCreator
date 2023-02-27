package com.pavel.databaseapp.employestasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.FragmentMyTasksBinding;
import com.pavel.databaseapp.mytask.MyTaskFragment;
import com.pavel.databaseapp.mytask.MyTaskViewModel;

public class EmployeeTaskFragment extends MyTaskFragment {
    //private EmployeeViewModel employeeViewModel;//для мониторинга выбранного работника
    //private MyTaskViewModel myTaskViewModel;//для запросов данных работника
    //FragmentMyTasksBinding binding;
    public static EmployeeTaskFragment newInstance(Employee employee){
        Bundle args = new Bundle();
        args.putSerializable("Employee",employee);
        EmployeeTaskFragment taskFragment = new EmployeeTaskFragment();
        taskFragment.setArguments(args);
        return taskFragment;
    }
    public static EmployeeTaskFragment newInstance(){
        return new EmployeeTaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //employeeViewModel = new ViewModelProvider(getActivity()).get(EmployeeViewModel.class);
        myTaskViewModel = new ViewModelProvider(this).get(MyTaskViewModel.class);
        myTaskViewModel.init();
        if(getArguments() != null)
            myTaskViewModel.setEmployee((Employee) getArguments().getSerializable("Employee"));
        binding = FragmentMyTasksBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        myTaskViewModel.downloadTaskByEmployeeLogin(myTaskViewModel.getEmployee().mail);
        statusInit();
        downloadTasks();
        freshTasksListInit();
        taskCalendarInit();
        tasksMsgInit();
    }
    
    
}
