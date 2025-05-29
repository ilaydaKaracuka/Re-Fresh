package com.example.re_fresh;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Sistem çubuğu içeriğine göre padding ayarla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Navigation ayarları
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_profile_activity);
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        bottomNavigationView.setItemIconTintList(null);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_profile);

        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

        // İsraf Bilgisi butonu tıklanınca fragment göster
        LinearLayout israfBilgisiBtn = findViewById(R.id.btn_waste_fragment);
        israfBilgisiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WasteFragment fragment = new WasteFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Ayarlar butonu tıklanınca fragment göster
        LinearLayout ayarlarBtn = findViewById(R.id.btn_settings_fragment);
        ayarlarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsFragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
