package com.pavel.databaseapp;

import static com.pavel.databaseapp.data.User.USER_COLLECTION;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pavel.databaseapp.data.User;
import com.pavel.databaseapp.databinding.FragmentDataRecieverBinding;
import com.pavel.databaseapp.adapter.useradapter.UserAdapter;

import java.util.LinkedList;
import java.util.List;

public class DataReceiverFragment extends Fragment {
    private MutableLiveData<List<User>> usersLiveData;
    private UserAdapter adapter;
    private FragmentDataRecieverBinding binding;
    private FirebaseFirestore firestore;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usersLiveData = new MutableLiveData<>();
        binding = FragmentDataRecieverBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Observer<List<User>> userUploadObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adapterInit(users);
            }
        };
        usersLiveData.observe(getViewLifecycleOwner(),userUploadObserver);
        dataUpload();
    }
    public void adapterInit(List<User> users) {
        adapter = new UserAdapter(getContext(), users);
        binding.userList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    public void dataUpload(){
        firestore.collection(USER_COLLECTION).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "Cant upload data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<User> users = new LinkedList<>();
                    for(QueryDocumentSnapshot document : task.getResult())
                        //Toast.makeText(getContext(), document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
                        users.add(User.parse(document));
                    usersLiveData.postValue(users);
                });
    }
}