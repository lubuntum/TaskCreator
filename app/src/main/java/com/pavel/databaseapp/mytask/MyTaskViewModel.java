package com.pavel.databaseapp.mytask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MyTaskViewModel extends AndroidViewModel {
    FirebaseFirestore firestore;
    private MutableLiveData<List<Task>> mutableTasks;
    private MutableLiveData<String> status;
    private MutableLiveData<String> msgWall;
    private SharedPreferences preferences;
    private Employee employee;
    private Date pickedDate;

    private static volatile boolean taskListIsActive = false;
    public MyTaskViewModel(Application app){
        super(app);
        this.firestore = FirebaseFirestore.getInstance();
        this.preferences = app.getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,Context.MODE_PRIVATE);

        Gson gson = new Gson();
        this.employee = gson.fromJson(preferences.getString(SettingsViewModel.EMPLOYEE,null),Employee.class);
    }
    public void init(){
        this.mutableTasks = new MutableLiveData<>();
        this.status = new MutableLiveData<>();
        this.msgWall = new MutableLiveData<>();

        this.pickedDate = null;
    }
    public void downloadTaskByEmployeeLogin(String mail){
        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                .whereEqualTo(Task.EMPLOYEE_MAIL,mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            List<Task> tasks = new LinkedList<>();
                            List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot doc: docs) {
                                Task task = doc.toObject(Task.class);
                                task.setId(doc.getId());
                                tasks.add(task);
                            }
                            mutableTasks.postValue(tasks);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        status.postValue("Пожалуйста проверьте интернет соединение");
                        //Log.d("TASK FAIL","Please check network connection");
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.getResult().getDocuments() == null ||
                                task.getResult().getDocuments().size() == 0)
                            msgWall.setValue("Пока у вас нет новых задач");
                    }
                });
    }
    public void completeTaskUpload(Task task){
        task.setComplete(true);
        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                .document(task.getId())
                .set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        status.postValue(String.format("Задача %s успешно выполнена",task.getTaskName()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        status.postValue("Ошибка" + e.toString());
                    }
                });
    }
    public void synchronizeTaskList(){
        Runnable updateRnb = new Runnable() {
            @Override
            public void run() {
                while (taskListIsActive){
                    try {
                        Thread.sleep(5000);
                        downloadTaskByEmployeeLogin(employee.mail);
                        status.postValue("Update succeed");
                        Thread.sleep(2000);
                    } catch (InterruptedException e){
                        status.postValue("Ошибка синхронизации");
                    }

                }
            }
        };
        Thread thread = new Thread(updateRnb);
        thread.start();
    }
    public List<Task> getActiveTasks(){
        if(mutableTasks.getValue() == null || mutableTasks.getValue().size() == 0){
            return null;
        }
        List<Task> activeTasks = new LinkedList<>();
        for (Task task: mutableTasks.getValue())
            if(!task.isComplete)  activeTasks.add(task);
        return activeTasks;
    }

    public Date getPickedDate() {
        return pickedDate;
    }

    public void setPickedDate(Date pickedDate) {
        this.pickedDate = pickedDate;
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

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public static void setTaskListIsActive(boolean taskListIsActive) {
        MyTaskViewModel.taskListIsActive = taskListIsActive;
    }
    public static boolean isTaskListIsActive() {
        return taskListIsActive;
    }

    public MutableLiveData<String> getMsgWall() {
        return msgWall;
    }

    public void setMsgWall(MutableLiveData<String> msgWall) {
        this.msgWall = msgWall;
    }
}
