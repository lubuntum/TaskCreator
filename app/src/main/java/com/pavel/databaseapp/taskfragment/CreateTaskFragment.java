package com.pavel.databaseapp.taskfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pavel.databaseapp.databinding.FragmentCreateTaskBinding;
import com.pavel.databaseapp.dialog.EmployeeSearchDialog;

public class CreateTaskFragment extends Fragment {
    FragmentCreateTaskBinding binding;
    CreateTaskViewModel taskViewModel;

    public static CreateTaskFragment newInstance() {
        CreateTaskFragment fragment = new CreateTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(this).get(CreateTaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.searchEmployee.setOnClickListener((v) ->{
            EmployeeSearchDialog searchDialog = new EmployeeSearchDialog();
            searchDialog.show(getParentFragmentManager(),"search_fragment");
        });
    }
}