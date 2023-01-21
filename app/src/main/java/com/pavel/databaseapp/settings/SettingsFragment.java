package com.pavel.databaseapp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.databinding.FragmentSettingsBinding;
import com.pavel.databaseapp.mytask.MyTaskViewModel;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    SettingsViewModel settingsViewModel;
    MyTaskViewModel myTaskViewModel;
    FragmentSettingsBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        myTaskViewModel = new ViewModelProvider(getActivity()).get(MyTaskViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userName.setText(
                String.format(getResources().getString(R.string.user_name),
                        settingsViewModel.getEmployee().getSecondName(),
                        settingsViewModel.getEmployee().getName())
        );
        binding.position.setText(
                String.format(getResources().getString(R.string.current_position),
                        settingsViewModel.getEmployee().getPosition()));
        if(myTaskViewModel.getMutableTasks() != null &&
                myTaskViewModel.getMutableTasks().getValue() != null)
            binding.activeTasks.setText(String.format(getResources().getString(R.string.current_task_count),
                    myTaskViewModel.getMutableTasks().getValue().size()));

        currentThemeInit();
    }
    public void currentThemeInit(){
        binding.currentTheme.setChecked(!settingsViewModel.getPreferences()
                .getString(SettingsViewModel.CURRENT_THEME, SettingsViewModel.WHITE_THEME)
                .equals(SettingsViewModel.WHITE_THEME));

        binding.currentTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = settingsViewModel.getPreferences().edit();
                if (isChecked){
                    editor.putString(SettingsViewModel.CURRENT_THEME,SettingsViewModel.DARK_THEME);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    editor.putString(SettingsViewModel.CURRENT_THEME, SettingsViewModel.WHITE_THEME);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                editor.apply();
            }
        });
    }
}
