package com.pavel.databaseapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.ActivityHomeBinding;
import com.pavel.databaseapp.services.BitmapStoreService;
import com.pavel.databaseapp.services.ImagePickerService;
import com.pavel.databaseapp.services.notification.NotificationService;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private SharedPreferences preferences;
    private Employee employee;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_DatabaseAppDark);
        else setTheme(R.style.Theme_DatabaseApp);
        super.onCreate(savedInstanceState);
        if(!isNotificationServiceRunning()) {
            startNotificationService();
        }

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_create_task, R.id.nav_settings, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        uploadLocalAccountInfo();
        navBarInit();
        headerNavBarInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void navBarInit(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView employeeName = headerView.findViewById(R.id.employee_name);
        TextView employeePosition = headerView.findViewById(R.id.position);
        employeeName.setText(employee.name);
        employeePosition.setText(employee.position);
    }
    public void headerNavBarInit(){
        View header = binding.navView.getHeaderView(0);
        ImageView accountImage = header.findViewById(R.id.account_image);
        BitmapStoreService bitmapStoreService = new BitmapStoreService(getApplicationContext());
        Drawable profileIcon = ResourcesCompat.getDrawable(getResources(),R.drawable.icon_add_photo,null);
        /*
        if(preferences.contains(SettingsViewModel.PROFILE_ICON))
            accountImage.setImageBitmap(
                    bitmapStoreService.getBitMapByKey(SettingsViewModel.PROFILE_ICON));
        else accountImage.setImageDrawable(profileIcon);
         */
        if(employee.profileIcon != null)
            accountImage.setImageBitmap(employee.profileIcon);

        ImagePickerService imagePickerService = new ImagePickerService(getApplicationContext(),this);
        imagePickerService.getBitmapLiveData().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                accountImage.setImageBitmap(bitmap);
                Toast.makeText(MainActivity.this, "Фото успешно изменено", Toast.LENGTH_SHORT).show();
                //Локальное кэширование
                bitmapStoreService.saveBitmap(SettingsViewModel.PROFILE_ICON,bitmap);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put(Employee.PROFILE_ICON,BitmapStoreService.encodeBitmap(bitmap));
                firestore.collection(SettingsViewModel.EMPLOYEE_COLLECTION)
                        .document(employee.getId()).update(updateMap);
            }
        });

        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imagePickerIntent = new Intent();
                imagePickerIntent.setType("image/*");
                imagePickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerService.launchImage(imagePickerIntent);
            }
        });
    }
    //Также делать для API > 26
    public void startNotificationService(){
        Intent intent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this,intent);
        }
    }
    //Проверка, если сервис оповещений уже запущен
    public boolean isNotificationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE))
            if(NotificationService.class.getName().equals(serviceInfo.service.getClassName()))
                return true;
        return false;
    }
    public void uploadLocalAccountInfo(){
        preferences = getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,MODE_PRIVATE);
        employee = new Gson().
                fromJson(preferences.getString(SettingsViewModel.EMPLOYEE,null), Employee.class);
    }
}