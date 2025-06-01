package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        // Получаем список всех врачей (четыре: Billi Ailish, Anthony, Мирон Федоров, Скала)
        List<Doctor> doctors = getAllDoctors();
        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);

        setupBottomNavigation();
    }

    private List<Doctor> getAllDoctors() {
        return java.util.Arrays.asList(
                new Doctor("Billi Ailish", "Педиатр", "billie"),      // имя drawable — billie.png
                new Doctor("Anthony", "Спортивный врач", "anthony"),  // anthony.png
                new Doctor("Мирон Федоров", "Детский логопед", "miron"),
                new Doctor("Скала", "Уролог", "skala")
        );
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // уже здесь
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    // Внутренний POJO для врача
    private static class Doctor {
        String name;
        String specialty;
        String photoResName; // без расширения ".png"

        Doctor(String name, String specialty, String photoResName) {
            this.name = name;
            this.specialty = specialty;
            this.photoResName = photoResName;
        }
    }

    // Адаптер для списка врачей
    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {
        private final List<Doctor> doctors;

        DoctorsAdapter(List<Doctor> doctors) {
            this.doctors = doctors;
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            Doctor doctor = doctors.get(position);
            holder.tvDoctorName.setText(doctor.name);
            holder.tvSpecialty.setText(doctor.specialty);

            // Загружаем фото из drawable по имени
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

            holder.itemView.setOnClickListener(v -> {
                // 1) Проверяем, есть ли запись к этому врачу
                AppointmentManager.Appointment existing =
                        AppointmentManager.getInstance().getAppointmentForDoctor(doctor.name);

                if (existing == null) {
                    // Нет записи — переходим на AppointmentActivity, чтобы создать новую
                    Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                    intent.putExtra("doctor_name", doctor.name);
                    intent.putExtra("doctor_specialty", doctor.specialty);
                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                    startActivity(intent);
                } else {
                    // Уже есть запись — показываем экран управления записью
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
            android.widget.TextView tvDoctorName, tvSpecialty;
            android.widget.ImageView imgDoctorPhoto;

            DoctorViewHolder(@NonNull android.view.View itemView) {
                super(itemView);
                imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
                tvDoctorName   = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty    = itemView.findViewById(R.id.tvSpecialty);
            }
        }
    }
}





