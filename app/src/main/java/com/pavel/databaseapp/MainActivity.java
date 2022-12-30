package com.pavel.databaseapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.ActivityHomeBinding;
import com.pavel.databaseapp.services.notification.NotificationService;
import com.pavel.databaseapp.settings.SettingsViewModel;

public class MainActivity extends AppCompatActivity {
    public static int tasksCount = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private SharedPreferences preferences;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isNotificationServiceRunning()) {
            tasksCount = 0;
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
        navBarInit();
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
        preferences = getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,MODE_PRIVATE);
        Employee employee = new Gson().
                fromJson(preferences.getString(SettingsViewModel.EMPLOYEE,null), Employee.class);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView employeeName = headerView.findViewById(R.id.employee_name);
        TextView employeePosition = headerView.findViewById(R.id.position);
        employeeName.setText(employee.name);
        employeePosition.setText(employee.position);
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
}