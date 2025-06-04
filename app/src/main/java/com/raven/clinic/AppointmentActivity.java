package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;  // <— импорт для использования View.GONE
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgDoctorPhoto;
    private TextView tvDoctorName, tvSpecialty, tvAbout;
    private RadioGroup rgDateTimeOptions;
    private Button btnConfirm;

    private String doctorName, doctorSpecialty, doctorPhoto;
    private String skipDateTime; // Если переходим с ManageAppointment
    private String doctorAbout;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Инициализируем Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        doctorAbout     = intent.getStringExtra("doctor_about");    // может быть null

        // Заполняем текст, фото, описание врача
        tvDoctorName.setText(doctorName);
        tvSpecialty.setText(doctorSpecialty);
        int resIdPhoto = getResources().getIdentifier(
                doctorPhoto.replace(".png", ""),
                "drawable",
                getPackageName()
        );
        if (resIdPhoto != 0) {
            imgDoctorPhoto.setImageResource(resIdPhoto);
        } else {
            imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
        }

        // Если пришло описание «about», отображаем TextView, иначе скрываем
        tvAbout = findViewById(R.id.tvAboutDoctor);
        if (doctorAbout != null) {
            tvAbout.setText(doctorAbout);
            tvAbout.setVisibility(View.VISIBLE);
        } else {
            tvAbout.setVisibility(View.GONE);
        }

        // Доступные варианты слотов (жёстко прописаны)
        List<String> allSlots = new ArrayList<>();
        allSlots.add("30 июня 2025, 09:00");
        allSlots.add("01 июля 2025, 11:30");
        allSlots.add("03 июля 2025, 15:00");

        // Если skipDateTime != null, убираем эту опцию
        if (skipDateTime != null) {
            allSlots.remove(skipDateTime);
        }

        // Динамически заполняем радиокнопки
        rgDateTimeOptions.removeAllViews();
        for (String slot : allSlots) {
            RadioButton rb = new RadioButton(this);
            rb.setText(slot);
            rb.setTextColor(getResources().getColor(R.color.white));
            rb.setButtonTintList(getResources().getColorStateList(R.color.primary));
            // Отступы:
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

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Сначала войдите в учётную запись", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = user.getUid();

            // Подготовим данные для записи
            Map<String, Object> apptMap = new HashMap<>();
            apptMap.put("doctorName", doctorName);
            apptMap.put("specialty", doctorSpecialty);
            apptMap.put("dateTime", dateTime);
            apptMap.put("doctorPhoto", doctorPhoto);

            // Сохраняем новую запись в Firestore → appointments/{uid}/user_appointments
            db.collection("appointments")
                    .document(uid)
                    .collection("user_appointments")
                    .document(doctorName) // имя врача как ключ
                    .set(apptMap)
                    .addOnSuccessListener(aVoid -> {
                        // Переходим на ConfirmationActivity
                        Intent i = new Intent(AppointmentActivity.this, ConfirmationActivity.class);
                        i.putExtra("doctor_name", doctorName);
                        i.putExtra("doctor_specialty", doctorSpecialty);
                        i.putExtra("doctor_photo", doctorPhoto);
                        i.putExtra("date_time", dateTime);
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Не удалось сохранить запись: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        // BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
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
