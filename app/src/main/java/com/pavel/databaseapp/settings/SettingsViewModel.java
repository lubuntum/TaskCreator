package com.pavel.databaseapp.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;

import java.io.File;

public class SettingsViewModel extends AndroidViewModel {
    //Network
    public static final String EMPLOYEE_COLLECTION = "employees";
    public static final String TASKS_COLLECTION = "tasks";

    /*APP Local Data*/
    public static final String SETTINGS_STORAGE = "settings";
    public static final String LOGIN_KEY = "login";//deprecate
    public static final String PASSWORD_KEY = "password";//deprecate
    public static final String CURRENT_THEME = "theme";
    public static final String DARK_THEME = "dark";
    public static final String WHITE_THEME = "white";
    public static final String EMPLOYEE = "employee";
    public static final String PROFILE_ICON = "profile_icon.jpg";

    private SharedPreferences preferences;
    private Employee employee;
    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.preferences = application.getSharedPreferences(SETTINGS_STORAGE, Context.MODE_PRIVATE);
        employeeInit();
    }
    public void employeeInit(){
        Gson gson = new Gson();
        this.employee =
                gson.fromJson(preferences.getString(EMPLOYEE,null),Employee.class);
    }

    public Employee getEmployee() {
        return employee;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
