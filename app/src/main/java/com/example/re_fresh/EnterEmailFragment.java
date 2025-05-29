package com.example.re_fresh;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class EnterEmailFragment extends Fragment {

    private EditText emailEditText;
    private Button checkEmailButton;
    private ImageButton backLoginBtn;
    private FirebaseAuth mAuth;

    public EnterEmailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_email, container, false);

        emailEditText = view.findViewById(R.id.emailEditText1);
        mAuth = FirebaseAuth.getInstance();

        if (getActivity() != null) {
            checkEmailButton = getActivity().findViewById(R.id.forgot_password_button);
            backLoginBtn = getActivity().findViewById(R.id.backLoginBtn);

            if (checkEmailButton != null) {
                emailEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String email = s.toString().trim();
                        boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();

                        checkEmailButton.setBackgroundResource(
                                isValid ? R.drawable.rounded_button_green : R.drawable.rounded_button
                        );
                        checkEmailButton.setBackgroundTintList(
                                ContextCompat.getColorStateList(requireContext(),
                                        isValid ? R.color.main_theme_color : R.color.button_text_color_3)
                        );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                checkEmailButton.setOnClickListener(v -> {
                    String email = emailEditText.getText().toString().trim();

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(requireContext(), "Lütfen geçerli bir e-posta girin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Şifre sıfırlama linki e-posta adresinize gönderildi.", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (com.google.firebase.auth.FirebaseAuthInvalidUserException e) {
                                        Toast.makeText(requireContext(), "Bu email adresi kayıtlı değil.", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(requireContext(), "Hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                });
            }

            if (backLoginBtn != null) {
                backLoginBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        return view;
    }
}
