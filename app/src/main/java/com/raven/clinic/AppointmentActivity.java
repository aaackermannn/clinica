package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppointmentActivity extends AppCompatActivity {

    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty;
    private RadioGroup rgDateTimeOptions;
    private Button btnConfirm;

    private String doctorName, doctorSpecialty, doctorPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // 1) Получаем переданные данные
        Intent intent = getIntent();
        doctorName = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto = intent.getStringExtra("doctor_photo");

        // 2) Находим View
        imgDoctorPhoto = findViewById(R.id.imgAppointmentDoctorPhoto);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        rgDateTimeOptions = findViewById(R.id.rgDateTimeOptions);
        btnConfirm = findViewById(R.id.btnConfirm);

        // 3) Устанавливаем данные в UI
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);

        // Загружаем фото врача
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

        // 4) Варианты выбора даты/времени уже заданы в макете (rbOption1, rbOption2, rbOption3)
        // Если нужно поменять тексты программно, можно сделать так:
        RadioButton rb1 = findViewById(R.id.rbOption1);
        RadioButton rb2 = findViewById(R.id.rbOption2);
        RadioButton rb3 = findViewById(R.id.rbOption3);

        // Чтобы сделать уникальные варианты для каждого врача, можно сверять doctorName:
        if (doctorName.equals("Доктор Шон Мерфи")) {
            rb1.setText("30 июня 2025, 09:00");
            rb2.setText("01 июля 2025, 11:30");
            rb3.setText("03 июля 2025, 15:00");
        }
        else if (doctorName.equals("Антонио Бандерас")) {
            rb1.setText("28 июня 2025, 10:00");
            rb2.setText("29 июня 2025, 14:45");
            rb3.setText("02 июля 2025, 16:00");
        }
        // и так далее для остальных врачей...
        else {
            // по умолчанию оставляются тексты из XML
        }

        // 5) Обработка кнопки «Записаться»
        btnConfirm.setOnClickListener(v -> {
            int selectedId = rgDateTimeOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRb = findViewById(selectedId);
            String dateTime = selectedRb.getText().toString();

            // Переходим на ConfirmationActivity, передаём дату/время
            Intent confirmIntent = new Intent(AppointmentActivity.this, ConfirmationActivity.class);
            confirmIntent.putExtra("doctor_name", doctorName);
            confirmIntent.putExtra("doctor_specialty", doctorSpecialty);
            confirmIntent.putExtra("doctor_photo", doctorPhoto);
            confirmIntent.putExtra("date_time", dateTime);
            startActivity(confirmIntent);
            finish();
        });
    }
}



