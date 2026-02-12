package com.example.clickanddrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import java.util.List;

public class BlockUsersAdapter extends RecyclerView.Adapter<BlockUsersAdapter.UserViewHolder> {

    private List<UserProfileResponse> users;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserClick(UserProfileResponse user);
    }

    public BlockUsersAdapter(List<UserProfileResponse> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlockUsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockUsersAdapter.UserViewHolder holder, int position) {
        UserProfileResponse user = users.get(position);

        // Fill name and lastname
        String fullName = user.getName() + " " + user.getSurname();
        holder.userName.setText(fullName);

        // Load image
        //...

        holder.itemView.setOnClickListener(v -> {
            listener.onUserClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateList(List<UserProfileResponse> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
        }
    }
}
