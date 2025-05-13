package com.example.re_fresh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddProductActivity extends AppCompatActivity {

    private ImageView qrCode, gallery, kamera, urunResim;
    private ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        qrCode = findViewById(R.id.btnQrCode);
        gallery = findViewById(R.id.btnGallery);
        urunResim = findViewById(R.id.imageViewUrunResim);
        kamera =findViewById(R.id.btnKamera);


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        urunResim.setImageURI(selectedImageUri);

                        // 🎯 Resim başarıyla eklendiyse butonları gizle
                        kamera.setVisibility(View.GONE);
                        gallery.setVisibility(View.GONE);
                        qrCode.setVisibility(View.GONE); //resim eklendiyse bu imageviewlar görünmez olurlar
                    }
                }
        );   //galeriden alınan resim imageview içine eklenir.

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            urunResim.setImageBitmap(imageBitmap);

                            // 🎯 Resim başarıyla eklendiyse butonları gizle
                            kamera.setVisibility(View.GONE);
                            gallery.setVisibility(View.GONE);
                            qrCode.setVisibility(View.GONE);
                        }
                    }
                }
        );


        kamera.setOnClickListener(v -> openCamera()); //şimdilik sadece kamerayı açıyor
        gallery.setOnClickListener(v -> openGallery());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_add_product_activity);
        bottomNavigationView.setItemRippleColor(null);
        bottomNavigationView.setItemBackground(null);
        bottomNavigationView.setItemIconTintList(null);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_add);

        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent); // Artık launcher üzerinden çağırıyoruz
        }
    }


    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhoto);
    }
}
