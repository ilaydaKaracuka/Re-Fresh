package com.example.re_fresh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Zorunlu boş constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button deleteAccountBtn = view.findViewById(R.id.btnDeleteAccount);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        Button logoutBtn = view.findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        return view;
    }

    private void showDeleteAccountDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.delete_account_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancelAccountDeletion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnConfirmAccountDeletion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Hesap silindi.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // startActivity(new Intent(getActivity(), LoginActivity.class));
                // getActivity().finish();
            }
        });

        dialog.show();
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.logout_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_logout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_confirm_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // startActivity(new Intent(getActivity(), LoginActivity.class));
                // getActivity().finish();
            }
        });

        dialog.show();
    }
}
