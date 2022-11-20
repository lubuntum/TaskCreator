package com.pavel.databaseapp.adapter.employadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Employee> employees;

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.inflater = LayoutInflater.from(context);
        this.employees = employees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.employee_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.primaryInfo.setText(String.format("%s %s должность: %s",employee.name,employee.secondName,employee.position));
        holder.mail.setText(employee.mail);
        holder.phone.setText(employee.phone);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView primaryInfo;
        final TextView phone;
        final TextView mail;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.primaryInfo = view.findViewById(R.id.primaryInfo);
            this.phone = view.findViewById(R.id.phone);;
            this.mail = view.findViewById(R.id.mail);;
        }
    }
}
