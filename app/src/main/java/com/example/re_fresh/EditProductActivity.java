package com.example.re_fresh;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProductActivity extends AppCompatActivity {

    private EditText etUrunAdi2;
    private Button btnKaydet2;
    private ImageView btnDelete2, btnBack2;

    private String productId;
    private String productName;
    private String productCategory;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Intent ile gelen verileri al
        productId = getIntent().getStringExtra("productId");
        productName = getIntent().getStringExtra("productName");
        productCategory = getIntent().getStringExtra("productCategory");

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etUrunAdi2 = findViewById(R.id.etUrunAdi2);
        btnKaydet2 = findViewById(R.id.btnKaydet2);
        btnDelete2 = findViewById(R.id.btnDelete2);
        btnBack2 = findViewById(R.id.btnBack2);

        etUrunAdi2.setText(productName);

        btnBack2.setOnClickListener(v -> finish());
        btnDelete2.setOnClickListener(v -> showDeleteDialog());
        btnKaydet2.setOnClickListener(v -> updateProduct());
    }

    private void showDeleteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.delete_product_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnSil = dialogView.findViewById(R.id.btnConfirmRemoveProduct);
        Button btnIptal = dialogView.findViewById(R.id.btnCancelRemoveProduct);

        btnSil.setOnClickListener(v -> {
            deleteProduct();
            dialog.dismiss();
        });

        btnIptal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteProduct() {
        db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ürün silindi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Silme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Silme", "Hata: ", e);
                });
    }

    private void updateProduct() {
        String yeniUrunAdi = etUrunAdi2.getText().toString().trim();

        if (yeniUrunAdi.isEmpty()) {
            etUrunAdi2.setError("Ürün adı boş olamaz");
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)
                .update("urunAdi", yeniUrunAdi)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ürün güncellendi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Güncelleme", "Hata: ", e);
                });
    }
}
