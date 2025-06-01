package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLoginMedId = findViewById(R.id.btnLoginMedId);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLoginMedId.setOnClickListener(v -> {
            // TODO: Здесь в будущем будет логика входа через MedID (API)
            // Пока просто переходим на HomeActivity:
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}

