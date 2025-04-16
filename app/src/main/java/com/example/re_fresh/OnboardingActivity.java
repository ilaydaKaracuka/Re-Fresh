package com.example.re_fresh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private int currentPage = 0;

    private ImageView onboardingImage, pagination;
    private TextView titleText, descText;
    private Button skipButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        onboardingImage = findViewById(R.id.onboardingImage);
        pagination = findViewById(R.id.paginationImage);
        titleText = findViewById(R.id.titleText);
        descText = findViewById(R.id.descriptionText);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);

        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        nextButton.setOnClickListener(v -> {
            currentPage++;
            updateContent();
        });
    }

    private void updateContent() {
        switch (currentPage) {
            case 1:
                onboardingImage.setImageResource(R.drawable.onboarding_2);
                pagination.setImageResource(R.drawable.pagination_2);
                titleText.setText("Zamanında\nbildirim al!");
                descText.setText("Tarihi yaklaşan ürünler için \nhatırlatma al, israfı önle.");
                break;
            case 2:
                onboardingImage.setImageResource(R.drawable.onboarding_3);
                pagination.setImageResource(R.drawable.pagination_3);
                titleText.setText("Daha Bilinçli Tüketim!");
                descText.setText("Tasarruf et, doğaya katkı sağla.");
                nextButton.setText("Başla");
                break;
            case 3:
                startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
                finish();
                break;
        }
    }
}
