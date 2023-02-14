package com.pavel.databaseapp.employestasks;

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
import com.pavel.databaseapp.adapter.employadapter.EmployeeAdapter;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.FragmentEmployeesBinding;
import com.pavel.databaseapp.dialog.employee.EmployeeViewModel;

import java.util.List;

public class EmployeeFragment extends Fragment implements EmployeeAdapter.ViewHolder.OnEmployeeClickListener{
    EmployeeAdapter employeeAdapter;
    EmployeeViewModel viewModel;
    FragmentEmployeesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmployeesBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(EmployeeViewModel.class);
        viewModel.mutableInit();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uploadErrorObserverInit();
        uploadEmployeesObserverInit();

        employeeListInit();

    }
    public void uploadEmployeesObserverInit(){
        viewModel.getEmployeesMutable().observe(getViewLifecycleOwner(), new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employees) {
                employeeAdapter = new EmployeeAdapter(getContext(),employees,getFragmentContext());
                binding.employeeList.setAdapter(employeeAdapter);
                viewModel.setEmployees(employees);

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
    @Override
    public void onClick(int position) {
        //Toast.makeText(getContext(), viewModel.getEmployees().get(position).name, Toast.LENGTH_SHORT).show();
        //Прописать поиск задач выбранного работника здесь
        //Toast.makeText(getContext(), employeeAdapter.getFilterEmployees().get(position).mail, Toast.LENGTH_SHORT).show();
        //viewModel.getPickedEmployee().setValue(employeeAdapter.getFilterEmployees().get(position));
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment_content_home, EmployeeTaskFragment.newInstance(employeeAdapter.getFilterEmployees().get(position)),"employee_task")
                .setReorderingAllowed(true)
                .addToBackStack("employees_list_employee_task")
                .commit();
    }
    public EmployeeFragment getFragmentContext(){
        return this;
    }
}
