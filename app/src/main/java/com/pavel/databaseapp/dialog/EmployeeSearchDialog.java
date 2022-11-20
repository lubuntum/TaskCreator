package com.pavel.databaseapp.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pavel.databaseapp.adapter.employadapter.EmployeeAdapter;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.EmployeeSearchBinding;

import java.util.List;

public class EmployeeSearchDialog extends DialogFragment {
    EmployeeAdapter employeeAdapter;
    EmployeeSearchBinding binding;
    EmployeeViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(EmployeeViewModel.class);
        viewModel.mutableInit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EmployeeSearchBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        uploadEmployeesObserverInit();
        uploadErrorObserverInit();
        employeeListInit();
    }
    public void employeeListInit(){
        //employeeAdapter = new EmployeeAdapter(getContext(), viewModel.getEmployees());
        //binding.employeeList.setAdapter(employeeAdapter);
        Runnable uploadEmployeesRnb = new Runnable() {
            @Override
            public void run() {
                viewModel.uploadEmployees();
            }
        };
        Thread thread = new Thread(uploadEmployeesRnb);
        thread.start();
        binding.swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "Запрос ресурсов...", Toast.LENGTH_SHORT).show();
                viewModel.setRefreshing(true);
                viewModel.uploadEmployees();
            }
        });
    }

    public void uploadEmployeesObserverInit(){
        viewModel.getEmployeesMutable().observe(getViewLifecycleOwner(), new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employees) {
                employeeAdapter = new EmployeeAdapter(getContext(),employees);
                binding.employeeList.setAdapter(employeeAdapter);

                binding.swipeRefreshContainer.setRefreshing(false);
                viewModel.setRefreshing(false);
            }
        });
    }

    public void uploadErrorObserverInit(){
        viewModel.getUploadState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean state) {
                if(!state) {
                    Toast.makeText(getContext(), "Ошибка при загрузки работников", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.uploadBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Работники успешно загружены", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
