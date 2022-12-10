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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pavel.databaseapp.MainActivity;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.databinding.FragmentAuthorizationBinding;

public class AuthorizationFragment extends Fragment {
    FragmentAuthorizationBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //взять с полей инфу и зарегистрировать пользователя
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                if (password.length() < 6) {
                    binding.password.setError("Длинна пароля должна быть не менее 6 символов");
                    return;
                }
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),"Добро пожаловать",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(),MainActivity.class));
                        }
                        else Toast.makeText(getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
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
