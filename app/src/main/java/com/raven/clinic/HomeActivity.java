package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;
    private Button btnMyAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnMyAppointments.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyAppointmentsActivity.class));
        });

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        // Четыре врача с фотографиями (файлы должны лежать в res/drawable: billie.png, anthony.png, miron.png, skala.png)
        List<Doctor> doctors = Arrays.asList(
                new Doctor("Billi Ailish", "Педиатр", "billie"),
                new Doctor("Anthony", "Спортивный врач", "anthony"),
                new Doctor("Мирон Федоров", "Детский логопед", "miron"),
                new Doctor("Скала", "Уролог", "skala")
        );

        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        // Сразу отмечаем «Домой»
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // уже здесь
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    // Класс "Doctor"
    private static class Doctor {
        String name;
        String specialty;
        String photoResName; // например, "billie" (без .png)

        Doctor(String name, String specialty, String photoResName) {
            this.name = name;
            this.specialty = specialty;
            this.photoResName = photoResName;
        }
    }

    // Адаптер списка врачей
    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

        private final List<Doctor> doctors;

        DoctorsAdapter(List<Doctor> doctors) {
            this.doctors = doctors;
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            Doctor doctor = doctors.get(position);

            holder.tvDoctorName.setText(doctor.name);
            holder.tvSpecialty.setText(doctor.specialty);

            // Загружаем фото из drawable по имени (например, billie.png → R.drawable.billie)
            int resId = getResources().getIdentifier(
                    doctor.photoResName,
                    "drawable",
                    getPackageName()
            );
            if (resId != 0) {
                holder.imgDoctorPhoto.setImageResource(resId);
            } else {
                holder.imgDoctorPhoto.setImageResource(R.drawable.doctor_placeholder);
            }

            // При клике на карточку: либо запись, либо управление имеющейся записью
            holder.container.setOnClickListener(v -> {
                AppointmentManager.Appointment existing =
                        AppointmentManager.getInstance().getAppointmentForDoctor(doctor.name);
                if (existing == null) {
                    Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                    intent.putExtra("doctor_name", doctor.name);
                    intent.putExtra("doctor_specialty", doctor.specialty);
                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, ManageAppointmentActivity.class);
                    intent.putExtra("doctor_name", doctor.name);
                    intent.putExtra("doctor_specialty", doctor.specialty);
                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                    intent.putExtra("current_date_time", existing.dateTime);
                    startActivity(intent);
                }
            });

            // Кнопка «Записаться» всегда ведёт к созданию новой записи (если её нет)
            holder.btnBook.setOnClickListener(v -> {
                AppointmentManager.Appointment existing =
                        AppointmentManager.getInstance().getAppointmentForDoctor(doctor.name);
                if (existing == null) {
                    Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                    intent.putExtra("doctor_name", doctor.name);
                    intent.putExtra("doctor_specialty", doctor.specialty);
                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                    startActivity(intent);
                } else {
                    // Если запись уже есть, переходим в ManageAppointment
                    Intent intent = new Intent(HomeActivity.this, ManageAppointmentActivity.class);
                    intent.putExtra("doctor_name", doctor.name);
                    intent.putExtra("doctor_specialty", doctor.specialty);
                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                    intent.putExtra("current_date_time", existing.dateTime);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }

        class DoctorViewHolder extends RecyclerView.ViewHolder {
            View container;
            ImageView imgDoctorPhoto;
            TextView tvDoctorName, tvSpecialty, tvAbout;
            Button btnBook;

            DoctorViewHolder(@NonNull View itemView) {
                super(itemView);
                container       = itemView.findViewById(R.id.container_doctor);
                imgDoctorPhoto  = itemView.findViewById(R.id.imgDoctorPhoto);
                tvDoctorName    = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty     = itemView.findViewById(R.id.tvSpecialty);
                tvAbout         = itemView.findViewById(R.id.tvAboutDoctor);
                btnBook         = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}
