package com.pavel.databaseapp.useradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pavel.databaseapp.R;
import com.pavel.databaseapp.data.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<User> users;

    public UserAdapter(Context context, List<User> users){
        this.inflater = LayoutInflater.from(context);
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.user_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.login.setText(user.login);
        holder.userName.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView userName;
        final TextView login;
        public ViewHolder(View view){
            super(view);
            this.userName = view.findViewById(R.id.user_name);
            this.login =view.findViewById(R.id.user_login);
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
