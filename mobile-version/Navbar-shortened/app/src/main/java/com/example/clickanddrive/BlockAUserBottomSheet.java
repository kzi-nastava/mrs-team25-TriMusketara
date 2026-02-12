package com.example.clickanddrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BlockAUserBottomSheet extends BottomSheetDialogFragment {

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvEmail;
    private TextView tvPhone;
    private Button btnBlock;
    private Button btnNote;

    public static BlockAUserBottomSheet newInstance(String name, String surname, String address, String email, String phone) {
        BlockAUserBottomSheet sheet = new BlockAUserBottomSheet();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("surname", surname);
        args.putString("address", address);
        args.putString("email", email);
        args.putString("phone", phone);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_block_user, container, false);

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

        if (getArguments() != null) {
            String full = getArguments().getString("name") + " " + getArguments().getString("surname");
            tvName.setText(full);
            tvAddress.setText(getArguments().getString("address"));
            tvEmail.setText(getArguments().getString("email"));
            tvPhone.setText(getArguments().getString("phone"));
        }
    }

    private void setUpListeners() {
        btnBlock.setOnClickListener(v -> {
            Toast.makeText(getContext(), "User blocked", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnNote.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Note added", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

}
