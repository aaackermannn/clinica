package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignUpMedID;
    private TextView textViewGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail           = findViewById(R.id.editTextSignUpEmail);
        editTextPassword        = findViewById(R.id.editTextSignUpPassword);
        editTextConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);
        buttonSignUpMedID       = findViewById(R.id.buttonSignUpMedID);
        textViewGoToLogin       = findViewById(R.id.textViewGoToLogin);

        buttonSignUpMedID.setOnClickListener(v -> {
            String email    = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirm  = editTextConfirmPassword.getText().toString().trim();

            // Проверяем заполнение
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            // Проверяем формат email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Неверный формат email");
                return;
            }
            // Проверяем совпадение паролей
            if (!password.equals(confirm)) {
                editTextConfirmPassword.setError("Пароли не совпадают");
                return;
            }

            // Если всё ок, переходим на HomeActivity
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
            finish();
        });

        textViewGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}





