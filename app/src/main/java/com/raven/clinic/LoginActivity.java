package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLoginMedID;
    private TextView textViewSignUp;

    // FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализируем FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Находим элементы
        editTextEmail    = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLoginMedID = findViewById(R.id.buttonLoginMedID);
        textViewSignUp   = findViewById(R.id.textViewSignUp);

        // Обработчик входа
        buttonLoginMedID.setOnClickListener(v -> {
            String email    = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // 1) Проверяем пустые поля
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            // 2) Проверяем формат e‑mail
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Неверный формат email");
                return;
            }
            // 3) Вызываем Firebase для аутентификации
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Вход выполнен успешно
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                // Ошибка авторизации (неверный пароль или пользователь не найден)
                                String errorMessage = task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Ошибка авторизации";
                                Toast.makeText(LoginActivity.this,
                                        "Ошибка: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        // Обработчик перехода на регистрацию
        textViewSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }
}
