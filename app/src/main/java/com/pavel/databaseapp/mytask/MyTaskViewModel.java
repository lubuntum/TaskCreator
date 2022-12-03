package com.pavel.databaseapp.mytask;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyTaskViewModel {
    FirebaseFirestore firestore;
    public MyTaskViewModel(){
        this.firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}
