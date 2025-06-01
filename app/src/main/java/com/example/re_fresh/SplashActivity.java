package com.example.re_fresh;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("first_time", true); // onboarding gösterildi mi?

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // gerçek giriş durumu

            if (isFirstTime) {
                // Kullanıcı onboarding görmemiş
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            } else if (currentUser == null) {
                // Onboarding geçmiş ama giriş yapmamış
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            } else {
                // Hem onboarding tamamlanmış hem de giriş yapılmış
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            finish();
        }, 3000);


    }
}