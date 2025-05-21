package com.example.re_fresh;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.MotionEvent;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity {

    // Firebase kimlik doğrulama nesnesi
    private FirebaseAuth mAuth;

    private EditText fullNameEditText; // ⬅ Ad soyad kutusu

    // Email ve Şifre giriş kutuları
    private EditText emailEditText, passwordEditText;
    private TextView loginText; // Giriş Yap yazısı

    // Kayıt ol butonu
    private Button registerButton;
    private boolean isPasswordVisible = false; //şifre saklama

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
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.register_button);
        loginText = findViewById(R.id.loginText);
        // Şifre göster gizle özelliği

        Drawable visibleIcon = ContextCompat.getDrawable(this, R.drawable.visible);
        Drawable notVisibleIcon = ContextCompat.getDrawable(this, R.drawable.not_visible);

        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2;
                Drawable drawable = passwordEditText.getCompoundDrawables()[drawableEnd];
                if (drawable != null) {
                    int drawableWidth = drawable.getBounds().width();
                    int touchX = (int) event.getX();
                    int width = passwordEditText.getWidth();

                    if (touchX >= (width - drawableWidth - passwordEditText.getPaddingEnd())) {
                        isPasswordVisible = !isPasswordVisible;

                        if (isPasswordVisible) {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, visibleIcon, null);
                        } else {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, notVisibleIcon, null);
                        }

                        passwordEditText.setSelection(passwordEditText.length());
                        v.performClick();
                        return true;
                    }
                }
            }
            return false;
        });
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Kayıt ol butonuna tıklayınca createAccount() fonksiyonu çalışacak
        registerButton.setOnClickListener(v -> createAccount());
    }
    // Kullanıcıyı Firebase ile kayıt eden fonksiyon
    private void createAccount() {
        // Kullanıcının girdiği email ve şifreyi al
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Email veya şifre boşsa uyarı ver
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ad Soyad, Email ve Şifre boş olamaz!", Toast.LENGTH_SHORT).show();
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

                        // Giriş ekranına yönlendirme
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish(); // Bu ekranı kapat

                    } else {
                        // Kayıt başarısız olduysa hata mesajı göster
                        Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
