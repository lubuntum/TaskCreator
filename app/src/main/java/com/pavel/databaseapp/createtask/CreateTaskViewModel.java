package com.pavel.databaseapp.createtask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

public class CreateTaskViewModel extends AndroidViewModel {
    private MutableLiveData<String> taskValidationStatus;
    private MutableLiveData<String> datePickStatus;
    private Task composeTask;
    private MutableLiveData<String> taskSendStatus;
    FirebaseFirestore firebase;
    private SharedPreferences preferences;
    private Employee employee;
    public CreateTaskViewModel(Application app){
        super(app);
        this.firebase = FirebaseFirestore.getInstance();
        this.preferences = app.getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE, Context.MODE_PRIVATE);
    }
    public void mutableInit(){
        this.taskValidationStatus = new MutableLiveData<>();
        this.datePickStatus = new MutableLiveData<>();
        this.taskSendStatus = new MutableLiveData<>();
    }
    public void taskIsValid(Task task){
        if (task.employee == null || task.employee.matches(" *")) taskValidationStatus.setValue("Укажите имя работника или выберите из списка");
        else if (task.taskName == null || task.taskName.matches(" *")) taskValidationStatus.setValue("Имя задачи пустое");
        else if (task.description == null || task.description.matches("[A-Za-z]{0,5}| *")) taskValidationStatus.setValue("Описание должно быть больше 5 символов");
        else if (task.startDate == null || task.endDate == null ||
                task.startDate.matches(" *") || task.endDate.matches(" *")) taskValidationStatus.setValue("Выбор даты обязателен");
        else if (task.creator == null) taskValidationStatus.setValue("Неизвестная ошибка, автор неуказан");
        else if (task.employeeMail.matches(" *") || task.employeeMail.matches("[A-Za-z_\\d]{2,20}@[A-Za-z]{2,10}\\.[A-Za-z]]"))
            taskValidationStatus.setValue("Неверный формат почты");
        else {
            this.composeTask = task;//сохраняем задачу
            taskValidationStatus.setValue("Отправка..");
        }
    }
    public void sendTask(){
        firebase.collection("tasks").add(composeTask).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                taskSendStatus.postValue("Задача отправлена");
                    //уведомить что данные отправлены
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskSendStatus.postValue("Не удается отправить задачу, проверьте интернет соединение");
                    //уведомить что данные не отправ
            }
        });
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
    public Employee getEmployee(){
        if (employee == null) {
            Gson gson = new Gson();
            employee = gson.fromJson(
                    preferences.getString(SettingsViewModel.EMPLOYEE, null), Employee.class);
        }
        return employee;
    }

    public MutableLiveData<String> getTaskValidationStatus() {
        return taskValidationStatus;
    }

    public MutableLiveData<String> getDatePickStatus() {
        return datePickStatus;
    }

    public MutableLiveData<String> getTaskSendStatus() {
        return taskSendStatus;
    }
}
