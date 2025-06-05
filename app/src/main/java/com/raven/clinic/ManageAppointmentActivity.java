package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;  // импорт для использования View.GONE
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * ManageAppointmentActivity показывает пользователю их текущую запись к врачу.
 * Позволяет либо перенести запись (переход на AppointmentActivity с параметром skip_date_time),
 * либо полностью отменить запись (возврат на HomeActivity).
 */
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

        // Получаем данные из Intent, переданные при переходе сюда (HomeActivity или AppointmentActivity)
        Intent intent = getIntent();
        doctorName      = intent.getStringExtra("doctor_name");
        doctorSpecialty = intent.getStringExtra("doctor_specialty");
        doctorPhoto     = intent.getStringExtra("doctor_photo");       // например "billie.png"
        currentDateTime = intent.getStringExtra("current_date_time");  // существующая дата/время
        doctorAbout     = intent.getStringExtra("doctor_about");       // может быть null

        // Заполняем поля на экране
        tvDoctorName.setText(doctorName);
        tvCurrentDateTime.setText("Текущий приём: " + currentDateTime);

        // Если передано описание врача (about), показываем его, иначе скрываем TextView
        if (doctorAbout != null) {
            tvAbout.setText(doctorAbout);
            tvAbout.setVisibility(View.VISIBLE);
        } else {
            tvAbout.setVisibility(View.GONE);
        }

        // Устанавливаем фотографию врача (если есть ресурс, иначе заглушка)
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

        // Обработчик «Перенести запись»
        btnReschedule.setOnClickListener(v -> {
            // Удаляем текущую запись локально (через AppointmentManager) —
            // если вы всё ещё храните в локальном списке AppointmentManager
            AppointmentManager.getInstance().removeAppointment(doctorName);

            // И одновременно можем удалить её из Realtime Database или Firestore,
            // если вы ранее писали туда. Но так как для этой задачи вы используете RTDB,
            // то удаление будет происходить в MyAppointmentsActivity или в HomeActivity →
            // там, куда вы перенаправляете. Здесь оставляем удаление локально.

            // Переходим на AppointmentActivity для переноса (skip_date_time = текущий слот)
            Intent i = new Intent(ManageAppointmentActivity.this, AppointmentActivity.class);
            i.putExtra("doctor_name", doctorName);
            i.putExtra("doctor_specialty", doctorSpecialty);
            i.putExtra("doctor_photo", doctorPhoto);
            i.putExtra("skip_date_time", currentDateTime); // чтобы скрыть текущую дату/время
            i.putExtra("doctor_about", doctorAbout);
            startActivity(i);
            finish();
        });

        // Обработчик «Отменить запись»
        btnCancelExisting.setOnClickListener(v -> {
            // Удаляем текущую запись локально
            AppointmentManager.getInstance().removeAppointment(doctorName);

            // Также нужно удалить узел из Realtime Database, если вы его туда ранее добавили.
            // Предположим, вы использовали ключ safeDoctorKey = doctorName.replaceAll("[.#$\\[\\]]", "_"),
            // то код удаления из RTDB выглядит так:
            // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // if (user != null) {
            //     String uid = user.getUid();
            //     String safeKey = doctorName.replaceAll("[.#$\\[\\]]", "_");
            //     FirebaseDatabase.getInstance().getReference()
            //         .child("appointments")
            //         .child(uid)
            //         .child(safeKey)
            //         .removeValue();
            // }

            Toast.makeText(this, "Запись отменена", Toast.LENGTH_SHORT).show();
            // Возвращаемся на главный экран
            startActivity(new Intent(ManageAppointmentActivity.this, HomeActivity.class));
            finish();
        });

        // Нижнее меню навигации
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
