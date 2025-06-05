package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextSignUpEmail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpConfirmPassword;
    private EditText editTextNickname;
    private Button buttonSignUpMedID;
    private TextView textViewGoToLogin;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Инициализация FirebaseAuth и Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ссылки на View
        editTextSignUpEmail = findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        editTextSignUpConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);
        editTextNickname = findViewById(R.id.editTextNickname);
        buttonSignUpMedID = findViewById(R.id.buttonSignUpMedID);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        // Обработчик кнопки «Зарегистрироваться»
        buttonSignUpMedID.setOnClickListener(v -> {
            String email = editTextSignUpEmail.getText().toString().trim();
            String password = editTextSignUpPassword.getText().toString().trim();
            String confirm = editTextSignUpConfirmPassword.getText().toString().trim();
            String nickname = editTextNickname.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextSignUpEmail.setError("Неверный формат email");
                return;
            }
            if (!password.equals(confirm)) {
                editTextSignUpConfirmPassword.setError("Пароли не совпадают");
                return;
            }
            if (password.length() < 6) {
                editTextSignUpPassword.setError("Минимум 6 символов");
                return;
            }

            // Создание пользователя в FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, authTask -> {
                        if (authTask.isSuccessful()) {
                            // Регистрация в FirebaseAuth прошла успешно
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                // Переходим на главный экран ДО записи данных в Firestore
                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                                // Сохраняем email и nickname в Firestore (в фоне)
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", email);
                                userMap.put("nickname", nickname);

                                db.collection("users")
                                        .document(uid)
                                        .set(userMap)
                                        .addOnFailureListener(e ->
                                                Toast.makeText(SignUpActivity.this,
                                                        "Ошибка сохранения профиля: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show()
                                        );
                            }
                        } else {
                            // Ошибка при регистрации
                            Toast.makeText(SignUpActivity.this,
                                    "Ошибка при регистрации: " + authTask.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Ссылка «Уже есть аккаунт? Войти»
        textViewGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}
