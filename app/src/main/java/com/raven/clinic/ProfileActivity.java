package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgUserAvatar;
    private TextView tvNickname, tvEmail;
    private LinearLayout btnChangePassword, btnHelp;
    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Инициализация Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ссылки на View
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvNickname    = findViewById(R.id.tvNickname);
        tvEmail       = findViewById(R.id.tvEmail);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnHelp       = findViewById(R.id.btnHelp);
        btnLogout     = findViewById(R.id.btnLogout);

        // Устанавливаем аватар (по желанию можно заменить)
        imgUserAvatar.setImageResource(R.drawable.user_placeholder);

        // Получаем текущего пользователя
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Если нет авторизованного пользователя, возвращаем на экран входа
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();
        // Запросим данные из Firestore
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String nickname = documentSnapshot.getString("nickname");
                        tvEmail.setText(email != null ? email : "—");
                        tvNickname.setText(nickname != null ? nickname : "—");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this,
                        "Ошибка загрузки профиля: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());

        // Обработчик клика «Сменить пароль»
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
        });

        // Обработчик клика «Справка» (можете реализовать нужное действие)
        btnHelp.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Здесь откроется справка", Toast.LENGTH_SHORT).show();
        });

        // Обработчик кнопки «Выйти»
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        // BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // Уже здесь
            }
            return false;
        });
    }
}
