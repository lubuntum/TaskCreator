package com.pavel.databaseapp.auth;

import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.pavel.databaseapp.data.Employee;

public class AuthViewModel extends ViewModel {
    private FirebaseFirestore firestore ;
    private FirebaseAuth auth ;
    private Employee employee;

    private MutableLiveData<Boolean> filterIsValid;
    private MutableLiveData<Boolean> registration;
    private MutableLiveData<Boolean> sendEmployeeData;
    private MutableLiveData<String> status;
    public AuthViewModel(){
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }
    public void mutableInit(){
        this.filterIsValid = new MutableLiveData<>();
        this.registration = new MutableLiveData<>();
        this.sendEmployeeData = new MutableLiveData<>();
        this.status = new MutableLiveData<>();
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

    public Employee getEmployee() {
        return employee;
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }
}
