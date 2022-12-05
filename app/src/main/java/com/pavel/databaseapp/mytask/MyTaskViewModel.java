package com.pavel.databaseapp.mytask;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.LinkedList;
import java.util.List;

public class MyTaskViewModel extends ViewModel {
    FirebaseFirestore firestore;
    private MutableLiveData<List<Task>> mutableTasks;
    private MutableLiveData<String> status;
    public MyTaskViewModel(){
        this.firestore = FirebaseFirestore.getInstance();
    }
    public void mutableInit(){
        this.mutableTasks = new MutableLiveData<>();
        this.status = new MutableLiveData<>();
    }
    public void getTaskByEmployeeName(String name){
        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                .whereEqualTo("employee",name)
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
