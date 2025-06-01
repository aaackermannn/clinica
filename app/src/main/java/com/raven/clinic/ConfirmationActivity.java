package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    private ImageView imgConfirmDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty, tvDate, tvTime;
    private Button btnBackToHub, btnCancelBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // 1) Получаем переданные данные
        Intent intent = getIntent();
        String doctorName = intent.getStringExtra("doctor_name");
        String doctorSpecialty = intent.getStringExtra("doctor_specialty");
        String doctorPhoto = intent.getStringExtra("doctor_photo");
        String dateTime = intent.getStringExtra("date_time");

        // 2) Находим View
        imgConfirmDoctorPhoto = findViewById(R.id.imgConfirmDoctorPhoto);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        btnBackToHub = findViewById(R.id.btnBackToHub);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);

        // Устанавливаем фото врача
        int resId = getResources().getIdentifier(
                doctorPhoto.replace(".png", ""),
                "drawable",
                getPackageName()
        );
        if (resId != 0) {
            imgConfirmDoctorPhoto.setImageResource(resId);
        } else {
            imgConfirmDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
        }

        // Устанавливаем текстовые поля
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);

        // Парсим dateTime на дату и время
        // Формат: "25 июня 2025, 10:00"
        if (dateTime.contains(",")) {
            String[] parts = dateTime.split(",");
            tvDate.setText("Дата: " + parts[0].trim());
            tvTime.setText("Время: " + parts[1].trim());
        } else {
            tvDate.setText("Дата/Время: " + dateTime);
            tvTime.setText("");
        }

        // Кнопка «На главный экран»
        btnBackToHub.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmationActivity.this, HomeActivity.class));
            finishAffinity();
        });

        // Кнопка «Отменить запись»
        btnCancelBooking.setOnClickListener(v -> {
            finish(); // просто закрываем экран
        });
    }
}



