package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;  // <— импорт для использования View.GONE
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManageAppointmentActivity extends AppCompatActivity {

    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvCurrentDateTime, tvAbout;
    private Button btnReschedule, btnCancelExisting;

    private String doctorName, doctorSpecialty, doctorPhoto;
    private String currentDateTime, doctorAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment);

        imgDoctorPhoto    = findViewById(R.id.imgManageDoctorPhoto);
        tvDoctorName      = findViewById(R.id.tvManageDoctorName);
        tvCurrentDateTime = findViewById(R.id.tvManageCurrentDateTime);
        tvAbout           = findViewById(R.id.tvManageAbout);
        btnReschedule     = findViewById(R.id.btnReschedule);
        btnCancelExisting = findViewById(R.id.btnCancelExisting);

        // Получаем данные из Intent
        Intent intent = getIntent();
        doctorName      = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto     = intent.getStringExtra("doctor_photo");    // например "billie.png"
        currentDateTime = intent.getStringExtra("current_date_time");
        doctorAbout     = intent.getStringExtra("doctor_about");    // может быть null

        // Заполняем поля
        tvDoctorName.setText(doctorName);
        tvCurrentDateTime.setText("Текущий приём: " + currentDateTime);

        // Если передано описание врача, показываем, иначе скрываем
        if (doctorAbout != null) {
            tvAbout.setText(doctorAbout);
            tvAbout.setVisibility(View.VISIBLE);
        } else {
            tvAbout.setVisibility(View.GONE);
        }

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

        // Кнопка «Перенести запись»
        btnReschedule.setOnClickListener(v -> {
            // 1) Удаляем текущую запись из Firestore/локально
            AppointmentManager.getInstance().removeAppointment(doctorName);

            // 2) Переходим в AppointmentActivity для переноса (skipDateTime = текущий слот)
            Intent i = new Intent(ManageAppointmentActivity.this, AppointmentActivity.class);
            i.putExtra("doctor_name", doctorName);
            i.putExtra("doctor_specialty", doctorSpecialty);
            i.putExtra("doctor_photo", doctorPhoto);
            i.putExtra("skip_date_time", currentDateTime); // чтобы скрыть эту опцию
            i.putExtra("doctor_about", doctorAbout);
            startActivity(i);
            finish();
        });

        // Кнопка «Отменить запись»
        btnCancelExisting.setOnClickListener(v -> {
            AppointmentManager.getInstance().removeAppointment(doctorName);
            Toast.makeText(this, "Запись отменена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ManageAppointmentActivity.this, HomeActivity.class));
            finish();
        });

        // BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ManageAppointmentActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ManageAppointmentActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
