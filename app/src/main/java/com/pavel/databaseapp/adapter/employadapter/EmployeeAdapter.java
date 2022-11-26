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
    private ViewHolder.OnEmployeeClickListener itemListener;

    public EmployeeAdapter(Context context, List<Employee> employees, ViewHolder.OnEmployeeClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.employees = employees;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.employee_item,parent,false);
        return new ViewHolder(view,itemListener);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView primaryInfo;
        final TextView phone;
        final TextView mail;
        OnEmployeeClickListener listener;

        public ViewHolder(@NonNull View view, OnEmployeeClickListener listener) {
            super(view);
            this.primaryInfo = view.findViewById(R.id.primaryInfo);
            this.phone = view.findViewById(R.id.phone);
            this.mail = view.findViewById(R.id.mail);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }

        public interface OnEmployeeClickListener{
            void onClick(int position);
        }
    }
}
