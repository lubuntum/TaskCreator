package com.pavel.databaseapp.services.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.pavel.databaseapp.MainActivity;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.util.LinkedList;
import java.util.List;


public class NotificationService extends LifecycleService {
    public static final String CHANNEL_ID = "Notification service ID";
    public static final int NOTIFY_ID = 1001;

    public boolean isActive = true;
    private FirebaseFirestore firestore;
    private SharedPreferences preferences;
    private Employee employee;
    private List<Task> uncheckedTasks;
    private MutableLiveData<List<Task>> tasksMutable;
    //Иниц. сервера, настроек и текущего пользователя для последующих синхронизаций и проверок задач
    @Override
    public void onCreate() {
        super.onCreate();
        firestore = FirebaseFirestore.getInstance();
        preferences = getBaseContext().getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,MODE_PRIVATE);
        employee = new Gson().fromJson(
                preferences.getString(SettingsViewModel.EMPLOYEE,null),Employee.class);
        uncheckedTasks = new LinkedList<>();
        tasksMutable = new MutableLiveData<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable notificationRnb = new Runnable() {
            @Override
            public void run() {
                while (isActive){
                    Log.e("Notification", "Service is running");
                    downloadUncheckedTasks();

                    //updateCheckedTasks();
                    try {
                        Thread.sleep(25000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_accept_gray);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("Нет новых задач")
                    .setContentTitle("Новые задачи")
                    .setSmallIcon(R.drawable.icon_accept_green)
                    .setLargeIcon(bitmap);
            startForeground(NOTIFY_ID, notification.build());
        }
        Observer<List<Task>> taskDownload = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                if (taskList != null && taskList.size() > 0){
                    MainActivity.tasksCount += taskList.size();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_accept_gray);
                        Notification.Builder notification = new Notification.Builder(getService(), CHANNEL_ID)
                                .setContentText(String.format("У вас новые задачи: %d", MainActivity.tasksCount))
                                .setContentTitle("Новые задачи")
                                .setSmallIcon(R.drawable.icon_accept_green)
                                .setLargeIcon(bitmap);
                        startForeground(NOTIFY_ID, notification.build());
                        updateCheckedTasks();
                    }
                }
            }
        };
        tasksMutable.observe(getService(),taskDownload);
        Thread thread = new Thread(notificationRnb);
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
    public void updateCheckedTasks(){
        if (tasksMutable.getValue() == null) return;
        for(Task task: tasksMutable.getValue()){
            task.setChecked(true);
            firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                    .document(task.getId())
                    .set(task);
        }
        uncheckedTasks.clear();
    }
    public void downloadUncheckedTasks(){
        firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                .whereEqualTo(Task.EMPLOYEE_MAIL,employee.mail)
                .whereEqualTo(Task.IS_CHECKED,false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            //uncheckedTasks.clear();
                            List<Task> tasks = new LinkedList<>();
                            for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                                Task task = doc.toObject(Task.class);
                                task.setId(doc.getId());
                                tasks.add(task);
                            }
                            tasksMutable.postValue(tasks);
                        }
                    }
                });
    }
    public NotificationService getService(){
        return this;
    }
}
