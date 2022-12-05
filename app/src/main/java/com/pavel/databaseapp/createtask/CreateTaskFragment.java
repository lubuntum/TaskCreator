package com.pavel.databaseapp.createtask;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.databinding.FragmentCreateTaskBinding;
import com.pavel.databaseapp.dialog.employee.EmployeeSearchDialog;
import com.pavel.databaseapp.dialog.employee.EmployeeViewModel;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.droidsonroids.gif.GifDrawable;

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
        ((GifDrawable)binding.sendTaskAnim.getDrawable()).stop();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inputEmployeeNameInit();//Для отслеживания ввода имени работника
        searchEmployeeInit();//Поиск работников, открытие окна при нажатии на лупу и тд
        datePickerInit();//Инициализация выбора даты
        sendTaskFilterInit();//фильтрация отправляемых данных, проверка
        sendTaskStatusInit();//статус после отправки сообщения успешно или нет
        startSendingTaskInit();//для отображения статуса перед отправкой + отправка
   }
   private void sendTaskStatusInit(){
        Observer<String> sendTaskObserver = new Observer<String>() {
            @Override
            public void onChanged(String status) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
                //binding.sendTaskAnim.setVisibility(View.INVISIBLE);
                //binding.sendTaskBtn.setVisibility(View.VISIBLE);
                //((GifDrawable)binding.sendTaskAnim.getDrawable()).reset();
                ((GifDrawable)binding.sendTaskAnim.getDrawable()).seekToBlocking(0);
                ((GifDrawable)binding.sendTaskAnim.getDrawable()).stop();
            }
        };
        taskViewModel.getTaskSendStatus().observe(getViewLifecycleOwner(),sendTaskObserver);
   }
   private void datePickerInit(){
       //Читай события низу вверх
       Calendar actualCalendar = Calendar.getInstance();
       //Запоминают выбранные даты для их сравнения
       Calendar startDateCalendar = Calendar.getInstance();
       Calendar endDateCalendar = Calendar.getInstance();
       DateFormat dateFormat = SimpleDateFormat.getDateInstance();
       DatePickerDialog.OnDateSetListener endDatePickListener = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               endDateCalendar.set(year,month,dayOfMonth);
               if (endDateCalendar.before(startDateCalendar))
                   Toast.makeText(getContext(), "Не корректно выбраны даты", Toast.LENGTH_SHORT).show();
               else {
                   String startDateStr = dateFormat.format(startDateCalendar.getTime()) + " ->";
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

   private void inputEmployeeNameInit(){
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
   private void sendTaskFilterInit(){
        binding.sendTaskAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task(
                        binding.description.getText().toString(),
                        binding.taskName.getText().toString(),
                        binding.startDate.getText().toString(),
                        binding.endDate.getText().toString(),
                        binding.employee.getText().toString(),
                        SettingsViewModel.CREATOR_NAME);
                taskViewModel.taskIsValid(task);
            }
        });
   }

   private void startSendingTaskInit(){
        Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Отправка..")) {
                    Toast.makeText(getContext(), "Все хорошо, отправляем", Toast.LENGTH_SHORT).show();
                    //binding.sendTaskAnim.setVisibility(View.VISIBLE);
                    ((GifDrawable)binding.sendTaskAnim.getDrawable()).start();
                    //binding.sendTaskBtn.setVisibility(View.GONE);
                    taskViewModel.sendTask();
                }
                else
                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }
        };
        taskViewModel.getTaskValidationStatus().observe(getViewLifecycleOwner(),statusObserver);
   }

}