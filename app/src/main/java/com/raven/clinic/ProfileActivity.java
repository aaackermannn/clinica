package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgUserAvatar;
    private TextView tvFullName, tvEmail;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvFullName    = findViewById(R.id.tvFullName);
        tvEmail       = findViewById(R.id.tvEmail);
        btnLogout     = findViewById(R.id.btnLogout);

        // Пока статические данные
        tvFullName.setText("Иван Иванов");
        tvEmail.setText("ivan@example.com");
        imgUserAvatar.setImageResource(android.R.drawable.sym_def_app_icon);

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finishAffinity();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        // Отмечаем пункт «Профиль»
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // уже здесь
            }
            return false;
        });
    }
}
