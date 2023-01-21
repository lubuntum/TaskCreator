package com.pavel.databaseapp.adapter.taskadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.Task;
import com.pavel.databaseapp.settings.SettingsViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    FirebaseFirestore firestore;
    private List<Task> taskList;
    private List<Task> filterList;
    private Map<String, Integer> statusMap;
    private LayoutInflater inflater;
    private final int resource;
    private Context context;
    public final int [] IMPORTANCE_STATUSES_CODES = {R.drawable.task_item_ripple_mask_success,
            R.drawable.task_item_ripple_mask_normal,R.drawable.task_item_ripple_mask_hard,
            R.drawable.task_item_ripple_mask_extreme};
    private ViewHolder.OnTaskCompleteClickListener taskCompleteListener;
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
        this.firestore = FirebaseFirestore.getInstance();
        this.taskList = taskList;
        this.filterList = taskList;
        this.inflater = LayoutInflater.from(context);
        //Протестировать порядок важен!!!
        List<String> statusesList = Arrays.asList(
                context.getResources().getStringArray(R.array.task_priority));
        this.statusMap = new HashMap<>();
        statusMap.put("success",IMPORTANCE_STATUSES_CODES[0]);
        for(int i = 1; i < statusesList.size();i++)
            statusMap.put(statusesList.get(i),IMPORTANCE_STATUSES_CODES[i]);
    }
    @Override
    public int getItemViewType(int position) {
        Task task = filterList.get(position);
        //Написать потом нормальный код!!!! вынести success
        if (task.isComplete) return statusMap.get("success");
        if (statusMap.get(task.getImportance()) == null) return IMPORTANCE_STATUSES_CODES[1];
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
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setOnTaskCompleteClickListener(taskCompleteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = filterList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.dateRange.setText(task.getEndDate());
        holder.creatorName.setText(task.getCreator());
        holder.expandableTextView.setText(task.getDescription());
        if (task.isComplete){
            holder.dateRange.setTextColor(context.getResources()
                    .getColor(com.beardedhen.androidbootstrap.R.color.bootstrap_brand_success));
            holder.completeTaskBtn.setColorFilter(ContextCompat.getColor(
                    getContext(),
                    com.beardedhen.androidbootstrap.R.color.bootstrap_brand_success));
            holder.completeTaskBtn.setClickable(false);
        }
        //Если задача новая отобразить это и обновить задачу
        //Потом можно попытаться обновить только одно поле а не всю задачу
        if(!task.isChecked){
            task.setChecked(true);
            holder.newTask.setVisibility(View.VISIBLE);
            firestore.collection(SettingsViewModel.TASKS_COLLECTION)
                    .document(task.getId())
                    .set(task);
        }
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
    //Сделать сравнение по датам (сделано)
    public void filterByDate(Date date) {
        filterList = new LinkedList<>();
        DateFormat parseFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
        for (Task task: taskList) {
            try {
                //Date testDate = parseFormat.parse(task.getEndDate());
                //Log.d(task.getEndDate(),String.valueOf(date.compareTo(testDate)));
                if (parseFormat.parse(task.getEndDate()).compareTo(date) == 0)
                    filterList.add(task);
            } catch (ParseException e){
                System.err.println(String.format("Incorrect date format %s",task.getEndDate()));
            }
        }
        notifyDataSetChanged();
    }
    public void completeTask(int position){
        if (position < 0 || position > filterList.size()) return;
        filterList.get(position).setComplete(true);
        notifyItemChanged(position);
    }
    public Task getItemByPosition(int position){
        return filterList.get(position);
    }
    public void addNewTasks(List<Task> uploadedTask){
        for(Task upTask : uploadedTask){
            boolean isUnique = true;
            for(Task curTask : taskList){
                if (upTask.getId().equals(curTask.getId())){
                    isUnique = false;
                    break;
                }
            }
            if(isUnique) taskList.add(upTask);
        }
        notifyDataSetChanged();
    }
    public Context getContext() {
        return context;
    }

    public ViewHolder.OnTaskCompleteClickListener getTaskCompleteListener() {
        return taskCompleteListener;
    }

    public void setTaskCompleteListener(ViewHolder.OnTaskCompleteClickListener taskCompleteListener) {
        this.taskCompleteListener = taskCompleteListener;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public List<Task> getFilterList() {
        return filterList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView taskName;
        public TextView dateRange;
        public TextView creatorName;
        public ExpandableTextView expandableTextView;
        public ImageButton completeTaskBtn;
        public ImageView newTask;
        public OnTaskCompleteClickListener onTaskCompleteClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskName = itemView.findViewById(R.id.task_name);
            this.dateRange = itemView.findViewById(R.id.date_range);
            this.creatorName = itemView.findViewById(R.id.creator_name);
            this.expandableTextView = itemView.findViewById(R.id.expand_text_view);
            this.completeTaskBtn = itemView.findViewById(R.id.complete_task_btn);
            this.newTask = itemView.findViewById(R.id.new_task);
        }

        public void setOnTaskCompleteClickListener(OnTaskCompleteClickListener onTaskCompleteClickListener) {
            this.onTaskCompleteClickListener = onTaskCompleteClickListener;
            completeTaskBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskCompleteClickListener.onClick(getAdapterPosition());
                }
            });
        }

        public interface OnTaskCompleteClickListener {
            void onClick(int position);
        }
    }
}
