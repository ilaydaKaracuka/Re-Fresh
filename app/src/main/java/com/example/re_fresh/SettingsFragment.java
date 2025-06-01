package com.example.re_fresh;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // ✅ Checkbox'ları tanımla
        CheckBox cbSonGun = view.findViewById(R.id.checkbox_son_gun);
        CheckBox cb3GunOnce = view.findViewById(R.id.checkbox_3_gun_once);
        CheckBox cb1HaftaOnce = view.findViewById(R.id.checkbox_1_hafta_once);
        CheckBox cb1AyOnce = view.findViewById(R.id.checkbox_1_ay_once);

        CheckBox[] checkBoxes = {cbSonGun, cb3GunOnce, cb1HaftaOnce, cb1AyOnce};

        for (CheckBox cb : checkBoxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (CheckBox otherCb : checkBoxes) {
                        if (otherCb != buttonView) {
                            otherCb.setChecked(false);
                        }
                    }
                }
            });
        }

        // Hesap silme butonu
        Button deleteAccountBtn = view.findViewById(R.id.btnDeleteAccount);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        // Çıkış yapma butonu
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

        dialogView.findViewById(R.id.btnCancelAccountDeletion).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnConfirmAccountDeletion).setOnClickListener(v -> {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(getContext(), "Kullanıcı oturumu bulunamadı.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            String userId = currentUser.getUid();

            // 1. Firestore'dan kullanıcı verisini sil
            FirebaseFirestore.getInstance()
                    .collection("users")  // Koleksiyon adın farklıysa değiştir
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // 2. Firebase Authentication'dan kullanıcıyı sil
                        currentUser.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Hesap başarıyla silindi.", Toast.LENGTH_SHORT).show();

                                        // 3. Çıkış yap ve login ekranına yönlendir
                                        FirebaseAuth.getInstance().signOut();

                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Geri tuşunu engelle
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "Authentication silme hatası: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Firestore silme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

            dialog.dismiss();
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
                // Oturum bilgisini temizle
                requireActivity().getSharedPreferences("Re-Fresh", getContext().MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                Toast.makeText(getContext(), "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // LoginActivity'ye yönlendir
                startActivity(new Intent(getActivity(), LoginActivity.class));

                // Geçerli aktiviteyi kapat
                requireActivity().finish();
            }
        });

        dialog.show();
    }

}
