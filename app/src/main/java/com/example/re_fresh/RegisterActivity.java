package com.example.re_fresh;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    // Firebase kimlik doğrulama nesnesi
    private FirebaseAuth mAuth;

    // Email ve Şifre giriş kutuları
    private EditText emailEditText, passwordEditText;

    // Kayıt ol butonu
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Firebase Authentication başlatılıyor
        mAuth = FirebaseAuth.getInstance();
        // XML dosyasındaki bileşenleri tanımlıyoruz
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.register_button);
        // Kayıt ol butonuna tıklayınca createAccount() fonksiyonu çalışacak
        registerButton.setOnClickListener(v -> createAccount());
    }
    // Kullanıcıyı Firebase ile kayıt eden fonksiyon
    private void createAccount() {
        // Kullanıcının girdiği email ve şifreyi al
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Email veya şifre boşsa uyarı ver
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email ve Şifre boş olamaz!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Firebase ile kullanıcı kaydı yap
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Kayıt başarılı
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                        Log.d("FIREBASE", "createUserWithEmail:success");

                        // Kayıt başarılı olursa istersen başka bir sayfaya yönlendirme yapılabilir
                        // Örnek: startActivity(new Intent(this, WelcomeActivity.class));

                    } else {
                        // Kayıt başarısız olduysa hata mesajı göster
                        Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
