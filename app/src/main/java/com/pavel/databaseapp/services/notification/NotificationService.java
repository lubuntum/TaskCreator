package com.pavel.databaseapp.services.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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

    public static final int REQUEST_CODE = 4041;

    public boolean isActive = true;
    private FirebaseFirestore firestore;
    private SharedPreferences preferences;
    private Employee employee;
    //public static List<Task> uncheckedTasks = new LinkedList<>();
    private MutableLiveData<List<Task>> tasksMutable;
    private List<Task> prevTasks = new LinkedList<>();
    private boolean noTasks = false;
    //Иниц. сервера, настроек и текущего пользователя для последующих синхронизаций и проверок задач
    @Override
    public void onCreate() {
        super.onCreate();
        firestore = FirebaseFirestore.getInstance();
        preferences = getBaseContext().getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,MODE_PRIVATE);
        employee = new Gson().fromJson(
                preferences.getString(SettingsViewModel.EMPLOYEE,null),Employee.class);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_accept_gray);

            Intent openTasks = new Intent(this, MainActivity.class);
            openTasks.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(this,REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("Нет новых задач")
                    .setContentTitle("Новые задачи")
                    .setSmallIcon(R.drawable.icon_accept_green)
                    .setLargeIcon(bitmap)
                    .setContentIntent(pendingIntent);
            startForeground(NOTIFY_ID, notification.build());
        }
        /*Если сервер вернул все новые задачи, но они все уже были, мы ничего не сделаем
        А вот если он вернул 0 новых задач, значит они все были проверены и можно
        оповестить пользователя о том, что новыз задач более нет, только в этом случае
        А вот если сервер прислал еще какую то новую задачу, мы должны оповестить что пришла новая задача
        noTasks отвечает за то, что оповещение что задач нет уже было показано, что бы не дублировать
        taskList пришедшие задачи с сервера
        * */
        Observer<List<Task>> taskDownload = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> taskList) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_accept_gray);
                    Intent openTasks = new Intent(getService(), MainActivity.class);
                    openTasks.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent
                            .getActivity(getService(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification.Builder notification = new Notification.Builder(getService(), CHANNEL_ID)
                            .setContentTitle("Новые задачи")
                            .setSmallIcon(R.drawable.icon_accept_green)
                            .setLargeIcon(bitmap)
                            .setContentIntent(pendingIntent);

                    if(findNewTasks(taskList)) {
                        notification.setContentText(String.format("У вас новые задачи: %d", taskList.size()));
                        //Запоминаем для дальнейшего сравнения
                        prevTasks = taskList;
                        noTasks = false;
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFY_ID, notification.build());
                    } else if (taskList != null && taskList.size() == 0 && !noTasks){//Если новых задач нет, и мы это еще не показывали то...
                        notification.setContentText("У вас нет новых задач");
                        noTasks = true;
                        //что бы при повторном вызове уведомление не сработало, если не появились новые задачи
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFY_ID, notification.build());
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
        //uncheckedTasks.clear();
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
                            return;
                        }
                        tasksMutable.postValue(new LinkedList<>());
                    }
                });
    }

    private boolean findNewTasks(List<Task> tasks){
        //если пришедших задач нету, то и разговора нет, если прошлых нет, но новые есть то сразу да
        if(tasks == null || tasks.size() == 0) return false;
        if(prevTasks == null || prevTasks.size() == 0) return true;
        for (Task task : tasks) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //если нет id, то это новая задача
                if(!prevTasks.stream().anyMatch(t->t.getId().equals(task.getId())))
                    return true;
            }
        }
        return false;
    }



    /*
    //Для загрузки аудио канала + отображения сообщения если есть новые задачи
    public void initNotificationManager(Notification.Builder notification){
        Uri notySound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getPackageName() + "/raw/notification_sound.mp3");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            channel = new NotificationChannel("SOUND_SERVICE_ID","ring",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(Color.GRAY);
            channel.enableLights(true);
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
            channel.setSound(notySound,audioAttributes);
            notificationManager.createNotificationChannel(channel);

            notification.setSound(notySound,audioAttributes);
            notification.setVibrate(new long[]{0,500,1000});
            notificationManager.notify(NOTIFY_ID,notification.build());
        }
        //notificationManager.notify(NOTIFY_ID,notification.build());
    }
     */
    public NotificationService getService(){
        return this;
    }
}
