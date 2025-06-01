package com.example.re_fresh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

//        skipButton.setOnClickListener(v -> goToWelcome());
//        nextButton.setOnClickListener(v -> {
//            currentPage++;
//            updateContent();
//        });
//
//        updateContent();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("first_time", true);


        skipButton.setOnClickListener(v -> goToWelcome());

        nextButton.setOnClickListener(v -> {
            currentPage++;
            if (currentPage > 2) {
                goToWelcome(); // sharedPreferences burada da yazılıyor
            } else {
                updateContent();
            }
        });

        updateContent();





    }

    private void updateContent() {
        switch (currentPage) {
            case 0:
                onboardingImage.setImageResource(R.drawable.onboarding_1);
                pagination.setImageResource(R.drawable.pagination_1);
                titleText.setText("Ürünlerini\nkolayca ekle!");
                descText.setText("Ürünlerinin son kullanma tarihini \nkaydet, düzenli takip et.");
                nextButton.setText("İleri");
                skipButton.setVisibility(View.VISIBLE);
                break;

            case 1:
                onboardingImage.setImageResource(R.drawable.onboarding_2);
                pagination.setImageResource(R.drawable.pagination_2);
                titleText.setText("Zamanında\nbildirim al!");
                descText.setText("Tarihi yaklaşan ürünler için \nhatırlatma al, israfı önle.");
                nextButton.setText("İleri");
                skipButton.setVisibility(View.VISIBLE);
                break;

            case 2:
                onboardingImage.setImageResource(R.drawable.onboarding_3);
                pagination.setImageResource(R.drawable.pagination_3);
                titleText.setText("Daha Bilinçli Tüketim!");
                descText.setText("Doğa için tasarruf et. Küçük adımlar, sürdürülebilir bir dünya yaratır.");
                nextButton.setText("İleri");
                skipButton.setVisibility(View.GONE);

                // nextButton boyutunu ayarla: height = 52dp, width = 335dp
                ViewGroup.LayoutParams params = nextButton.getLayoutParams();
                float scale = getResources().getDisplayMetrics().density;
                params.width = (int) (335 * scale);    // 335dp
                nextButton.setLayoutParams(params);
                break;

            default:
                goToWelcome();
                break;
        }
    }

//    private void goToWelcome() {
//        startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
//        finish();
//    }

    private void goToWelcome() {
        // Onboarding tamamlandı → shared preferences'e kaydet
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("first_time", false);
        editor.apply();

        // Welcome ekranına geç
        startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
        finish();
    }

}
