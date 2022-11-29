package com.pavel.databaseapp.taskfragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;

public class CreateTaskViewModel extends ViewModel {
    private MutableLiveData<String> taskValidationStatus;
    private MutableLiveData<String> datePickStatus;
    public CreateTaskViewModel(){
        this.taskValidationStatus = new MutableLiveData<>();
        this.datePickStatus = new MutableLiveData<>();
    }

    public void taskIsValid(Task task){
        if (task.employee == null || task.employee.matches(" *")) taskValidationStatus.setValue("Укажите имя работника или выберите из списка");
        else if (task.taskName == null || task.taskName.matches(" *")) taskValidationStatus.setValue("Имя задачи пустое");
        else if (task.description == null || task.description.matches("[A-Za-z]{0,5}| *")) taskValidationStatus.setValue("Описание должно быть больше 5 символов");
        else if (task.startDate == null || task.endDate == null ||
                task.startDate.matches(" *") || task.endDate.matches(" *")) taskValidationStatus.setValue("Выбор даты обязателен");
        else if (task.creator == null) taskValidationStatus.setValue("Неизвестная ошибка, автор неуказан");
        else taskValidationStatus.setValue("ok");
    }

    public MutableLiveData<String> getTaskValidationStatus() {
        return taskValidationStatus;
    }

    public MutableLiveData<String> getDatePickStatus() {
        return datePickStatus;
    }
}
