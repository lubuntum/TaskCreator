package com.pavel.databaseapp.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pavel.databaseapp.databinding.FragmentLogoutBinding;
import com.pavel.databaseapp.settings.SettingsViewModel;

public class LogoutFragment extends Fragment {
    FragmentLogoutBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater,container,true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout();
    }
    public void logout(){
        SharedPreferences pref =
                getContext().getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(SettingsViewModel.EMPLOYEE);
        editor.apply();
        Intent intent = new Intent(getContext(), AuthActivity.class);
        startActivity(intent);
    }
}
