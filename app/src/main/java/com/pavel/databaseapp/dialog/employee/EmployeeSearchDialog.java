package com.pavel.databaseapp.dialog.employee;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowMetrics;
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

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class EmployeeSearchDialog extends DialogFragment implements EmployeeAdapter.ViewHolder.OnEmployeeClickListener {
    EmployeeAdapter employeeAdapter;
    EmployeeSearchBinding binding;
    EmployeeViewModel viewModel;

    TextWatcher textWatcher;//Для мониторинга ввода текста

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(EmployeeViewModel.class);
        //viewModel.mutableInit();
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
        employeeNameInit();//Отслеживание ввода, фильтрация
        observeDialogLayoutChanges();
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

    @Override
    public void onClick(int position) {
        //Toast.makeText(getContext(), viewModel.getEmployees().get(position).name, Toast.LENGTH_SHORT).show();
        viewModel.getPickedEmployee()
                .setValue(employeeAdapter.getFilterEmployees().get(position));
        dismiss();
    }
    public void observeDialogLayoutChanges(){
        //Адаптация элементов диалогового окна под текущий размер
        binding.employeeSearchContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewGroup.LayoutParams params = binding.employeeNameContainer.getLayoutParams();
                        try {
                            params.width = (int) (requireActivity().getWindow().getDecorView().getWidth() * 0.7);
                        } catch (Exception e) {
                            params.width = 500;
                        }
                    }
                });
    }
    public void employeeNameInit(){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(employeeAdapter == null) return;
                if(s == null || s.toString().matches(" *") ){
                    employeeAdapter.showAll();
                    return;
                }
                employeeAdapter.filter(s.toString());
            }
        };
        binding.employeeName.addTextChangedListener(textWatcher);
    }
    public EmployeeSearchDialog getFragmentContext(){
        return this;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
