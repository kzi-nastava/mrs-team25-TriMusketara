package com.example.clickanddrive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.requests.NoteRequest;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockAUserBottomSheet extends BottomSheetDialogFragment {

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnBlock;
    private Button btnNote;
    private ShapeableImageView profileImage;

    private UserProfileResponse user;

    public static BlockAUserBottomSheet newInstance(UserProfileResponse user) {
        BlockAUserBottomSheet sheet = new BlockAUserBottomSheet();
        Bundle args = new Bundle();
        args.putLong("userId", user.getId());
        args.putString("name", user.getName());
        args.putString("surname", user.getSurname());
        args.putString("address", user.getAddress());
        args.putString("email", user.getEmail());
        args.putString("phone", user.getPhone());
        args.putBoolean("blocked", user.isBlocked());
        args.putString("blockReason", user.getBlockReason());
        args.putString("profileImageUrl", user.getProfileImageUrl());
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_block_user, container, false);

        // Reconstruct user from bundle
        if (getArguments() != null) {
            user = new UserProfileResponse();
            user.setId(getArguments().getLong("userId"));
            user.setName(getArguments().getString("name"));
            user.setSurname(getArguments().getString("surname"));
            user.setAddress(getArguments().getString("address"));
            user.setEmail(getArguments().getString("email"));
            user.setPhone(getArguments().getString("phone"));
            user.setBlocked(getArguments().getBoolean("blocked"));
            user.setBlockReason(getArguments().getString("blockReason"));
            user.setProfileImageUrl(getArguments().getString("profileImageUrl"));
        }

        initializeViews(view);
        setUpListeners();

        return view;
    }

    private void initializeViews(View view) {
        tvName = view.findViewById(R.id.sheet_fullname);
        tvAddress = view.findViewById(R.id.sheet_user_address);
        tvEmail = view.findViewById(R.id.sheet_user_email);
        tvPhone = view.findViewById(R.id.sheet_user_phone);
        btnBlock = view.findViewById(R.id.btn_sheet_block);
        btnNote = view.findViewById(R.id.btn_sheet_note);
        profileImage = view.findViewById(R.id.sheet_user_avatar);

        if (user != null) {
            String fullName = user.getName() + " " + user.getSurname();
            tvName.setText(fullName);
            tvAddress.setText(user.getAddress());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone());

            loadProfileImage();

            updateBlockButtonText();
        }
    }

    private void loadProfileImage() {
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            String filename = user.getProfileImageUrl().substring(
                    user.getProfileImageUrl().lastIndexOf("/") + 1
            );

            Call<ResponseBody> call = ClientUtils.userService.getProfileImage(filename);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            InputStream inputStream = response.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profileImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            profileImage.setImageResource(R.drawable.no_profile_picture);
                        }
                    } else {
                        profileImage.setImageResource(R.drawable.no_profile_picture);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    profileImage.setImageResource(R.drawable.no_profile_picture);
                }
            });
        } else {
            profileImage.setImageResource(R.drawable.no_profile_picture);
        }
    }

    // When the user is not blocked say 'Block'
    // If the user is already blocked say 'Unblock'
    private void updateBlockButtonText() {
        if (user.isBlocked()) {
            btnBlock.setText("Unblock");
        } else {
            btnBlock.setText("Block");
        }
    }

    private void setUpListeners() {
        btnBlock.setOnClickListener(v -> {
            if (user.isBlocked()) {
                unblockUser(); // already blcked, unblock him
            } else {
                blockUser(); // regular block
            }
        });

        btnNote.setOnClickListener(v -> {
            showNoteDialog(); // popup to leave a reason on why the user was blocked
        });
    }

    private void blockUser() {
        showConfirmationDialog(
                "Block User",
                "Are you sure you want to block " + user.getName() + " " + user.getSurname() + "?",
                () -> {
                    NoteRequest emptyRequest = new NoteRequest("");
                    Call<UserProfileResponse> call = ClientUtils.adminService.blockUser(user.getId(), emptyRequest);
                    call.enqueue(new Callback<UserProfileResponse>() {
                        @Override
                        public void onResponse(Call<UserProfileResponse> call,
                                               Response<UserProfileResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                user = response.body();
                                updateBlockButtonText();
                                Toast.makeText(getContext(), "User blocked successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to block user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        );
    }

    private void unblockUser() {
        showConfirmationDialog(
                "Unblock User",
                "Are you sure you want to unblock " + user.getName() + " " + user.getSurname() + "?",
                () -> {
                    Call<UserProfileResponse> call = ClientUtils.adminService.unblockUser(user.getId());
                    call.enqueue(new Callback<UserProfileResponse>() {
                        @Override
                        public void onResponse(Call<UserProfileResponse> call,
                                               Response<UserProfileResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                user = response.body();
                                updateBlockButtonText();
                                Toast.makeText(getContext(), "User unblocked successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to unblock user",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        );
    }

    public void showNoteDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_note_input);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextInputEditText etNoteInput = dialog.findViewById(R.id.et_note_input);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnSave = dialog.findViewById(R.id.btn_save);

        dialogTitle.setText("Add Note for " + user.getName());

        // If already has note
        if (user.getBlockReason() != null && !user.getBlockReason().isEmpty()) {
            etNoteInput.setText(user.getBlockReason());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String noteText = etNoteInput.getText().toString().trim();
            if (!noteText.isEmpty()) {
                NoteRequest noteRequest = new NoteRequest(noteText);

                Call<UserProfileResponse> call = ClientUtils.adminService.leaveNote(user.getId(), noteRequest);
                call.enqueue(new Callback<UserProfileResponse>() {
                    @Override
                    public void onResponse(Call<UserProfileResponse> call,
                                           Response<UserProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            user = response.body();
                            Toast.makeText(getContext(), "Note saved successfully",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Failed to save note",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Note cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button btnNegative = dialog.findViewById(R.id.btn_negative);
        Button btnPositive = dialog.findViewById(R.id.btn_positive);

        dialogTitle.setText(title);
        dialogMessage.setText(message);

        btnNegative.setOnClickListener(v -> dialog.dismiss());

        btnPositive.setOnClickListener(v -> {
            onConfirm.run();
            dialog.dismiss();
        });

        dialog.show();
    }
}
