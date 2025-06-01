package com.example.re_fresh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        dialogView.findViewById(R.id.btnConfirmAccountDeletion).setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccountAndUserProducts();
        });

        dialog.show();
    }
    private void deleteAccountAndUserProducts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            String uid = user.getUid();

            db.collection("users")
                    .document(uid)
                    .collection("products")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete();
                        }
                        db.collection("users").document(uid).delete()
                                .addOnSuccessListener(unused -> Log.d("DELETE", "Kullanıcı verisi silindi"))
                                .addOnFailureListener(e -> Log.e("DELETE", "Kullanıcı verisi silinemedi", e));

                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Hesap tamamen silindi", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), WelcomeActivity.class));
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), "Lütfen tekrar giriş yapın", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DELETE", "Ürünler silinemedi", e);
                        Toast.makeText(getContext(), "Ürünler silinemedi", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.logout_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_logout_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_confirm_logout).setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();

            Toast.makeText(getContext(), "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        dialog.show();
    }

}
