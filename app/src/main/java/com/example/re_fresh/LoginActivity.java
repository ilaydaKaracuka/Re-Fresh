package com.example.re_fresh;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView, forgotPasswordText;
    private final String correctEmail = "ogulcan.erd@gmail.com";
    private final String correctPassword = "1234";
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.emailEditText2);
        passwordEditText = findViewById(R.id.passwordEditText2);
        loginButton = findViewById(R.id.btn_login);
        registerTextView = findViewById(R.id.loginText2); // "Kayıt Ol" text
        forgotPasswordText = findViewById(R.id.txt_forgot_password); // "Şifremi unuttum"

        // Şifre göster/gizle drawable ikon işlemi
        Drawable visibleIcon = ContextCompat.getDrawable(this, R.drawable.visible);
        Drawable notVisibleIcon = ContextCompat.getDrawable(this, R.drawable.not_visible);

        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Right drawable
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
                        return true;
                    }
                }
            }
            return false;
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        TextWatcher formWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValidity();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        emailEditText.addTextChangedListener(formWatcher);
        passwordEditText.addTextChangedListener(formWatcher);

        // Giriş butonu
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.equals(correctEmail) && password.equals(correctPassword)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "E-posta veya şifre yanlış", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFormValidity() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        boolean isValid = isValidEmail(email) && isValidPassword(password);
        loginButton.setEnabled(isValid);

        int buttonBackground = isValid ? R.drawable.rounded_button_green : R.drawable.rounded_button;
        loginButton.setBackgroundResource(buttonBackground);

        loginButton.setBackgroundTintList(isValid ? ContextCompat.getColorStateList(this, R.color.main_theme_color) : ContextCompat.getColorStateList(this, R.color.button_text_color_3));
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 4;
    }
}
