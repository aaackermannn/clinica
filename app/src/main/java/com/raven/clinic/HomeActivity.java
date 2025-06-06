package com.raven.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * HomeActivity показывает список врачей и позволяет записаться к выбранному врачу.
 * При клике на врача проверяется наличие записи в Firebase Realtime Database:
 *   – если запись найдена → ManageAppointmentActivity;
 *   – если нет        → AppointmentActivity.
 */
public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvDoctors;
    private DoctorsAdapter adapter;
    private Button btnMyAppointments;

    // Firebase Auth
    private FirebaseAuth mAuth;
    // Realtime Database
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Инициализируем FirebaseAuth и Realtime Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnMyAppointments.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyAppointmentsActivity.class));
        });

        rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));

        // Четыре врача с их фотографиями и описаниями (жёстко прописано)
        List<Doctor> doctors = Arrays.asList(
                new Doctor("Billi Ailish", "Педиатр", "billie",
                        "Билли Айлеш — опытный педиатр с 10-летним стажем, специализируется на детских вирусных заболеваниях."),
                new Doctor("Anthony", "Спортивный врач", "anthony",
                        "Anthony — врач спортивной медицины, работает с профессиональными и любительскими спортсменами."),
                new Doctor("Мирон Федоров", "Детский логопед", "miron",
                        "Мирон Федоров имеет 15 лет опыта в логопедии, помогает детям корректировать речь."),
                new Doctor("Скала", "Уролог", "skala",
                        "«Скала» — ведущий уролог, специализируется на лечении камней в почках и профилактике заболеваний.")
        );

        adapter = new DoctorsAdapter(doctors);
        rvDoctors.setAdapter(adapter);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        // Отмечаем, что мы находимся на «Домой»
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // Уже здесь
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Внутренний класс Doctor — модель данных врача
    private static class Doctor {
        String name;
        String specialty;
        String photoResName; // например, "billie" (без .png)
        String about;        // описание «О враче»

        Doctor(String name, String specialty, String photoResName, String about) {
            this.name = name;
            this.specialty = specialty;
            this.photoResName = photoResName;
            this.about = about;
        }
    }

    // Адаптер для RecyclerView
    private class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

        private final List<Doctor> doctors;

        DoctorsAdapter(List<Doctor> doctors) {
            this.doctors = doctors;
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            Doctor doctor = doctors.get(position);

            holder.tvDoctorName.setText(doctor.name);
            holder.tvSpecialty.setText(doctor.specialty);
            holder.tvAbout.setText(doctor.about);

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

            // При клике на карточку врача
            holder.container.setOnClickListener(v -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(HomeActivity.this,
                            "Сначала войдите в учётную запись", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();
                // Получаем «безопасный» ключ врача для RTDB:
                String safeKey = doctor.name.replaceAll("[.#$\\[\\]]", "_");

                // Проверяем, есть ли запись в Realtime Database по пути appointments/{uid}/{safeKey}
                database.child("appointments")
                        .child(uid)
                        .child(safeKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Если запись найдена, открываем ManageAppointmentActivity
                                    String existingDateTime = snapshot.child("dateTime")
                                            .getValue(String.class);

                                    Intent intent = new Intent(HomeActivity.this, ManageAppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("current_date_time", existingDateTime);
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                } else {
                                    // Если записи нет — открываем экран записи (AppointmentActivity)
                                    Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(HomeActivity.this,
                                        "Ошибка проверки записи: " + error.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            // При клике на кнопку «Записаться» внутри карточки
            holder.btnBook.setOnClickListener(v -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(HomeActivity.this,
                            "Сначала войдите в учётную запись", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();
                String safeKey = doctor.name.replaceAll("[.#$\\[\\]]", "_");

                database.child("appointments")
                        .child(uid)
                        .child(safeKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Если запись уже есть — ManageAppointmentActivity
                                    String existingDateTime = snapshot.child("dateTime")
                                            .getValue(String.class);

                                    Intent intent = new Intent(HomeActivity.this, ManageAppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("current_date_time", existingDateTime);
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                } else {
                                    // Иначе — AppointmentActivity
                                    Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
                                    intent.putExtra("doctor_name", doctor.name);
                                    intent.putExtra("doctor_specialty", doctor.specialty);
                                    intent.putExtra("doctor_photo", doctor.photoResName + ".png");
                                    intent.putExtra("doctor_about", doctor.about);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(HomeActivity.this,
                                        "Ошибка проверки записи: " + error.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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
                container = itemView.findViewById(R.id.container_doctor);
                imgDoctorPhoto = itemView.findViewById(R.id.imgDoctorPhoto);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
                tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
                tvAbout = itemView.findViewById(R.id.tvAbout);
                btnBook = itemView.findViewById(R.id.btnBook);
            }
        }
    }
}
