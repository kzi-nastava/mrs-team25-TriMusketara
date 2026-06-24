package com.example.clickanddrive.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.R;
import com.example.clickanddrive.dtosample.ChatMessageDTO;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private final List<ChatMessageDTO> messages;
    private final String currentUserEmail;

    public ChatMessageAdapter(List<ChatMessageDTO> messages, String currentUserEmail) {
        this.messages = messages;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessageDTO message = messages.get(position);
        boolean mine = currentUserEmail != null && currentUserEmail.equals(message.getSenderEmail());

        holder.tvMessage.setText(message.getMessage());
        holder.tvTime.setText(formatTime(message.getSentAt()));

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) holder.bubbleContainer.getLayoutParams();
        params.gravity = mine ? Gravity.END : Gravity.START;
        holder.bubbleContainer.setLayoutParams(params);

        holder.bubbleContainer.setBackgroundResource(
                mine ? R.drawable.chat_bubble_mine : R.drawable.chat_bubble_other
        );
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    private String formatTime(String sentAt) {
        if (sentAt == null || sentAt.length() < 16) return "";
        return sentAt.substring(11, 16);
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout bubbleContainer;
        TextView tvMessage;
        TextView tvTime;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            bubbleContainer = itemView.findViewById(R.id.bubbleContainer);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}