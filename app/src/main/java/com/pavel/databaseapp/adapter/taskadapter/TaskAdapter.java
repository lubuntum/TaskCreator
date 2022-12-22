package com.pavel.databaseapp.adapter.taskadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final List<Task> taskList;
    private final LayoutInflater inflater;
    private final int resource;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.resource = R.layout.task_item;
    }

    public TaskAdapter(Context context, List<Task> taskList, int resource){
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(resource,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.dateRange.setText(task.getEndDate());
        holder.creatorName.setText(task.getCreator());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView taskName;
        public TextView dateRange;
        public TextView creatorName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskName = itemView.findViewById(R.id.task_name);
            this.dateRange = itemView.findViewById(R.id.date_range);
            this.creatorName = itemView.findViewById(R.id.creator_name);
        }
    }
}
