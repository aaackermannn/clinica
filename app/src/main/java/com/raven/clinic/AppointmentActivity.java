package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty;
    private RadioGroup rgDateTimeOptions;
    private Button btnConfirm;

    private String doctorName, doctorSpecialty, doctorPhoto;
    private String skipDateTime; // Если переходим с ManageAppointment, то этот слот нужно скрыть

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        btnBack = findViewById(R.id.btnBack);
        imgDoctorPhoto = findViewById(R.id.imgAppointmentDoctorPhoto);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        rgDateTimeOptions = findViewById(R.id.rgDateTimeOptions);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Получаем данные из Intent
        Intent intent = getIntent();
        doctorName      = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto     = intent.getStringExtra("doctor_photo");
        skipDateTime    = intent.getStringExtra("skip_date_time"); // может быть null

        // Заполняем текст и фото врача
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

        // Доступные варианты слотов (жёстко прописаны, но в будущем можно вытянуть из БД)
        List<String> allSlots = new ArrayList<>();
        allSlots.add("30 июня 2025, 09:00");
        allSlots.add("01 июля 2025, 11:30");
        allSlots.add("03 июля 2025, 15:00");

        // Если skipDateTime != null, убираем эту опцию
        if (skipDateTime != null) {
            allSlots.remove(skipDateTime);
        }

        // Теперь динамически заполняем радиокнопки
        rgDateTimeOptions.removeAllViews();
        for (String slot : allSlots) {
            RadioButton rb = new RadioButton(this);
            rb.setText(slot);
            rb.setTextColor(getResources().getColor(R.color.white));
            rb.setButtonTintList(getResources().getColorStateList(R.color.primary));
            // Нужны отступы:
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, dpToPx(8));
            rb.setLayoutParams(lp);
            rgDateTimeOptions.addView(rb);
        }

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            int checkedId = rgDateTimeOptions.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRb = findViewById(checkedId);
            String dateTime = selectedRb.getText().toString();

            // Сохраняем новую запись
            AppointmentManager.Appointment appt = new AppointmentManager.Appointment(
                    doctorName, doctorSpecialty, dateTime, doctorPhoto
            );
            AppointmentManager.getInstance().addOrUpdateAppointment(appt);

            // Переходим на ConfirmationActivity
            Intent i = new Intent(AppointmentActivity.this, ConfirmationActivity.class);
            i.putExtra("doctor_name", doctorName);
            i.putExtra("doctor_specialty", doctorSpecialty);
            i.putExtra("doctor_photo", doctorPhoto);
            i.putExtra("date_time", dateTime);
            startActivity(i);
            finish();
        });

        // BottomNav
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

    // Утилита для конвертации dp → px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}







