package com.example.clickanddrive;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.clients.FileUtils;
import com.example.clickanddrive.dtosample.responses.ProfileImageResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CardView cardGuest, cardMyAccount, cardApplication;
    private LinearLayout containerApplicationButtons;

    private ShapeableImageView imgProfile;
    private ImageView btnUploadImage, btnDeleteImage;
    private String currentImageUrl = null;
    private Uri selectedImageUri = null;

    // Hardcoded user ID
    private static final Long TEMP_USER_ID = 2L;

    // ActivityResultLauncher for permissions
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find by id all of the cards defined above
        cardGuest = view.findViewById(R.id.card_guest);
        cardMyAccount = view.findViewById(R.id.card_my_account);
        cardApplication = view.findViewById(R.id.card_application);
        containerApplicationButtons = view.findViewById(R.id.container_application_buttons);

        imgProfile = view.findViewById(R.id.img_profile);
        btnUploadImage = view.findViewById(R.id.btn_upload_image);
        btnDeleteImage = view.findViewById(R.id.btn_delete_image);

        // All cards have no visibility
        cardGuest.setVisibility(View.GONE);
        cardMyAccount.setVisibility(View.GONE);
        cardApplication.setVisibility(View.GONE);

        // Initialize launchers
        setUpImagePickerLauncher();
        setUpPermissionLauncher();

        // Listeners...
        btnUploadImage.setOnClickListener(v -> checkPermissionAndPickImage());
        btnDeleteImage.setOnClickListener(v -> deleteImage());

        // Button actions
        // ...
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> openLoginFragment());
        btnRegister.setOnClickListener(v -> openRegisterFragment());

        // Getting button for changing information by its id
        Button btnChangeInfo = view.findViewById(R.id.btn_change_info);
        btnChangeInfo.setOnClickListener(v -> openProfileChangeInfoFragment());  


        switch (SessionManager.currentUserType) {
            case SessionManager.GUEST:
                cardGuest.setVisibility(View.VISIBLE);
                break;
            case SessionManager.USER:
            case SessionManager.DRIVER:
            case SessionManager.ADMIN:
                cardMyAccount.setVisibility(View.VISIBLE);
                cardApplication.setVisibility(View.VISIBLE);

                displayUserProfileImage();

                // Add different buttons based on the role
                addApplicationButtons(SessionManager.currentUserType);
                break;
        }

        return view;
    }

    private void setUpPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setUpImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadImage(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Upload profile image
    private void uploadImage(Uri imageUri) {
        try {
            // Convert Uri to File
            File file = FileUtils.getFileFromUri(requireContext(), imageUri);

            if (file == null || !file.exists()) {
                Toast.makeText(getContext(), "Failed to read image", Toast.LENGTH_SHORT).show();
                return;
            }

//            RequestBody requestFile = RequestBody.create(
//                    MediaType.parse("image/*"),
//                    file
//            );
            MediaType mediaType = MediaType.get("image/*");
            RequestBody requestFile = RequestBody.create(file, mediaType);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


            Call<ProfileImageResponse> call = ClientUtils.userService.uploadProfileImage(TEMP_USER_ID, body);

            call.enqueue(new Callback<ProfileImageResponse>() {
                @Override
                public void onResponse(Call<ProfileImageResponse> call, retrofit2.Response<ProfileImageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentImageUrl = response.body().getProfileImageUrl();
                        loadImageFromUrl(currentImageUrl);
                        btnDeleteImage.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Load users image
    private void loadImageFromUrl(String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        Call<ResponseBody> call = ClientUtils.userService.getProfileImage(filename);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Convert response to Bitmap
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        imgProfile.setImageBitmap(bitmap);
                        btnDeleteImage.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        imgProfile.setImageResource(R.drawable.no_profile_picture);
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    imgProfile.setImageResource(R.drawable.no_profile_picture);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                imgProfile.setImageResource(R.drawable.no_profile_picture);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // When in profile, display both name and last name and profile photo
    private void displayUserProfileImage() {
        Call<UserProfileResponse> call = ClientUtils.userService.getUserProfile(TEMP_USER_ID);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    // Set full name
                    assert getView() != null;
                    TextView fullName = getView().findViewById(R.id.fullname);
                    fullName.setText(String.format("%s %s", profile.getName(), profile.getSurname()));

                    // Set profile image
                    currentImageUrl = profile.getProfileImageUrl();
                    if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                        loadImageFromUrl(currentImageUrl);
                    } else {
                        imgProfile.setImageResource(R.drawable.no_profile_picture);
                        btnDeleteImage.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Delete profile image
    private void deleteImage() {
        Call<Void> call = ClientUtils.userService.deleteProfileImage(TEMP_USER_ID);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    currentImageUrl = null;
                    imgProfile.setImageResource(R.drawable.no_profile_picture);
                    btnDeleteImage.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, loginFragment) 
                .addToBackStack(null) 
                .commit();
    }

    private void openRegisterFragment() {
        RegisterFragment registerFragment = new RegisterFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, registerFragment)
                .addToBackStack(null)
                .commit();
    }

    // Opening a new fragment with user info, along side ability to change info
    private void openProfileChangeInfoFragment() {
        ChangeInfoFragment fragment = new ChangeInfoFragment();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).addToBackStack(null).commit();
    }

    private void addApplicationButtons(int userType) {
        containerApplicationButtons.removeAllViews();

        switch(userType) {
            case SessionManager.USER:
                addButton("Favorite routes", R.drawable.heart ,() -> openFavoriteRoutesFragment());
                addButton("Ride history", R.drawable.history  ,() -> {});
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes, () -> {});
                addButton("Support", R.drawable.support, () -> {});
                addButton("Log out", R.drawable.logout, () -> logout());
                break;

            case SessionManager.DRIVER:
                addButton("Ride history", R.drawable.history,  () -> openDriverHistoryFragment());
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes, () -> {});
                addButton("Support", R.drawable.support, () -> {});
                addButton("Log out", R.drawable.logout, () -> logout());
                break;

            case SessionManager.ADMIN:
                addButton("Register new driver", 0, () -> openDriverRegistrationFragment());
                addButton("Check current rides", 0, () -> {});
                addButton("Change prices", R.drawable.price, () -> {});
                addButton("Ride history", R.drawable.history, () -> {});
                addButton("Requests", 0,  () -> {});
                addButton("Reports", R.drawable.report,  () -> {});
                addButton("Notes", R.drawable.notes,  () -> {});
                addButton("Log out", R.drawable.logout, () -> logout());
                break;
        }
    }

    private void logout() {
        if (isAdded() && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).logoutAndGoToLogin();
        }
    }

    private View createDivider() {
        View divider = new View(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
        params.setMargins(0,8,0,8);

        divider.setLayoutParams(params);

        divider.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.divider_color)
        );

        return divider;
    }

    /*private void addButton(String text, Runnable action) {
        Button button = new Button(new ContextThemeWrapper(getContext(), R.style.ProfileActionButton), null, 0);

        button.setText(text);

        button.setOnClickListener(v -> action.run());
        containerApplicationButtons.addView(button);

        // Bottom line
        containerApplicationButtons.addView(createDivider());
    }*/

    private void addButton(String text, int iconRes, Runnable action) {
        MaterialButton button = new MaterialButton(new ContextThemeWrapper(getContext(), R.style.ProfileActionButton), null, 0);
        button.setText(text);

        // If button has icon, display it
        if (iconRes != 0) {
            button.setIcon(ContextCompat.getDrawable(getContext(), iconRes));
            button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            button.setIconPadding(16);
        }

        button.setOnClickListener(v -> action.run());
        containerApplicationButtons.addView(button);
        containerApplicationButtons.addView(createDivider());
    }

    private void openDriverHistoryFragment() {
        Log.d("KLIK", "Kliknuto na Ride history!");
        DriverHistoryFragment historyFragment = new DriverHistoryFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, historyFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openDriverRegistrationFragment() {
        DriverRegistrationFragment fragment = new DriverRegistrationFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.flFragment, fragment).addToBackStack(null).commit();
    }

    private void openFavoriteRoutesFragment() {
        FavoriteRoutesFragment fragment = new FavoriteRoutesFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.flFragment, fragment).addToBackStack(null).commit();
    }
}