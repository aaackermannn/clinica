package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation();

        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Здесь должны быть реальные данные пользователя
        tvFullName.setText("Иван Иванов");
        tvEmail.setText("ivan@example.com");

        btnLogout.setOnClickListener(v -> {
            // Возвращаемся на экран входа
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity(); // Закрываем все активности
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.nav_add).setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorsActivity.class));
            finish();
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            // Уже здесь
        });
    }
}
