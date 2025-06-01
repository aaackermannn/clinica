package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextSignUpEmail;
    private EditText editTextSignUpPassword;
    private Button buttonSignUpMedID;
    private TextView textViewGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpEmail = findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        buttonSignUpMedID = findViewById(R.id.buttonSignUpMedID);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        buttonSignUpMedID.setOnClickListener(v -> {
            String email = editTextSignUpEmail.getText().toString().trim();
            String password = editTextSignUpPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextSignUpEmail.setError("Неверный формат email");
                return;
            }

            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
            finish();
        });

        textViewGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}




