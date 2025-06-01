package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_add).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            // уже здесь
        });
    }
}

