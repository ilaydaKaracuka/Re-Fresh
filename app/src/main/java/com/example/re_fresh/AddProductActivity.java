package com.example.re_fresh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText etUrunAdi;
    private Button btnKaydet;
    private ImageView qrCode, gallery, kamera, urunResim;
    private ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;

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

        // View binding
        qrCode = findViewById(R.id.btnQrCode);
        gallery = findViewById(R.id.btnGallery);
        urunResim = findViewById(R.id.imageViewUrunResim);
        kamera = findViewById(R.id.btnKamera);

        cvGida = findViewById(R.id.cvGida);
        cvIcecek = findViewById(R.id.cvIcecek);
        cvIlac = findViewById(R.id.cvIlac);
        cvKozmetik = findViewById(R.id.cvKozmetik);
        cvTemizlik = findViewById(R.id.cvTemizlik);
        cvDiger = findViewById(R.id.cvDiger);

        // Kategori seçimleri
        cvGida.setOnClickListener(v -> selectCategory("Gıda"));
        cvIcecek.setOnClickListener(v -> selectCategory("İçecek"));
        cvIlac.setOnClickListener(v -> selectCategory("İlaç"));
        cvKozmetik.setOnClickListener(v -> selectCategory("Kozmetik"));
        cvTemizlik.setOnClickListener(v -> selectCategory("Temizlik"));
        cvDiger.setOnClickListener(v -> selectCategory("Diğer"));

        // Galeri sonucu
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        urunResim.setImageURI(selectedImageUri);
                        hideImageSourceButtons();
                    }
                }
        );

        // Kamera sonucu
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (imageBitmap != null) {
                            urunResim.setImageBitmap(imageBitmap);
                            hideImageSourceButtons();
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

        // btnKaydet listener'ı popup açacak şekilde değiştirdik
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

            showExpiryInputDialog(urunAdi, selectedCategory);
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

    private void hideImageSourceButtons() {
        kamera.setVisibility(View.GONE);
        gallery.setVisibility(View.GONE);
        qrCode.setVisibility(View.GONE);
    }

//    private void selectCategory(String category) {
//        selectedCategory = category;
//
//        int selectedColor = getColor(R.color.main_theme_color);
//        int defaultColor = getColor(R.color.white);
//
//        cvGida.setCardBackgroundColor(category.equals("Gıda") ? selectedColor : defaultColor);
//        cvIcecek.setCardBackgroundColor(category.equals("İçecek") ? selectedColor : defaultColor);
//        cvIlac.setCardBackgroundColor(category.equals("İlaç") ? selectedColor : defaultColor);
//        cvKozmetik.setCardBackgroundColor(category.equals("Kozmetik") ? selectedColor : defaultColor);
//        cvTemizlik.setCardBackgroundColor(category.equals("Temizlik") ? selectedColor : defaultColor);
//        cvDiger.setCardBackgroundColor(category.equals("Diğer") ? selectedColor : defaultColor);
//    }

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

        // Kategoriye göre resmi set et
        int drawableRes = getDrawableResourceByCategory(category);
        if (drawableRes != 0) {
            urunResim.setImageResource(drawableRes);
            hideImageSourceButtons();  // İstersen seçince butonları gizle
        } else {
            urunResim.setImageDrawable(null);  // Resim yoksa sıfırla
        }
    }


    // Tarih popup'u gösteren method
    private void showExpiryInputDialog(String urunAdi, String kategori) {
        View dialogView = getLayoutInflater().inflate(R.layout.manual_expiry_input, null);

        EditText expiryInput = dialogView.findViewById(R.id.expiryInput);
        Button btnSaveExpiry = dialogView.findViewById(R.id.btn_save_expiry);

        // Tarih seçici açılması için listener ekliyoruz
        expiryInput.setOnClickListener(v -> {
            // Bugünün tarihi ile başlat
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                // Aylar 0-11 olduğu için +1 yapıyoruz
                String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                expiryInput.setText(formattedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnSaveExpiry.setOnClickListener(v -> {
            String expiryDate = expiryInput.getText().toString().trim();

            if (expiryDate.isEmpty()) {
                Toast.makeText(this, "Lütfen son kullanma tarihi girin", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProductToFirestore(urunAdi, kategori, expiryDate);
            dialog.dismiss();
        });

        dialog.show();
    }


    private void saveProductToFirestore(String urunAdi, String kategori, String expiryDateString) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Kullanıcı oturumu yok", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        Date expiryDate;
        try {
            expiryDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expiryDateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Tarih biçimi hatalı", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> productData = new HashMap<>();
        productData.put("urunAdi", urunAdi);
        productData.put("kategori", kategori);
        productData.put("expiryDate", expiryDate);
        productData.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("products")
                .add(productData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ürün ve tarih kaydedildi", Toast.LENGTH_SHORT).show();

                    etUrunAdi.setText("");
                    selectCategory("");
                    urunResim.setImageDrawable(null);
                    kamera.setVisibility(View.VISIBLE);
                    gallery.setVisibility(View.VISIBLE);
                    qrCode.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Kayıt başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private int getDrawableResourceByCategory(String category) {
        switch (category) {
            case "Gıda":
                return R.drawable.gida;       // drawable/gida.png gibi varsayalım
            case "İçecek":
                return R.drawable.icecek;
            case "İlaç":
                return R.drawable.ilac;
            case "Kozmetik":
                return R.drawable.kozmetik;
            case "Temizlik":
                return R.drawable.temizlik;
            case "Diğer":
                return R.drawable.diger;
            default:
                return 0;  // Veya default bir resim koyabilirsin
        }
    }

}
