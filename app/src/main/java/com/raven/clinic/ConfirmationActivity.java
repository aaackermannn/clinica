package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmationMessage;
    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty, tvDateTime;
    private Button btnCancelBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // 1) Находим View по id
        tvConfirmationMessage = findViewById(R.id.tvConfirmationMessage);
        imgDoctorPhoto       = findViewById(R.id.imgConfirmedDoctorPhoto);
        tvDoctorName         = findViewById(R.id.tvConfirmedDoctorName);
        tvSpecialty          = findViewById(R.id.tvConfirmedSpecialty);
        tvDateTime           = findViewById(R.id.tvConfirmedDateTime);
        btnCancelBooking     = findViewById(R.id.btnCancelBooking);

        // 2) Получаем данные из Intent
        Intent intent = getIntent();
        String doctorName      = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");
        String doctorPhoto     = intent.getStringExtra("doctor_photo");
        String dateTime        = intent.getStringExtra("date_time");

        // 3) Устанавливаем текст сообщения
        tvConfirmationMessage.setText("Вы записаны к " + doctorName);

        // 4) Заполняем поля врача
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
        tvDateTime.setText("Дата/время: " + dateTime);

        int resId = getResources().getIdentifier(
                doctorPhoto.replace(".png",""),
                "drawable",
                getPackageName()
        );
        if (resId != 0) {
            imgDoctorPhoto.setImageResource(resId);
        } else {
            imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
        }

        // 5) Обработка кнопки «Отменить запись»
        btnCancelBooking.setOnClickListener(v -> {
            Toast.makeText(this, "Запись отменена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ConfirmationActivity.this, HomeActivity.class));
            finish();
        });

        // 6) BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
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





