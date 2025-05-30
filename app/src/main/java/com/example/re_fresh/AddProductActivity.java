package com.example.re_fresh;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddProductActivity extends AppCompatActivity {

    private EditText etUrunAdi;
    private Button btnKaydet;
    private ImageView qrCode, gallery, kamera, urunResim;
    private ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;

    // CardView kategori butonları (id'ler düzenlendi)
    private CardView cvGida, cvIcecek, cvIlac, cvKozmetik, cvTemizlik, cvDiger;

    private String selectedCategory = "";

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
        kamera = findViewById(R.id.btnKamera);

        // Doğru ID'lerle kategori kartlarını bağla
        cvGida = findViewById(R.id.cvGida);
        cvIcecek = findViewById(R.id.cvIcecek);
        cvIlac = findViewById(R.id.cvIlac);
        cvKozmetik = findViewById(R.id.cvKozmetik);
        cvTemizlik = findViewById(R.id.cvTemizlik);
        cvDiger = findViewById(R.id.cvDiger);

        // Kartlara tıklanabilirlik ver
        cvGida.setOnClickListener(v -> selectCategory("Gıda"));
        cvIcecek.setOnClickListener(v -> selectCategory("İçecek"));
        cvIlac.setOnClickListener(v -> selectCategory("İlaç"));
        cvKozmetik.setOnClickListener(v -> selectCategory("Kozmetik"));
        cvTemizlik.setOnClickListener(v -> selectCategory("Temizlik"));
        cvDiger.setOnClickListener(v -> selectCategory("Diğer"));

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        urunResim.setImageURI(selectedImageUri);
                        kamera.setVisibility(View.GONE);
                        gallery.setVisibility(View.GONE);
                        qrCode.setVisibility(View.GONE);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            urunResim.setImageBitmap(imageBitmap);
                            kamera.setVisibility(View.GONE);
                            gallery.setVisibility(View.GONE);
                            qrCode.setVisibility(View.GONE);
                        }
                    }
                }
        );

        kamera.setOnClickListener(v -> openCamera());
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

        etUrunAdi = findViewById(R.id.etUrunAdi);
        btnKaydet = findViewById(R.id.btnKaydet);

        btnKaydet.setOnClickListener(v -> {
            String urunAdi = etUrunAdi.getText().toString().trim();

            if (urunAdi.isEmpty()) {
                Toast.makeText(this, "Ürün adı boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCategory.isEmpty()) {
                Toast.makeText(this, "Lütfen bir kategori seçin", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Ürün kaydedildi: " + urunAdi + " - " + selectedCategory, Toast.LENGTH_SHORT).show();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Kullanıcı oturumu yok", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = currentUser.getUid();

            // Ürün bilgilerini bir Map içine alıyoruz
            Map<String, Object> productData = new HashMap<>();
            productData.put("urunAdi", urunAdi);
            productData.put("kategori", selectedCategory);
            productData.put("timestamp", FieldValue.serverTimestamp());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .collection("products")
                    .add(productData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Ürün kaydedildi", Toast.LENGTH_SHORT).show();
                        etUrunAdi.setText("");
                        selectCategory(""); // Seçimi sıfırla
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Kayıt başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhoto);
    }

    // Seçilen kategoriyi tut ve kartlara arka plan rengi uygula
    private void selectCategory(String category) {
        selectedCategory = category;

        int selectedColor = getColor(R.color.main_theme_color);
        int defaultColor = getColor(R.color.white);

        cvGida.setCardBackgroundColor(category.equals("Gıda") ? selectedColor : defaultColor);
        cvIcecek.setCardBackgroundColor(category.equals("İçecek") ? selectedColor : defaultColor);
        cvIlac.setCardBackgroundColor(category.equals("İlaç") ? selectedColor : defaultColor);
        cvKozmetik.setCardBackgroundColor(category.equals("Kozmetik") ? selectedColor : defaultColor);
        cvTemizlik.setCardBackgroundColor(category.equals("Temizlik") ? selectedColor : defaultColor);
        cvDiger.setCardBackgroundColor(category.equals("Diğer") ? selectedColor : defaultColor);
    }
}
