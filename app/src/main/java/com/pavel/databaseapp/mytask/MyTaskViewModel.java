package com.pavel.databaseapp.mytask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.LinkedList;
import java.util.List;

public class MyTaskViewModel extends AndroidViewModel {
    FirebaseFirestore firestore;
    private MutableLiveData<List<Task>> mutableTasks;
    private MutableLiveData<String> status;
    private SharedPreferences preferences;
    private Employee employee;
    public MyTaskViewModel(Application app){
        super(app);
        this.firestore = FirebaseFirestore.getInstance();
        this.preferences = app.getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,Context.MODE_PRIVATE);

        Gson gson = new Gson();
        this.employee = gson.fromJson(preferences.getString(SettingsViewModel.EMPLOYEE,null),Employee.class);
    }
    public void mutableInit(){
        this.mutableTasks = new MutableLiveData<>();
        this.status = new MutableLiveData<>();
    }
    public void getTaskByEmployeeLogin(String mail){
        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                .whereEqualTo(Task.EMPLOYEE_MAIL,mail)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Task> tasks = new LinkedList<>();
                            for(QueryDocumentSnapshot doc: task.getResult())
                               tasks.add(Task.parse(doc));
                            mutableTasks.postValue(tasks);
                        }
                        else status.postValue("Возникла ошибка при загрузке данных");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        status.postValue("Пожалуйста проверьте интернет соединение");
                        //Log.d("TASK FAIL","Please check network connection");
                    }
                });
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getEditor(){
        return preferences.edit();
    }

    public Employee getEmployee() {
        return employee;
    }

    public MutableLiveData<List<Task>> getMutableTasks() {
        return mutableTasks;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}
