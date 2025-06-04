package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmationMessage;
    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty, tvDateTime;
    private Button btnGoToHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvConfirmationMessage = findViewById(R.id.tvConfirmationMessage);
        imgDoctorPhoto       = findViewById(R.id.imgConfirmedDoctorPhoto);
        tvDoctorName         = findViewById(R.id.tvConfirmedDoctorName);
        tvSpecialty          = findViewById(R.id.tvConfirmedSpecialty);
        tvDateTime           = findViewById(R.id.tvConfirmedDateTime);
        btnGoToHub           = findViewById(R.id.btnGoToHub);

        // Получаем данные из Intent
        Intent intent = getIntent();
        String doctorName      = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");
        String doctorPhoto     = intent.getStringExtra("doctor_photo");
        String dateTime        = intent.getStringExtra("date_time");

        // Устанавливаем текст сообщения
        tvConfirmationMessage.setText("Вы записаны к " + doctorName);

        // Заполняем поля врача
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
        tvDateTime.setText("Дата/время: " + dateTime);

        int resId = getResources().getIdentifier(
                doctorPhoto.replace(".png", ""),
                "drawable",
                getPackageName()
        );
        if (resId != 0) {
            imgDoctorPhoto.setImageResource(resId);
        } else {
            imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
        }

        btnGoToHub.setOnClickListener(v -> {
            // Возвращаемся на главный экран
            startActivity(new Intent(ConfirmationActivity.this, HomeActivity.class));
            finishAffinity();
        });

        // BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ConfirmationActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ConfirmationActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
