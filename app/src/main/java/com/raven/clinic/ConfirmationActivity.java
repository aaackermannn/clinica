package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Получаем данные из Intent
        String doctorName = getIntent().getStringExtra("doctor_name");
        String doctorSpecialty = getIntent().getStringExtra("doctor_specialty");
        long dateTimeMillis = getIntent().getLongExtra("date_time", 0);

        // Форматируем дату и время
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String dateStr = dateFormat.format(new Date(dateTimeMillis));
        String timeStr = timeFormat.format(new Date(dateTimeMillis));

        // Удостоверяемся, что в activity_confirmation.xml есть именно такие IDs
        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        TextView tvSpecialty = findViewById(R.id.tvSpecialty);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);

        // Заполняем UI
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
        tvDate.setText("Дата: " + dateStr);
        tvTime.setText("Время: " + timeStr);

        Button btnBackToHub = findViewById(R.id.btnBackToHub);
        Button btnCancelBooking = findViewById(R.id.btnCancelBooking);

        btnBackToHub.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        btnCancelBooking.setOnClickListener(v -> {
            // Просто закрываем экран, возвращаясь на предыдущий
            finish();
        });
    }
}


