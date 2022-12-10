package com.pavel.databaseapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pavel.databaseapp.MainActivity;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.FragmentRegistraionBinding;

public class RegistrationFragment extends Fragment {
    FragmentRegistraionBinding binding;
    AuthViewModel authViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        authViewModel.mutableInit();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistraionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registrationInit();
        sendEmployeeDataInit();
        statusInit();
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    authViewModel.registration();
                }
            }
        });
    }

    public void registrationInit(){
        Observer<Boolean> registrationObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isReg) {
                if(isReg)
                    authViewModel.sendEmployeeData();

            }
        };
        authViewModel.getRegistration().observe(getViewLifecycleOwner(),registrationObserver);
    }
    public void sendEmployeeDataInit(){
        Observer<Boolean> sendEmployeeObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDataSend) {
                if (isDataSend){
                    getParentFragmentManager().popBackStack();
                }
            }
        };
        authViewModel.getSendEmployeeData().observe(getViewLifecycleOwner(),sendEmployeeObserver);
    }
    public void statusInit(){
        Observer<String> statusObserver = new Observer<String>() {
            @Override
            public void onChanged(String status) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
            }
        };
        authViewModel.getStatus().observe(getViewLifecycleOwner(),statusObserver);
    }

    public boolean validation(){
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String [] personalData = binding.userName.getText().toString().split(" ");
        String phone = binding.phone.getText().toString();
        String position = binding.positionPicker.getSelectedItem().toString();
        if(email.length() < 6 || !email.matches("([A-Za-z\\d._-]+)@[A-Za-z._-]+\\.[a-zA-Z]{2,5}")) {
            binding.email.setError("Почта некорректна");
            return false;
        }
        if(password.length() < 6 || password.matches("(?=[A-Z]+)(?=[!@#$%^&*()_=\\-+]{2})")) {
            binding.password.setError("Пароль должен быть более 6 символов, " +
                    "содерать заглавные символы и специальные символы");
            return false;
        }
        if(position.equals(binding.positionPicker.getItemAtPosition(0))) {
            Toast.makeText(getContext(), "Выберите должность", Toast.LENGTH_SHORT).show();
            return false;
        }
        Employee employee = new Employee(
                personalData[0]
                ,personalData[1],
                "01.01.1999", position, email, phone);
        employee.setPassword(password);
        authViewModel.setEmployee(employee);
        return true;
    }
}
