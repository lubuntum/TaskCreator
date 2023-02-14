package com.pavel.databaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pavel.databaseapp.employestasks.EmployeeTaskFragment;

public class EmployeesTasksActivity extends AppCompatActivity {
/*Вынести задачи сотрудников в отдельную активити (доделать)
* Разобраться почему при выборе даты пропадают задачи скорее всего поле во вью модел
* фрагмент в нав баре должен вызывать активити для запуска
* необходимо полностью разграничить EmployeesTasks и MyTasks даже ViewModel у них должна быть разная*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employes_tasks);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.employee_tasks_main_fragment,new EmployeeTaskFragment(),"employee_task")
                .addToBackStack("employee_task_transaction")
                .commit();
    }
}