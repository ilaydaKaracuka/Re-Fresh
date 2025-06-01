package com.example.re_fresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;  // FirebaseAuth nesnesi eklendi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(MainActivity.this, productList);
        recyclerView.setAdapter(adapter);

        loadProductsFromFirestore();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_main_activity);
        bottomNavigationView.setItemRippleColor(null);
        bottomNavigationView.setItemBackground(null);
        bottomNavigationView.setItemIconTintList(null);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);

        bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Kullanıcı giriş yapmamışsa LoginActivity'ye yönlendir
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // MainActivity kapanır
        }
    }

    private void loadProductsFromFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Kullanıcı oturumu bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String urunAdi = doc.getString("urunAdi");
                            String kategori = doc.getString("kategori");
                            Timestamp timestamp = doc.getTimestamp("expiryDate");
                            Date expiryDate = timestamp != null ? timestamp.toDate() : null;
                            String docId = doc.getId();

                            if (urunAdi == null || kategori == null || expiryDate == null) {
                                continue; // eksik veri varsa atla
                            }

                            int resim = R.drawable.product_example;

                            // Tarihi "dd/MM/yyyy" formatında al
                            String formattedDate = sdf.format(expiryDate);

                            // Kalan gün sayısını hesapla
                            String daysLeft = calculateDaysLeft(expiryDate);

                            // Tarih ve kalan gün bilgisini birlikte göster
                            String displayDate = formattedDate + " (" + daysLeft + ")";

                            productList.add(new Product(urunAdi, kategori, displayDate, resim, docId));
                        } catch (Exception e) {
                            Log.e("ProductLoadError", "Hatalı belge: " + e.getMessage(), e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ürünler alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Veri çekme hatası: ", e);
                });
    }

    private String calculateDaysLeft(Date expiryDate) {
        if (expiryDate == null) {
            return "Tarih yok";
        }

        Date today = new Date();
        long diff = expiryDate.getTime() - today.getTime();
        long daysLeft = diff / (1000 * 60 * 60 * 24);

        if (daysLeft < 0) {
            return "Süresi doldu";
        } else {
            return daysLeft + " gün kaldı";
        }
    }

}
