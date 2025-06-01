package com.example.re_fresh;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText fullNameEditText, emailEditText, passwordEditText;
    private TextView loginText;
    private Button registerButton;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Sistem çubuklarına göre padding ayarla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.register_button);
        loginText = findViewById(R.id.loginText);

        Drawable visibleIcon = ContextCompat.getDrawable(this, R.drawable.visible);
        Drawable notVisibleIcon = ContextCompat.getDrawable(this, R.drawable.not_visible);

        // Şifre görünürlük toggle
        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Sağdaki drawable indexi
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

        // Login yazısına tıklanınca LoginActivity'e geç
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsAndUpdateButton();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        fullNameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        registerButton.setEnabled(false);
        setButtonColor(false);

        registerButton.setOnClickListener(v -> createAccount());
    }

    private void setButtonColor(boolean isValid) {
        int buttonBackground = isValid ? R.drawable.rounded_button_green : R.drawable.rounded_button;
        registerButton.setBackgroundResource(buttonBackground);

        registerButton.setBackgroundTintList(isValid
                ? ContextCompat.getColorStateList(this, R.color.main_theme_color)
                : ContextCompat.getColorStateList(this, R.color.button_text_color_3));
    }

    private void checkFieldsAndUpdateButton() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean isValid = !fullName.isEmpty() && isValidEmail(email) && isValidPassword(password);
        registerButton.setEnabled(isValid);
        setButtonColor(isValid);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void createAccount() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ad Soyad, Email ve Şifre boş olamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                        Log.d("FIREBASE", "createUserWithEmail:success");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = user.getUid();

                        String[] nameParts = fullName.split(" ", 2);
                        String firstName = nameParts.length > 0 ? nameParts[0] : "";
                        String lastName = nameParts.length > 1 ? nameParts[1] : "";

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("firstName", firstName);
                        userData.put("lastName", lastName);
                        userData.put("email", email);

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Kullanıcı Firestore'a kaydedildi"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Firestore'a kayıt başarısız", e));

                        // *** Burada LoginActivity yerine MainActivity'ye yönlendirme yapıldı ***
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
