package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.login.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;

    public UserListAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.apiUrlTextView.setText(user.getApiUrl());

        holder.itemView.setOnClickListener(v -> showUserDetails(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void showUserDetails(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("用户详情");
        builder.setMessage("用户名：" + user.getUsername() + "\nAPI URL：" + user.getApiUrl());
        builder.setPositiveButton("确定", null);
        builder.show();
    }

    public void removeUser(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
    
    // 其他方法省略

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView apiUrlTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            apiUrlTextView = itemView.findViewById(R.id.apiUrlTextView);
        }
    }
}