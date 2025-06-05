package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManageAppointmentActivity extends AppCompatActivity {

    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvCurrentDateTime, tvAbout;
    private Button btnReschedule;

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
        // Убираем логику по btnCancelExisting, так как сам элемент удалён из макета

        // Получаем данные из Intent
        Intent intent = getIntent();
        doctorName      = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto     = intent.getStringExtra("doctor_photo");
        currentDateTime = intent.getStringExtra("current_date_time");
        doctorAbout     = intent.getStringExtra("doctor_about");

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
            // 1) Удаляем текущую запись локально (через менеджер), а в вашей логике Firestore/RTDB удалять нужно отдельно, если было
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
