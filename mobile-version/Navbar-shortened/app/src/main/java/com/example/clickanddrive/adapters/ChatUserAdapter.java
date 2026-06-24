package com.example.clickanddrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    public interface OnChatUserClickListener {
        void onChatUserClick(String email);
    }

    private final List<String> users;
    private final OnChatUserClickListener listener;

    public ChatUserAdapter(List<String> users, OnChatUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        String email = users.get(position);
        holder.tvUserEmail.setText(email);
        holder.itemView.setOnClickListener(v -> listener.onChatUserClick(email));
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public static class ChatUserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserEmail;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
        }
    }
}