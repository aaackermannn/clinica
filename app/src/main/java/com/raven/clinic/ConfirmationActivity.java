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

        // Получаем данные о записи
        String doctorName = getIntent().getStringExtra("doctor_name");
        String doctorSpecialty = getIntent().getStringExtra("doctor_specialty");
        long dateTimeMillis = getIntent().getLongExtra("date_time", 0);

        // Форматируем дату и время
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String date = dateFormat.format(new Date(dateTimeMillis));
        String time = timeFormat.format(new Date(dateTimeMillis));

        // Устанавливаем данные в UI
        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        TextView tvSpecialty = findViewById(R.id.tvSpecialty);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);

        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
        tvDate.setText("Дата: " + date);
        tvTime.setText("Время: " + time);

        Button btnBackToHub = findViewById(R.id.btnBackToHub);
        Button btnCancelBooking = findViewById(R.id.btnCancelBooking);

        btnBackToHub.setOnClickListener(v -> {
            // Возвращаемся на главный экран
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        btnCancelBooking.setOnClickListener(v -> {
            // Отменяем запись
            finish(); // Просто закрываем экран
        });
    }
}
