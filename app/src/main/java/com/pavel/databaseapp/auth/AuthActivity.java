package com.pavel.databaseapp.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.pavel.databaseapp.MainActivity;
import com.pavel.databaseapp.R;

public class AuthActivity extends AppCompatActivity {
    public final FirebaseAuth auth = FirebaseAuth.getInstance();
    private AuthViewModel authViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.mutableInit();
        if (true)//auth.getCurrentUser() == null
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.auth_fragment,
                            new AuthorizationFragment(),
                            "authorization")
                    .addToBackStack("back_my_tasks")
                    .commit();
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}