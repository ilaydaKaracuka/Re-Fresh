package com.example.re_fresh;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

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

        skipButton.setOnClickListener(v -> goToWelcome());
        nextButton.setOnClickListener(v -> {
            currentPage++;
            updateContent();
        });

        updateContent();
    }

    private void updateContent() {
        // Varsayılan marginler
        setMargins(onboardingImage, 24, 0);
        setMargins(titleText, 16, 0);

        switch (currentPage) {
            case 0:
                onboardingImage.setImageResource(R.drawable.onboarding_1);
                pagination.setImageResource(R.drawable.pagination_1);
                titleText.setText("Ürünlerini\nkolayca ekle!");
                descText.setText("Ürünlerinin son kullanma tarihini \nkaydet, düzenli takip et.");
                nextButton.setText("İleri");
                break;

            case 1:
                onboardingImage.setImageResource(R.drawable.onboarding_2);
                pagination.setImageResource(R.drawable.pagination_2);
                titleText.setText("Zamanında\nbildirim al!");
                descText.setText("Tarihi yaklaşan ürünler için \nhatırlatma al, israfı önle.");
                nextButton.setText("İleri");
                break;

            case 2:
                onboardingImage.setImageResource(R.drawable.onboarding_3);
                pagination.setImageResource(R.drawable.pagination_3);
                titleText.setText("Daha Bilinçli Tüketim!");
                descText.setText("Tasarruf et, doğaya katkı sağla.");
                nextButton.setText("Başla");

                // onboarding_3 için biraz yukarı taşı
                setMargins(onboardingImage, 8, 0);
                setMargins(titleText, 8, 0);
                break;

            default:
                goToWelcome();
                break;
        }
    }

    private void setMargins(TextView view, int top, int bottom) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = dpToPx(top);
        params.bottomMargin = dpToPx(bottom);
        view.setLayoutParams(params);
    }

    private void setMargins(ImageView view, int top, int bottom) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = dpToPx(top);
        params.bottomMargin = dpToPx(bottom);
        view.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void goToWelcome() {
        startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
        finish();
    }
}
