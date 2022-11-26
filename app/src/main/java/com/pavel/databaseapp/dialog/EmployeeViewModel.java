package com.pavel.databaseapp.dialog;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pavel.databaseapp.data.Employee;

import java.util.LinkedList;
import java.util.List;

public class EmployeeViewModel extends ViewModel {
    private List<Employee> employees;
    private MutableLiveData<Employee> pickedEmployee;//выбор работника
    private FirebaseFirestore firestore;
    private MutableLiveData<Boolean> uploadState;//отслеживание загрузки
    private MutableLiveData<List<Employee>> employeesMutable;//результат загрузки, работники
    private boolean isRefreshing = false;

    public EmployeeViewModel(){
        this.employees = new LinkedList<>();
        this.firestore = FirebaseFirestore.getInstance();
    }
    public void mutableInit(){
        this.uploadState = new MutableLiveData<>();
        this.employeesMutable = new MutableLiveData<>();
        this.pickedEmployee = new MutableLiveData<>();
    }

    public void uploadEmployees(){
        /*Если работники уже были загружены, то отобразиться их список
        без обращаения к базе, да есть нюансы нужно все равно периодически
        проверять актуальность данных, но это позже
        * */
        if(!employees.isEmpty() && !isRefreshing) {//Кэширование
            employeesMutable.postValue(employees);
            uploadState.postValue(true);
            return;
        }
        List<Employee> tempList = new LinkedList<>();
        firestore.collection("employees")
                .get()
                .addOnCompleteListener(task ->{
                   if (!task.isSuccessful()) {
                       uploadState.postValue(false);
                       return;
                   }
                   for (QueryDocumentSnapshot document : task.getResult())
                       tempList.add(Employee.parse(document));

                   uploadState.setValue(true);
                   employeesMutable.postValue(tempList);
                });
    }

    public MutableLiveData<Employee> getPickedEmployee() {
        return pickedEmployee;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public MutableLiveData<List<Employee>> getEmployeesMutable() {
        return employeesMutable;
    }

    public MutableLiveData<Boolean> getUploadState() {
        return uploadState;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }
}
