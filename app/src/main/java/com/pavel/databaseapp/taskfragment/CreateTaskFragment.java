package com.pavel.databaseapp.taskfragment;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentCreateTaskBinding;
import com.pavel.databaseapp.dialog.EmployeeSearchDialog;
import com.pavel.databaseapp.dialog.EmployeeViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateTaskFragment extends Fragment {
    FragmentCreateTaskBinding binding;
    CreateTaskViewModel taskViewModel;
    EmployeeViewModel employeeViewModel;

    public static CreateTaskFragment newInstance() {
        CreateTaskFragment fragment = new CreateTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(this).get(CreateTaskViewModel.class);
        employeeViewModel = new ViewModelProvider(getActivity()).get(EmployeeViewModel.class);
        employeeViewModel.mutableInit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputEmployeeMonitoringInit();//Для отслеживания ввода имени работника
        searchEmployeeInit();//Поиск работников, открытие окна при нажатии на лупу и тд
        datePickerInit();//Инициализация выбора даты
        sendDataFilter();//фильтрация отправляемых данных, проверка
        initFilterStatus();//для отображения статуса отправки
   }
   private void datePickerInit(){
       //Читай события низу вверх
       Calendar actualCalendar = Calendar.getInstance();
       //Запоминают выбранные даты для их сравнения
       Calendar startDateCalendar = Calendar.getInstance();
       Calendar endDateCalendar = Calendar.getInstance();
       SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
       DatePickerDialog.OnDateSetListener endDatePickListener = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               endDateCalendar.set(year,month,dayOfMonth);
               if (endDateCalendar.before(startDateCalendar))
                   Toast.makeText(getContext(), "Не корректно выбраны даты", Toast.LENGTH_SHORT).show();
               else {
                   String startDateStr = dateFormat.format(startDateCalendar.getTime());
                   String endDateStr = dateFormat.format(endDateCalendar.getTime());
                   binding.startDate.setText(startDateStr);
                   binding.endDate.setText(endDateStr);
               }
           }
       };
       DatePickerDialog.OnDateSetListener startDatePickListener = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               startDateCalendar.set(year,month,dayOfMonth);
               DatePickerDialog endDatePicker = new DatePickerDialog(
                       getContext(), endDatePickListener,
                       actualCalendar.get(Calendar.YEAR),
                       actualCalendar.get(Calendar.MONTH),
                       actualCalendar.get(Calendar.DAY_OF_MONTH));
               endDatePicker.setTitle("Дата завершения работы");
               endDatePicker.show();
           }
       };
        binding.dateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog startDatePicker = new DatePickerDialog(
                        getContext(),startDatePickListener,
                        actualCalendar.get(Calendar.YEAR),
                        actualCalendar.get(Calendar.MONTH),
                        actualCalendar.get(Calendar.DAY_OF_MONTH));
                startDatePicker.setTitle("Дата начала работы");
                startDatePicker.show();
            }
        });

   }

   private void searchEmployeeInit(){
       binding.searchEmployee.setOnClickListener((v) ->{
           EmployeeSearchDialog searchDialog = new EmployeeSearchDialog();
           searchDialog.show(getParentFragmentManager(),"search_fragment");
       });
       Observer<Employee> pickedEmployeeObserver = new Observer<Employee>() {
           @Override
           public void onChanged(Employee employee) {
               binding.employee.setText(employeeViewModel.getPickedEmployee().getValue().name);
           }
       };
       employeeViewModel.getPickedEmployee().observe(getViewLifecycleOwner(),pickedEmployeeObserver);
   }

   private void inputEmployeeMonitoringInit(){
        binding.employee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(" *"))
                    binding.searchEmployee.setColorFilter(getContext()
                            .getResources()
                            .getColor(com.beardedhen.androidbootstrap.R.color.bootstrap_gray_lighter));
                else
                    binding.searchEmployee
                            .setColorFilter(getContext()
                                    .getResources().getColor(com.beardedhen.androidbootstrap.R.color.bootstrap_brand_success));
            }
        });
   }
   private void sendDataFilter(){
        binding.sendTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task(
                        binding.description.getText().toString(),
                        binding.taskName.getText().toString(),
                        binding.startDate.getText().toString(),
                        binding.endDate.getText().toString(),
                        binding.employee.getText().toString(),
                        binding.creatorName.getText().toString());
                taskViewModel.taskIsValid(task);
            }
        });
   }

   private void initFilterStatus(){
        Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("ok")) Toast.makeText(getContext(), "Задача отправлена", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        };
        taskViewModel.getTaskValidationStatus().observe(getViewLifecycleOwner(),statusObserver);
   }

}