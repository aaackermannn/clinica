package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppointmentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty;
    private RadioGroup rgDateTimeOptions;
    private Button btnConfirm;

    private String doctorName, doctorSpecialty, doctorPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // 1) Инициализация View
        btnBack = findViewById(R.id.btnBack);
        imgDoctorPhoto = findViewById(R.id.imgAppointmentDoctorPhoto);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        rgDateTimeOptions = findViewById(R.id.rgDateTimeOptions);
        btnConfirm = findViewById(R.id.btnConfirm);

        // 2) Получаем данные о враче из Intent
        Intent intent = getIntent();
        doctorName = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto = intent.getStringExtra("doctor_photo"); // например "billie.png"

        // Устанавливаем текст и фото врача
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
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

        // 3) Настраиваем варианты радиокнопок в зависимости от врача
        RadioButton rb1 = findViewById(R.id.rbOption1);
        RadioButton rb2 = findViewById(R.id.rbOption2);
        RadioButton rb3 = findViewById(R.id.rbOption3);
        switch (doctorName) {
            case "Billi Ailish":
                rb1.setText("30 июня 2025, 09:00");
                rb2.setText("01 июля 2025, 11:30");
                rb3.setText("03 июля 2025, 15:00");
                break;
            case "Anthony":
                rb1.setText("28 июня 2025, 10:00");
                rb2.setText("29 июня 2025, 14:45");
                rb3.setText("02 июля 2025, 16:00");
                break;
            case "Мирон Федоров":
                rb1.setText("27 июня 2025, 10:15");
                rb2.setText("29 июня 2025, 12:00");
                rb3.setText("04 июля 2025, 14:00");
                break;
            case "Скала":
                rb1.setText("26 июня 2025, 09:30");
                rb2.setText("28 июня 2025, 11:00");
                rb3.setText("30 июня 2025, 13:45");
                break;
        }

        // 4) Обработка кнопки Назад
        btnBack.setOnClickListener(v -> finish());

        // 5) Кнопка «Записаться»
        btnConfirm.setOnClickListener(v -> {
            int selectedId = rgDateTimeOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRb = findViewById(selectedId);
            String dateTime = selectedRb.getText().toString();

            // Добавляем запись в одиночный менеджер
            AppointmentManager.Appointment appt = new AppointmentManager.Appointment(
                    doctorName, doctorSpecialty, dateTime, doctorPhoto
            );
            AppointmentManager.getInstance().addAppointment(appt);

            // Переходим на ConfirmationActivity
            Intent confirmIntent = new Intent(AppointmentActivity.this, ConfirmationActivity.class);
            confirmIntent.putExtra("doctor_name", doctorName);
            confirmIntent.putExtra("doctor_specialty", doctorSpecialty);
            confirmIntent.putExtra("doctor_photo", doctorPhoto);
            confirmIntent.putExtra("date_time", dateTime);
            startActivity(confirmIntent);
            finish();
        });

        // 6) BottomNavigationView: Домой и Профиль
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(AppointmentActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(AppointmentActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}






