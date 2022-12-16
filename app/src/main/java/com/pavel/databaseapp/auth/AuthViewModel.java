package com.pavel.databaseapp.auth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.settings.SettingsViewModel;

public class AuthViewModel extends AndroidViewModel {
    private FirebaseFirestore firestore ;
    private FirebaseAuth auth ;
    private Employee employee;
    private SharedPreferences preferences;
    private  SharedPreferences.Editor editor;

    private MutableLiveData<Boolean> filterIsValid;
    private MutableLiveData<Boolean> registration;
    private MutableLiveData<Boolean> sendEmployeeData;
    private MutableLiveData<Boolean> startSession;

    private MutableLiveData<String> status;
    public AuthViewModel(Application app){
        super(app);
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.preferences = app.getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,Context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }
    public void mutableInit(){
        this.filterIsValid = new MutableLiveData<>();
        this.registration = new MutableLiveData<>();
        this.sendEmployeeData = new MutableLiveData<>();
        this.status = new MutableLiveData<>();
        this.startSession = new MutableLiveData<>();
    }
    public void registration(){
        auth.createUserWithEmailAndPassword(employee.getMail(), employee.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    registration.postValue(true);
                }
                else {
                    registration.postValue(false);
                    status.postValue("Ошибка " + task.getException());
                }
            }
        });
    }
    public void sendEmployeeData(){
        if(auth.getCurrentUser() != null)
            firestore.collection("employees").add(employee)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            sendEmployeeData.postValue(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sendEmployeeData.postValue(false);
                            status.postValue("Ошибка " + e);
                        }
                    });
    }
    public void uploadEmployeeByLogin(String login){
        firestore.collection(SettingsViewModel.EMPLOYEE_COLLECTION)
                .whereEqualTo(Employee.MAIL,login).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot doc : task.getResult())
                            employee = Employee.parse(doc);
                        hashCurrentEmployee();
                        //Сохранить employee в preferences, лучше новый метод
                        //task.getResult().iterator().next();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void hashCurrentEmployee(){
        Gson gson = new Gson();
        String jsonEmployee = gson.toJson(employee);
        editor.putString(SettingsViewModel.EMPLOYEE, jsonEmployee);
        editor.apply();
        startSession.postValue(true);
    }

    public void setFirestore(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void setFilterIsValid(MutableLiveData<Boolean> filterIsValid) {
        this.filterIsValid = filterIsValid;
    }

    public void setRegistration(MutableLiveData<Boolean> registration) {
        this.registration = registration;
    }

    public void setSendEmployeeData(MutableLiveData<Boolean> sendEmployeeData) {
        this.sendEmployeeData = sendEmployeeData;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setStatus(MutableLiveData<String> status) {
        this.status = status;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public MutableLiveData<Boolean> getFilterIsValid() {
        return filterIsValid;
    }

    public MutableLiveData<Boolean> getRegistration() {
        return registration;
    }

    public MutableLiveData<Boolean> getSendEmployeeData() {
        return sendEmployeeData;
    }

    public MutableLiveData<Boolean> getStartSession() {
        return startSession;
    }

    public void setStartSession(MutableLiveData<Boolean> startSession) {
        this.startSession = startSession;
    }

    public Employee getEmployee() {
        return employee;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public SharedPreferences.Editor getSharedEditor(){
        return preferences.edit();
    }
}








