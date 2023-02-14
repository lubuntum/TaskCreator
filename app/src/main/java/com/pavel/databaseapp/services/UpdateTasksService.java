package com.pavel.databaseapp.services;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.LinkedList;
import java.util.List;
//NO USE
public class UpdateTasksService extends LifecycleService {
    private final IBinder binder = new LocalBinder();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Task>> updatedTasks;
    private Employee employee;

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return binder;
    }

    public class LocalBinder extends Binder {
        public UpdateTasksService getService(){
            return UpdateTasksService.this;
        }
    }
    public void startMonitoring(){
        Runnable startMonitoringRnb = new Runnable() {
            @Override
            public void run() {
                try {//Приложение было запущено, обновление позже
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (UpdateTasksService.this.binder.isBinderAlive()) {
                    try {
                        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                                .whereEqualTo(Task.EMPLOYEE_MAIL,employee.mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty() && employee != null){
                                            List<Task> tasks = new LinkedList<>();
                                            List<DocumentSnapshot> docs = new LinkedList<>();
                                            for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()) {
                                                Task task = doc.toObject(Task.class);
                                                task.setId(doc.getId());
                                                tasks.add(task);
                                            }
                                            updatedTasks.postValue(tasks);
                                            Toast.makeText(UpdateTasksService.this, "Успешно", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Thread.sleep(35000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(startMonitoringRnb);
        thread.start();

        //thread.interrupt();
        //stopSelf();
    }

    public void setUpdatedTasks(MutableLiveData<List<Task>> updatedTasks) {
        this.updatedTasks = updatedTasks;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
