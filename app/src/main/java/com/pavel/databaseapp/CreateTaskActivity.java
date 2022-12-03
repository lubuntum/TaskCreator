package com.pavel.databaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pavel.databaseapp.createtask.CreateTaskFragment;

public class CreateTaskActivity extends AppCompatActivity {
    public  FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, CreateTaskFragment.newInstance(),"data_sender_fragment")
                .commit();

    }

}