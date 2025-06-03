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

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignUpMedID;
    private TextView textViewGoToLogin;

    // FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Инициализируем FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Находим элементы по id
        editTextEmail           = findViewById(R.id.editTextSignUpEmail);
        editTextPassword        = findViewById(R.id.editTextSignUpPassword);
        editTextConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);
        buttonSignUpMedID       = findViewById(R.id.buttonSignUpMedID);
        textViewGoToLogin       = findViewById(R.id.textViewGoToLogin);

        // Обработчик нажатия «Зарегистрироваться»
        buttonSignUpMedID.setOnClickListener(v -> {
            String email    = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirm  = editTextConfirmPassword.getText().toString().trim();

            // 1) Проверяем пустые поля
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            // 2) Проверяем формат e‑mail
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Неверный формат email");
                return;
            }
            // 3) Проверяем совпадение паролей
            if (!password.equals(confirm)) {
                editTextConfirmPassword.setError("Пароли не совпадают");
                return;
            }
            // 4) Вызываем Firebase для создания нового пользователя
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Регистрация прошла успешно
                                Toast.makeText(SignUpActivity.this,
                                        "Регистрация прошла успешно",
                                        Toast.LENGTH_SHORT).show();
                                // После успешной регистрации — переходим на экран входа
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                // Если регистрация не удалась — показываем ошибку
                                String errorMessage = task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Ошибка регистрации";
                                Toast.makeText(SignUpActivity.this,
                                        "Ошибка: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        // Обработчик «Уже есть аккаунт? Войти»
        textViewGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}
