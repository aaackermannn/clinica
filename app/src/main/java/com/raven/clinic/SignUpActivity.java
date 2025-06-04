package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

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

        // Инициализация Firebase
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

            // 1) Проверка, что все поля заполнены
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2) Проверка валидности email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextSignUpEmail.setError("Неверный формат email");
                return;
            }

            // 3) Проверка совпадения паролей
            if (!password.equals(confirm)) {
                editTextSignUpConfirmPassword.setError("Пароли не совпадают");
                return;
            }

            // 4) Выводим Toast и Log перед запросом, чтобы видеть, что он запустился
            Toast.makeText(this, "Пытаемся зарегистрировать пользователя...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Регистрация: email=" + email + ", nickname=" + nickname);

            // Создание пользователя в FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Успешно зарегистрирован
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                Log.d(TAG, "FirebaseAuth: пользователь создан, uid=" + uid);

                                // Сохраняем дополнительную информацию (никнейм и email) в Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", email);
                                userMap.put("nickname", nickname);

                                db.collection("users")
                                        .document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Firestore: данные пользователя успешно сохранены");
                                            Toast.makeText(SignUpActivity.this,
                                                    "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                            // Переход на экран входа
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Ошибка при сохранении данных в Firestore", e);
                                            Toast.makeText(SignUpActivity.this,
                                                    "Ошибка сохранения данных: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Непредвиденный случай: user == null
                                Log.e(TAG, "FirebaseAuth: task.isSuccessful(), но user == null");
                                Toast.makeText(SignUpActivity.this,
                                        "Не удалось получить данные нового пользователя",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Ошибка при регистрации
                            Exception e = task.getException();
                            String message = (e != null) ? e.getMessage() : "Неизвестная ошибка";
                            Log.e(TAG, "Ошибка при регистрации:", e);
                            Toast.makeText(SignUpActivity.this,
                                    "Ошибка при регистрации: " + message,
                                    Toast.LENGTH_LONG).show();
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
