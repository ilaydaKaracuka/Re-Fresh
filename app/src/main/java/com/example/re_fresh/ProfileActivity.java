package com.example.re_fresh;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Sistem çubuğu padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kullanıcı adını göster
        userNameTextView = findViewById(R.id.text_user_name);
        showAuthenticatedUserName();

        // İlk açılışta WasteFragment'i fragment_container'a koy
        if (savedInstanceState == null) {
            WasteFragment fragment = new WasteFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_profile_activity);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        bottomNavigationView.setItemIconTintList(null);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_profile);

        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

        // İsraf Bilgisi butonuna tıklanınca WasteFragment göster
        LinearLayout israfBilgisiBtn = findViewById(R.id.btn_waste_fragment);
        israfBilgisiBtn.setOnClickListener(v -> {
            WasteFragment fragment = new WasteFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Ayarlar butonuna tıklanınca SettingsFragment göster
        LinearLayout ayarlarBtn = findViewById(R.id.btn_settings_fragment);
        ayarlarBtn.setOnClickListener(v -> {
            SettingsFragment fragment = new SettingsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void showAuthenticatedUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            if (fullName != null && !fullName.isEmpty()) {
                                userNameTextView.setText(fullName);
                            } else {
                                userNameTextView.setText("Ad bilgisi yok");
                                Log.w("Firestore", "fullName alanı boş");
                            }
                        } else {
                            userNameTextView.setText("Kullanıcı bilgisi bulunamadı");
                            Log.w("Firestore", "Belge yok");
                        }
                    })
                    .addOnFailureListener(e -> {
                        userNameTextView.setText("Hata oluştu");
                        Log.e("Firestore", "Kullanıcı bilgisi alınamadı", e);
                    });
        } else {
            userNameTextView.setText("Kullanıcı yok");
            Log.e("FirebaseAuth", "Giriş yapan kullanıcı bulunamadı");
        }
    }

}
