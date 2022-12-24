package com.pavel.databaseapp.adapter.taskadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Task;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private Map<String, Integer> statusMap;
    private LayoutInflater inflater;
    private final int resource;
    private Context context;
    public final int [] STATUSES_CODES = {R.drawable.task_item_ripple_mask_success,
            R.drawable.task_item_ripple_mask_normal,R.drawable.task_item_ripple_mask_hard,
            R.drawable.task_item_ripple_mask_extreme};
    //public final int SUCCESS_STATUS_CODE =
    //public final int NORMAL_STATUS_CODE =
    //public final int HARD_STATUS_CODE = ;
    //public final int EXTREME_STATUS_CODE = ;

    public TaskAdapter(Context context, List<Task> taskList) {
        adapterInit(context,taskList);
        this.resource = R.layout.task_item;
    }

    public TaskAdapter(Context context, List<Task> taskList, int resource){
        adapterInit(context,taskList);
        this.resource = resource;
    }

    public void adapterInit(Context context, List<Task> taskList){
        this.context = context;
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        //Протестировать порядок важен!!!
        List<String> statusesList = Arrays.asList(
                context.getResources().getStringArray(R.array.task_priority));
        this.statusMap = new HashMap<>();
        //statusMap.put(statusesList.get(0),SUCCESS_STATUS_CODE);
        for(int i = 1; i < statusesList.size();i++)
            statusMap.put(statusesList.get(i),STATUSES_CODES[i]);
    }

    @Override
    public int getItemViewType(int position) {
        Task task = taskList.get(position);
        if (statusMap.get(task.getImportance()) == null) return STATUSES_CODES[1];
        return statusMap.get(task.getImportance());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(resource,parent,false);
        view.setBackground(ResourcesCompat.getDrawable(
                getContext().getResources(),
                viewType,
                null));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.dateRange.setText(task.getEndDate());
        holder.creatorName.setText(task.getCreator());
        holder.expandableTextView.setText(task.getDescription());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView taskName;
        public TextView dateRange;
        public TextView creatorName;
        public ExpandableTextView expandableTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskName = itemView.findViewById(R.id.task_name);
            this.dateRange = itemView.findViewById(R.id.date_range);
            this.creatorName = itemView.findViewById(R.id.creator_name);
            this.expandableTextView = itemView.findViewById(R.id.expand_text_view);
        }
    }

    public Context getContext() {
        return context;
    }
}
