package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBackChangePassword;
    private EditText editTextCurrentPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;
    private Button buttonChangePassword;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Инициализация FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Ссылки на View
        btnBackChangePassword = findViewById(R.id.btnBackChangePassword);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);

        // Обработка кнопки «Назад»
        btnBackChangePassword.setOnClickListener(v -> {
            // Возврат на экран профиля
            startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
            finish();
        });

        // Обработка кнопки «Сменить пароль»
        buttonChangePassword.setOnClickListener(v -> {
            String currentPwd = editTextCurrentPassword.getText().toString().trim();
            String newPwd = editTextNewPassword.getText().toString().trim();
            String confirmPwd = editTextConfirmNewPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                editTextConfirmNewPassword.setError("Пароли не совпадают");
                return;
            }
            if (newPwd.length() < 6) {
                editTextNewPassword.setError("Минимум 6 символов");
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сначала переаутентифицируем пользователя, чтобы можно было сменить пароль
            AuthCredential credential = EmailAuthProvider.getCredential(
                    user.getEmail(),
                    currentPwd
            );
            user.reauthenticate(credential)
                    .addOnCompleteListener(reAuthTask -> {
                        if (reAuthTask.isSuccessful()) {
                            // После успешной переаутентификации меняем пароль
                            user.updatePassword(newPwd)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this,
                                                    "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
                                            // Возвращаемся на профиль
                                            startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this,
                                                    "Ошибка при смене пароля: " + updateTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Неверный текущий пароль", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // BottomNav: переходы на Home и Profile
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ChangePasswordActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
