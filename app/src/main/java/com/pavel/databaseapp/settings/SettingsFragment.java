package com.pavel.databaseapp.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.databinding.FragmentSettingsBinding;
import com.pavel.databaseapp.mytask.MyTaskViewModel;

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
    }
}
