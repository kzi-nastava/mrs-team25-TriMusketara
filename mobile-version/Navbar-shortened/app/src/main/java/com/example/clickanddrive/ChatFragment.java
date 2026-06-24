package com.example.clickanddrive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickanddrive.adapters.ChatMessageAdapter;
import com.example.clickanddrive.adapters.ChatUserAdapter;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.ChatMessageDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private static final String ADMIN_EMAIL = "admin@demo.com";
    private static final long MESSAGE_REFRESH_MS = 3000;
    private static final long USER_REFRESH_MS = 5000;

    private TextView tvChatTitle, tvEmpty;
    private RecyclerView rvChatUsers, rvMessages;
    private LinearLayout composeBar;
    private EditText etMessage;
    private Button btnSend, btnBackToUsers;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<ChatMessageDTO> messages = new ArrayList<>();
    private final List<String> chatUsers = new ArrayList<>();

    private ChatMessageAdapter messageAdapter;
    private ChatUserAdapter userAdapter;

    private String currentUserEmail;
    private String selectedUserEmail;

    private final Runnable messagePoll = new Runnable() {
        @Override
        public void run() {
            loadHistory(false);
            handler.postDelayed(this, MESSAGE_REFRESH_MS);
        }
    };

    private final Runnable userPoll = new Runnable() {
        @Override
        public void run() {
            loadChatUsers(false);
            handler.postDelayed(this, USER_REFRESH_MS);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvChatTitle = view.findViewById(R.id.tvChatTitle);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvChatUsers = view.findViewById(R.id.rvChatUsers);
        rvMessages = view.findViewById(R.id.rvMessages);
        composeBar = view.findViewById(R.id.composeBar);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBackToUsers = view.findViewById(R.id.btnBackToUsers);

        currentUserEmail = SessionManager.userEmail;

        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please log in again before using chat", Toast.LENGTH_SHORT).show();
            return;
        }

        messageAdapter = new ChatMessageAdapter(messages, currentUserEmail);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(messageAdapter);

        userAdapter = new ChatUserAdapter(chatUsers, this::openConversation);
        rvChatUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatUsers.setAdapter(userAdapter);

        btnSend.setOnClickListener(v -> sendMessage());
        btnBackToUsers.setOnClickListener(v -> showAdminUserList());

        if (SessionManager.currentUserType == SessionManager.ADMIN) {
            showAdminUserList();
        } else {
            openConversation(ADMIN_EMAIL);
        }
    }

    private void showAdminUserList() {
        stopPolling();

        selectedUserEmail = null;
        tvChatTitle.setText("Support");
        btnBackToUsers.setVisibility(View.GONE);
        rvChatUsers.setVisibility(View.VISIBLE);
        rvMessages.setVisibility(View.GONE);
        composeBar.setVisibility(View.GONE);

        tvEmpty.setText("No active chats yet.");
        tvEmpty.setVisibility(chatUsers.isEmpty() ? View.VISIBLE : View.GONE);

        loadChatUsers(true);
        handler.postDelayed(userPoll, USER_REFRESH_MS);
    }

    private void openConversation(String email) {
        stopPolling();

        selectedUserEmail = email;
        tvChatTitle.setText(email);

        btnBackToUsers.setVisibility(
                SessionManager.currentUserType == SessionManager.ADMIN ? View.VISIBLE : View.GONE
        );

        rvChatUsers.setVisibility(View.GONE);
        rvMessages.setVisibility(View.VISIBLE);
        composeBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        loadHistory(true);
        handler.postDelayed(messagePoll, MESSAGE_REFRESH_MS);
    }

    private void loadChatUsers(boolean showErrors) {
        ClientUtils.chatService.getChatUsers(currentUserEmail).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    chatUsers.clear();
                    chatUsers.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(chatUsers.isEmpty() ? View.VISIBLE : View.GONE);
                } else if (showErrors) {
                    Toast.makeText(getContext(), "Failed to load chats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                if (isAdded() && showErrors) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadHistory(boolean showErrors) {
        if (selectedUserEmail == null) return;

        ClientUtils.chatService.getHistory(selectedUserEmail, currentUserEmail)
                .enqueue(new Callback<List<ChatMessageDTO>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessageDTO>> call,
                                           Response<List<ChatMessageDTO>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            messages.clear();
                            messages.addAll(response.body());
                            messageAdapter.notifyDataSetChanged();

                            if (!messages.isEmpty()) {
                                rvMessages.scrollToPosition(messages.size() - 1);
                            }

                            markAsSeen();
                        } else if (showErrors) {
                            Toast.makeText(getContext(), "Failed to load messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                        if (isAdded() && showErrors) {
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();

        if (text.isEmpty() || selectedUserEmail == null) {
            return;
        }

        ChatMessageDTO dto = new ChatMessageDTO(
                text,
                currentUserEmail,
                selectedUserEmail,
                null,
                false
        );

        btnSend.setEnabled(false);

        ClientUtils.chatService.sendMessage(dto).enqueue(new Callback<ChatMessageDTO>() {
            @Override
            public void onResponse(Call<ChatMessageDTO> call, Response<ChatMessageDTO> response) {
                if (!isAdded()) return;

                btnSend.setEnabled(true);

                if (response.isSuccessful()) {
                    etMessage.setText("");
                    loadHistory(false);
                } else {
                    Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatMessageDTO> call, Throwable t) {
                if (!isAdded()) return;

                btnSend.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsSeen() {
        if (selectedUserEmail == null) return;

        ClientUtils.chatService.markAsSeen(currentUserEmail, selectedUserEmail)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {}

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
    }

    private void stopPolling() {
        handler.removeCallbacks(messagePoll);
        handler.removeCallbacks(userPoll);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
    }
}