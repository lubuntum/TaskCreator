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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pavel.databaseapp.MainActivity;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Employee;
import com.pavel.databaseapp.databinding.FragmentAuthorizationBinding;

import pl.droidsonroids.gif.GifDrawable;

public class AuthorizationFragment extends Fragment {
    FragmentAuthorizationBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    AuthViewModel authViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthorizationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //if (auth.getCurrentUser() != null) {
        //    startActivity(new Intent(getContext(), MainActivity.class));
        //}
        startSessionInit();
        UIInit();
    }
    public void startSessionInit(){
        Observer<Boolean> startSessionObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean session) {
                if (session) {
                    Toast.makeText(getContext(), "Добро пожаловать", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            }
        };
        authViewModel.getStartSession().observe(getViewLifecycleOwner(),startSessionObserver);

    }
    public void UIInit(){
        binding.loginAnim.setVisibility(View.INVISIBLE);
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.email.getText().toString() == null ||
                        binding.email.getText().toString().matches(" *")) {
                    binding.email.setError("Введите логин");
                    return;
                }
                if (binding.password.getText().toString() == null ||
                        binding.password.getText().toString().matches(" *")){
                    binding.password.setError("Введите пароль");
                    return;
                }
                binding.loginAnim.setVisibility(View.VISIBLE);
                //взять с полей инфу и зарегистрировать пользователя
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            authViewModel.uploadEmployeeByLogin(auth.getCurrentUser().getEmail());//Логин воткнуть
                        }
                        else Toast.makeText(getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();

                        binding.loginAnim.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        binding.registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.auth_fragment,new RegistrationFragment(),"registration")
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack("back_login_fragment")
                        .commit();
            }
        });
    }
}
